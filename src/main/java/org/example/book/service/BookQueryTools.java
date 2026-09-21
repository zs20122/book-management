package org.example.book.service;

import org.example.book.entity.Borrow;
import org.example.book.repository.BookRepository;
import org.example.book.repository.BorrowRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookQueryTools {

    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;

    public BookQueryTools(BookRepository bookRepository, BorrowRepository borrowRepository) {
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
    }

    @Tool(description = "根据书名查询库存数量，返回格式如：红楼梦 剩余 5 本")
    public String getBookStock(@ToolParam(description = "书名，例如：红楼梦") String title) {
        return bookRepository.findByTitleContaining(title).stream()
                .map(b -> b.getTitle() + " 剩余 " + b.getStock() + " 本")
                .findFirst()
                .orElse("未找到该图书");
    }

    @Tool(description = "查询所有未归还的图书列表（包含借阅人），无需参数，直接调用即可获得结果")
    public String getUnreturnedBooks() {
        List<Borrow> borrows = borrowRepository.findByStatus("BORROWING");
        if (borrows == null || borrows.isEmpty()) {
            return "目前系统里没有未归还的图书。";
        }
        return borrows.stream()
                .map(b -> "《" + b.getBook().getTitle() + "》（借阅人：" + b.getUser().getUsername() + "）")
                .collect(Collectors.joining("，"));
    }
}