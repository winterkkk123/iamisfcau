## 高校智能助手管理信息系统

### 项目技术栈
- 前端: vue3
- 后端: springboot
- 数据库: mysql
- agent: deepseek-r1:1.5b

### 项目文件结构
```
├── 主文件夹
│   ├── iamisfcauFrontEnd   前端项目
│   ├── iamisfcauBackEnd    后端项目
│   ├── iamisfcauDatabase   数据库
│   └── README.md           项目说明
```

### 项目启动
1. 创建数据库
CREATE DATABASE iamisfcaudatabase;
2. 启动ai模型
ollama run deepseek-r1:1.5b 
3. 启动后端项目
cd iamisfcauBackEnd
4. 启动前端项目
pm run dev
5. 访问http://localhost:8080