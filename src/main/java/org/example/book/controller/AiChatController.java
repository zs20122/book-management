package org.example.book.controller;

import org.example.book.service.BookQueryTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final ChatClient chatClient;
    private final BookQueryTools bookQueryTools;

    public AiChatController(ChatClient.Builder builder, BookQueryTools bookQueryTools) {
        this.chatClient = builder
                .defaultSystem("你是一个图书管理系统的智能助手。你的职责是回答用户关于系统内图书和借阅情况的问题。" +
                        "你可以查询图书库存，也可以查询未归还的图书列表。" +
                        "如果用户问未归还的书，请直接调用 getUnreturnedBooks 工具，不要凭猜测回答。" +
                        "如果用户问某本书的库存，请调用 getBookStock 工具。")
                .build();
        this.bookQueryTools = bookQueryTools;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .tools(bookQueryTools)
                .call()
                .content();
    }
}