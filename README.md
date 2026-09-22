# 图书管理系统

基于 Spring Boot + Vue3 的前后端分离图书管理系统。

## 🛠️ 技术栈
- **后端**：Java, Spring Boot, MyBatis, MySQL
- **前端**：Vue3, Vite, Element Plus, Axios
- **工具**：IntelliJ IDEA, Git, Postman
- **AI协同**：DeepSeek (用于代码生成、Bug调试及SQL优化)

## ✨ 核心功能
1. **用户认证**：基于 Session 的登录拦截，区分管理员与普通用户权限。
2. **信息管理**：实现图书信息的增删改查，支持分页查询和条件检索。
3. **AI 图书助手**：集成大模型 API，用户可通过自然语言查询图书库存和借阅状态。

## 🚀 AI 图书助手演示
![AI图书助手演示](https://gitee.com/zhangsan220122/book-management/raw/master/images/ai-assistant.png)

## 💻 如何运行
1. 导入 `sql` 文件到 MySQL 数据库。
2. 修改 `application.properties` 中的数据库配置。
3. 运行 `Application.java` 启动项目。