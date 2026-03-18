package cn.edu.hdu.iamisfcaubackend.dev;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 导出接口清单
 * 输出：
 *  - {user.dir}/api-docs/endpoints.md
 *  - {user.dir}/api-docs/endpoints.http   IDEA HTTP Client 可直接运行
 */
@Component
@Profile("dev")
public class EndpointDocExporter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(EndpointDocExporter.class);

    private final RequestMappingHandlerMapping mapping;
    private final DefaultParameterNameDiscoverer paramNameDiscoverer = new DefaultParameterNameDiscoverer();

    private static final String BASE_PACKAGE = "cn.edu.hdu.iamisfcaubackend";

    // 前端联调用的 baseUrl
    private static final String BASE_URL = "http://localhost:8080";

    private static final int EXAMPLE_JSON_MAX_DEPTH = 2;

    public EndpointDocExporter(RequestMappingHandlerMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Endpoint> endpoints = collectEndpoints();

        Path outDir = java.nio.file.Path.of(System.getProperty("user.dir"), "api-docs");
        java.nio.file.Files.createDirectories(outDir);

        java.nio.file.Path mdPath = outDir.resolve("endpoints.md");
        java.nio.file.Path httpPath = outDir.resolve("endpoints.http");

        // 控制台简单输出
        log.info("==== Your Endpoints ({}) ====", endpoints.size());
        for (Endpoint ep : endpoints) {
            log.info("{} {} -> {}", ep.method, ep.path, ep.handler);
        }

        // 写文件
        writeString(mdPath, renderMarkdown(endpoints));
        writeString(httpPath, renderHttpFile(endpoints));

        log.info("Exported: {}", mdPath.toAbsolutePath());
        log.info("Exported: {}", httpPath.toAbsolutePath());
    }

    private List<Endpoint> collectEndpoints() {
        var all = mapping.getHandlerMethods();

        List<Endpoint> result = new ArrayList<>();

        for (var entry : all.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod hm = entry.getValue();

            // 过滤：只保留你业务包下的 Controller
            if (!hm.getBeanType().getPackageName().startsWith(BASE_PACKAGE)) {
                continue;
            }

            // Spring 7 推荐：直接拿“当前生效的 patterns 字符串”，避免 getPatternsCondition() 弃用
            // getPatternValues() 会根据 active patterns condition 返回 patterns。:contentReference[oaicite:1]{index=1}
            List<String> paths = new ArrayList<>(info.getPatternValues());
            if (paths.isEmpty()) paths = List.of("/");

            List<String> methods = info.getMethodsCondition().getMethods().stream()
                    .map(Enum::name).sorted().toList();
            if (methods.isEmpty()) methods = List.of("ANY");

            List<String> consumes = info.getConsumesCondition().getConsumableMediaTypes().stream()
                    .map(MediaType::toString).sorted().toList();

            List<String> produces = info.getProducesCondition().getProducibleMediaTypes().stream()
                    .map(MediaType::toString).sorted().toList();

            List<Param> params = extractParams(hm);
            String returnType = hm.getMethod().getGenericReturnType().getTypeName();

            // 一个 mapping 可能有多个 method / 多个 path：展开为更适合前端对接的一条条 endpoint
            for (String m : methods) {
                for (String p : paths) {
                    result.add(new Endpoint(
                            m,
                            p,
                            consumes,
                            produces,
                            hm.getBeanType().getSimpleName(),
                            hm.getMethod().getName(),
                            hm.getBeanType().getName() + "#" + hm.getMethod().getName(),
                            hm.getMethod().toGenericString(),
                            params,
                            returnType
                    ));
                }
            }
        }

        result.sort(Comparator
                .comparing((Endpoint e) -> e.controller)
                .thenComparing(e -> e.path)
                .thenComparing(e -> e.method));

        return result;
    }

    private List<Param> extractParams(HandlerMethod hm) {
        List<Param> result = new ArrayList<>();
        for (MethodParameter p : hm.getMethodParameters()) {
            p.initParameterNameDiscovery(paramNameDiscoverer);

            // 1) @PathVariable
            PathVariable pv = p.getParameterAnnotation(PathVariable.class);
            if (pv != null) {
                result.add(new Param("path",
                        nameOr(pv.name(), pv.value(), p.getParameterName()),
                        p.getGenericParameterType().getTypeName(),
                        pv.required(),
                        null
                ));
                continue;
            }

            // 2) @RequestParam
            RequestParam rp = p.getParameterAnnotation(RequestParam.class);
            if (rp != null) {
                result.add(new Param("query",
                        nameOr(rp.name(), rp.value(), p.getParameterName()),
                        p.getGenericParameterType().getTypeName(),
                        rp.required(),
                        StringUtils.hasText(rp.defaultValue()) ? rp.defaultValue() : null
                ));
                continue;
            }

            // 3) @RequestHeader
            RequestHeader rh = p.getParameterAnnotation(RequestHeader.class);
            if (rh != null) {
                result.add(new Param("header",
                        nameOr(rh.name(), rh.value(), p.getParameterName()),
                        p.getGenericParameterType().getTypeName(),
                        rh.required(),
                        StringUtils.hasText(rh.defaultValue()) ? rh.defaultValue() : null
                ));
                continue;
            }

            // 4) @RequestBody
            RequestBody rb = p.getParameterAnnotation(RequestBody.class);
            if (rb != null) {
                result.add(new Param("body",
                        "(requestBody)",
                        p.getGenericParameterType().getTypeName(),
                        rb.required(),
                        null
                ));
                continue;
            }

            // 5) 未标注：可能是查询对象/表单对象（也导出，方便前端理解）
            boolean isSpringInternal = p.getParameterType().getName().startsWith("org.springframework");
            if (!isSpringInternal) {
                result.add(new Param("auto",
                        p.getParameterName() != null ? p.getParameterName() : "(unknown)",
                        p.getGenericParameterType().getTypeName(),
                        false,
                        null
                ));
            }
        }
        return result;
    }

    private String renderMarkdown(List<Endpoint> endpoints) {
        StringBuilder sb = new StringBuilder();
        sb.append("# API Endpoints\n\n");
        sb.append("- Export time: ").append(LocalDateTime.now()).append("\n");
        sb.append("- Count: ").append(endpoints.size()).append("\n");
        sb.append("- Base URL: ").append(BASE_URL).append("\n\n");

        Map<String, List<Endpoint>> grouped = new LinkedHashMap<>();
        for (Endpoint e : endpoints) {
            grouped.computeIfAbsent(e.controller, k -> new ArrayList<>()).add(e);
        }

        // 总览（按 Controller）
        sb.append("## Overview\n\n");
        for (var entry : grouped.entrySet()) {
            sb.append("- ").append(entry.getKey())
                    .append(" (").append(entry.getValue().size()).append(")\n");
        }
        sb.append("\n");

        // 详情
        for (var entry : grouped.entrySet()) {
            String controller = entry.getKey();
            List<Endpoint> list = entry.getValue();

            sb.append("## Controller: ").append(controller).append("\n\n");

            // Controller 下的快速表格
            sb.append("| Method | Path | Handler |\n");
            sb.append("|---|---|---|\n");
            for (Endpoint e : list) {
                sb.append("| ").append(e.method).append(" | ")
                        .append(escapeMd(e.path)).append(" | `")
                        .append(escapeMd(e.methodName)).append("` |\n");
            }
            sb.append("\n");

            // 每个接口的详细信息
            for (Endpoint e : list) {
                sb.append("### ").append(e.method).append(" ").append(e.path).append("\n\n");
                sb.append("- Handler: `").append(e.handler).append("`\n");
                sb.append("- Java: `").append(escapeMd(e.javaSignature)).append("`\n");
                sb.append("- Produces: ").append(e.produces.isEmpty() ? "-" : String.join(", ", e.produces)).append("\n");
                sb.append("- Consumes: ").append(e.consumes.isEmpty() ? "-" : String.join(", ", e.consumes)).append("\n");
                sb.append("- Return: `").append(escapeMd(e.returnType)).append("`\n\n");

                // Params 表
                sb.append("#### Params\n\n");
                if (!e.params.isEmpty()) {
                    sb.append("| In | Name | Type | Required | Default |\n");
                    sb.append("|---|---|---|---:|---|\n");
                    for (Param p : e.params) {
                        sb.append("| ").append(p.in).append(" | ")
                                .append(escapeMd(p.name)).append(" | `")
                                .append(escapeMd(p.type)).append("` | ")
                                .append(p.required).append(" | ")
                                .append(p.defaultValue == null ? "-" : escapeMd(p.defaultValue))
                                .append(" |\n");
                    }
                    sb.append("\n");
                } else {
                    sb.append("- (none)\n\n");
                }

                // RequestBody 示例
                String bodyType = firstBodyType(e.params);
                if (bodyType != null) {
                    sb.append("#### Request Body Example (").append(escapeMd(bodyType)).append(")\n\n");
                    sb.append("```json\n");
                    sb.append(buildExampleJson(bodyType));
                    sb.append("\n```\n\n");
                }

                // cURL 模板
                sb.append("#### cURL\n\n");
                sb.append("```bash\n");
                sb.append(buildCurl(e));
                sb.append("\n```\n\n");
            }
        }

        return sb.toString();
    }

    private String renderHttpFile(List<Endpoint> endpoints) {
        StringBuilder sb = new StringBuilder();
        sb.append("@baseUrl = ").append(BASE_URL).append("\n");
        sb.append("# @token = paste_your_token_here\n\n");

        Map<String, List<Endpoint>> grouped = new LinkedHashMap<>();
        for (Endpoint e : endpoints) {
            grouped.computeIfAbsent(e.controller, k -> new ArrayList<>()).add(e);
        }

        for (var entry : grouped.entrySet()) {
            sb.append("### ===================== ").append(entry.getKey()).append(" =====================\n\n");
            for (Endpoint e : entry.getValue()) {
                sb.append("### ").append(e.method).append(" ").append(e.path).append(" (").append(e.methodName).append(")\n");
                sb.append(e.method).append(" {{baseUrl}}").append(e.path).append("\n");

                // 常见 header（按需打开）
                // sb.append("Authorization: Bearer {{token}}\n");
                if (!e.produces.isEmpty()) {
                    sb.append("Accept: ").append(e.produces.get(0)).append("\n");
                }

                String bodyType = firstBodyType(e.params);
                if (bodyType != null) {
                    sb.append("Content-Type: application/json\n\n");
                    sb.append(buildExampleJson(bodyType)).append("\n");
                }

                sb.append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String buildCurl(Endpoint e) {
        StringBuilder sb = new StringBuilder();

        sb.append("curl -X ").append(e.method.equals("ANY") ? "GET" : e.method)
                .append(" \"").append(BASE_URL).append(e.path).append("\"");

        if (!e.produces.isEmpty()) {
            sb.append(" \\\n  -H \"Accept: ").append(e.produces.get(0)).append("\"");
        }

        String bodyType = firstBodyType(e.params);
        if (bodyType != null) {
            sb.append(" \\\n  -H \"Content-Type: application/json\" \\\n  -d '")
                    .append(buildExampleJson(bodyType).replace("\n", " "))
                    .append("'");
        }

        return sb.toString();
    }

    private String firstBodyType(List<Param> params) {
        for (Param p : params) {
            if ("body".equals(p.in)) return p.type;
        }
        return null;
    }

    private String buildExampleJson(String typeName) {
        try {
            Object example = buildExampleObject(typeName, EXAMPLE_JSON_MAX_DEPTH, new HashSet<>());
            return toJson(example, 0);
        } catch (Exception ex) {
            // 反射失败就给个兜底
            return "{ \"TODO\": \"fill request body for type: " + escapeJson(typeName) + "\" }";
        }
    }

    private Object buildExampleObject(String typeName, int depth, Set<String> visiting) throws ClassNotFoundException {
        if (depth <= 0) {
            return "…";
        }

        // 去掉泛型
        String raw = typeName;
        int idx = raw.indexOf('<');
        if (idx > 0) raw = raw.substring(0, idx);
        raw = raw.trim();

        // 数组
        if (raw.endsWith("[]")) {
            String elem = raw.substring(0, raw.length() - 2);
            List<Object> arr = new ArrayList<>();
            arr.add(buildExampleObject(elem, depth - 1, visiting));
            return arr;
        }

        // 常见简单类型
        if (raw.equals("java.lang.String")) return "string";
        if (raw.equals("java.lang.Integer") || raw.equals("int")) return 123;
        if (raw.equals("java.lang.Long") || raw.equals("long")) return 123456789L;
        if (raw.equals("java.lang.Boolean") || raw.equals("boolean")) return true;
        if (raw.equals("java.lang.Double") || raw.equals("double")) return 12.34;
        if (raw.equals("java.math.BigDecimal")) return "12.34";
        if (raw.equals("java.time.LocalDate")) return LocalDate.now().toString();
        if (raw.equals("java.time.LocalDateTime")) return LocalDateTime.now().toString();

        // 容器类型（粗略处理）
        if (raw.startsWith("java.util.List") || raw.startsWith("java.util.Set") || raw.startsWith("java.util.Collection")) {
            return List.of("item");
        }
        if (raw.startsWith("java.util.Map")) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("key", "value");
            return m;
        }

        Class<?> clazz = Class.forName(raw);

        if (clazz.isEnum()) {
            Object[] constants = clazz.getEnumConstants();
            return constants != null && constants.length > 0 ? constants[0].toString() : "ENUM";
        }

        // 防循环
        if (!visiting.add(clazz.getName())) {
            return clazz.getSimpleName();
        }

        // record：用 record components
        if (clazz.isRecord()) {
            Map<String, Object> obj = new LinkedHashMap<>();
            try {
                var comps = clazz.getRecordComponents();
                for (var c : comps) {
                    obj.put(c.getName(), exampleValueForClass(c.getType(), depth - 1, visiting));
                }
            } finally {
                visiting.remove(clazz.getName());
            }
            return obj;
        }

        // 普通 POJO：用字段
        Map<String, Object> obj = new LinkedHashMap<>();
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.isSynthetic()) continue;
                if (Modifier.isStatic(f.getModifiers())) continue;
                if ("serialVersionUID".equals(f.getName())) continue;

                obj.put(f.getName(), exampleValueForClass(f.getType(), depth - 1, visiting));
            }
        } finally {
            visiting.remove(clazz.getName());
        }

        // 如果一个字段都没有（可能全是 getter/setter，字段在父类等），兜底一下
        if (obj.isEmpty()) {
            obj.put("value", clazz.getSimpleName());
        }

        return obj;
    }

    private Object exampleValueForClass(Class<?> type, int depth, Set<String> visiting) {
        try {
            if (type == String.class) return "string";
            if (type == int.class || type == Integer.class) return 123;
            if (type == long.class || type == Long.class) return 123456789L;
            if (type == boolean.class || type == Boolean.class) return true;
            if (type == double.class || type == Double.class) return 12.34;
            if (type == LocalDate.class) return LocalDate.now().toString();
            if (type == LocalDateTime.class) return LocalDateTime.now().toString();

            if (type.isEnum()) {
                Object[] constants = type.getEnumConstants();
                return constants != null && constants.length > 0 ? constants[0].toString() : "ENUM";
            }

            if (type.isArray()) {
                int len = 1;
                List<Object> arr = new ArrayList<>();
                arr.add(exampleValueForClass(type.getComponentType(), depth - 1, visiting));
                // 这里用 List 表示数组更直观
                return arr;
            }

            // 其他复杂类型：尝试递归
            return buildExampleObject(type.getName(), depth, visiting);
        } catch (Exception e) {
            return type.getSimpleName();
        }
    }

    private String toJson(Object obj, int indent) {
        String pad = "  ".repeat(indent);

        if (obj == null) return "null";
        if (obj instanceof String s) return "\"" + escapeJson(s) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();

        if (obj instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            int i = 0;
            for (var en : map.entrySet()) {
                sb.append("  ".repeat(indent + 1))
                        .append("\"").append(escapeJson(String.valueOf(en.getKey()))).append("\": ")
                        .append(toJson(en.getValue(), indent + 1));
                i++;
                if (i < map.size()) sb.append(",");
                sb.append("\n");
            }
            sb.append(pad).append("}");
            return sb.toString();
        }

        if (obj instanceof Collection<?> col) {
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            int i = 0;
            for (Object it : col) {
                sb.append("  ".repeat(indent + 1)).append(toJson(it, indent + 1));
                i++;
                if (i < col.size()) sb.append(",");
                sb.append("\n");
            }
            sb.append(pad).append("]");
            return sb.toString();
        }

        // 兜底：把对象当成字符串输出
        return "\"" + escapeJson(String.valueOf(obj)) + "\"";
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private void writeString(java.nio.file.Path path, String content) throws IOException {
        java.nio.file.Files.writeString(path, content, java.nio.charset.StandardCharsets.UTF_8);
    }

    private String nameOr(String a, String b, String fallback) {
        if (StringUtils.hasText(a)) return a;
        if (StringUtils.hasText(b)) return b;
        return fallback != null ? fallback : "(unknown)";
    }

    private String escapeMd(String s) {
        if (s == null) return "";
        return s.replace("|", "\\|");
    }

    public static class Endpoint {
        public String method;
        public String path;
        public List<String> consumes;
        public List<String> produces;
        public String controller;
        public String methodName;
        public String handler;
        public String javaSignature;
        public List<Param> params;
        public String returnType;

        public Endpoint(String method, String path, List<String> consumes, List<String> produces,
                        String controller, String methodName, String handler, String javaSignature,
                        List<Param> params, String returnType) {
            this.method = method;
            this.path = path;
            this.consumes = consumes != null ? consumes : List.of();
            this.produces = produces != null ? produces : List.of();
            this.controller = controller;
            this.methodName = methodName;
            this.handler = handler;
            this.javaSignature = javaSignature;
            this.params = params != null ? params : List.of();
            this.returnType = returnType;
        }
    }

    public static class Param {
        public String in;          // path/query/header/body/auto
        public String name;
        public String type;
        public boolean required;
        public String defaultValue;

        public Param(String in, String name, String type, boolean required, String defaultValue) {
            this.in = in;
            this.name = name;
            this.type = type;
            this.required = required;
            this.defaultValue = defaultValue;
        }
    }
}