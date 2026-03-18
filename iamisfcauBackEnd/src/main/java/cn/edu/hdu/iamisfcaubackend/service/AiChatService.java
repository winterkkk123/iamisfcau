package cn.edu.hdu.iamisfcaubackend.service;

import cn.edu.hdu.iamisfcaubackend.dto.AiChatRequest;
import cn.edu.hdu.iamisfcaubackend.dto.AiChatResponse;
import cn.edu.hdu.iamisfcaubackend.entity.AiConversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    private final ChatClient toolChatClient;
    private final ChatClient plainChatClient;
    private final AiConversationService aiConversationService;

    @Value("${app.ai.tools-enabled:true}")
    private boolean toolsEnabled;

    public AiChatService(ChatClient.Builder chatClientBuilder,
                         AiConversationService aiConversationService,
                         CampusAiToolService campusAiToolService) {
        this.toolChatClient = chatClientBuilder
                .defaultTools(campusAiToolService)
                .build();

        this.plainChatClient = chatClientBuilder.build();

        this.aiConversationService = aiConversationService;
    }

    public AiChatResponse chat(AiChatRequest request) {
        if (request == null
                || request.conversationId() == null || request.conversationId().isBlank()
                || request.message() == null || request.message().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "conversationId 和 message 不能为空");
        }

        log.info("AI chat request start, conversationId={}, userId={}, toolsEnabled={}, message={}",
                request.conversationId(), request.userId(), toolsEnabled, request.message());

        aiConversationService.ensureConversationExists(request.conversationId());
        aiConversationService.saveUserMessage(request.conversationId(), request.message());
        aiConversationService.updateTitleIfNeeded(request.conversationId(), request.message());

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(buildSystemPrompt(request.userId(), request.userName(), toolsEnabled)));
        messages.addAll(aiConversationService.buildRecentPromptMessages(request.conversationId()));

        String answer;
        Instant start = Instant.now();

        try {
            Prompt prompt = new Prompt(messages);

            log.info("Before calling Ollama, messageCount={}, toolsEnabled={}", messages.size(), toolsEnabled);

            ChatClient currentClient = toolsEnabled ? toolChatClient : plainChatClient;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                Future<String> future = executor.submit(() ->
                        currentClient.prompt(prompt).call().content()
                );

                answer = future.get(20, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                log.error("AI chat timeout, costMs={}, toolsEnabled={}",
                        Duration.between(start, Instant.now()).toMillis(), toolsEnabled, e);
                answer = "抱歉，AI 响应超时，请检查 Ollama 模型是否卡住，或先更换一个更稳定的模型。";
            } finally {
                executor.shutdownNow();
            }

            log.info("After calling Ollama, costMs={}, toolsEnabled={}",
                    Duration.between(start, Instant.now()).toMillis(), toolsEnabled);

        } catch (Exception e) {
            log.error("AI chat failed, costMs={}, toolsEnabled={}",
                    Duration.between(start, Instant.now()).toMillis(), toolsEnabled, e);
            answer = "抱歉，AI 调用失败：" + e.getClass().getSimpleName() + " - " + e.getMessage();
        }

        if (answer == null || answer.isBlank()) {
            answer = "抱歉，这次我没有生成有效回复。";
        }

        aiConversationService.saveAssistantMessage(request.conversationId(), answer);

        AiConversation conversation = aiConversationService.getConversation(request.conversationId());
        return new AiChatResponse(
                conversation.getId(),
                conversation.getTitle(),
                answer
        );
    }

    private String buildSystemPrompt(String userId, String userName, boolean toolsEnabled) {
        String currentUserId = (userId == null || userId.isBlank()) ? "unknown" : userId;
        String currentUserName = (userName == null || userName.isBlank()) ? "unknown" : userName;

        if (!toolsEnabled) {
            return """
                    你是“高校智能助手”。

                    当前登录用户：
                    - userId: %s
                    - userName: %s

                    现在处于纯对话测试模式：
                    1. 不要调用任何工具。
                    2. 直接用中文简洁回答。
                    3. 如果是需要查询数据库的问题，就说明“当前处于测试模式，暂不查库”。

                    回答要简短。
                    """.formatted(currentUserId, currentUserName);
        }

        return """
                你是“高校智能助手”。

                当前登录用户：
                - userId: %s
                - userName: %s

                回答规则：
                1. 如果问题和校内系统数据有关，例如活动、通知、社区帖子、请假申请、资料变更申请、出勤签到、我的活动、我的个人资料，优先调用系统已提供的工具查询数据库。
                2. 当用户说“我”“我的”时，默认指当前登录用户。
                3. 只能调用系统实际提供的工具，不能自行编造工具名，不能改写工具名。
                4. 如果工具没有查到数据，要明确说“我没有查到相关数据”，不要编造。
                5. 如果问题是通用内容，例如学习建议、写作润色、代码解释、常识问答、总结改写，可以直接回答。
                6. 回答使用中文，简洁清晰。

                工具调用提示：
                - 查询当前用户资料时，使用 get_my_profile
                - 查询活动列表时，使用 search_activities
                - 查询活动详情时，使用 get_activity_detail
                - 查询我的活动时，使用 list_my_activities
                - 查询通知公告时，使用 search_notices
                - 查询出勤统计时，使用 get_attendance_summary
                - 查询我的申请记录时，使用 get_my_applications
                - 查询社区帖子时，使用 search_posts
                """.formatted(currentUserId, currentUserName);
    }
}