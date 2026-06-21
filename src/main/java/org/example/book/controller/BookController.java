package org.example.book.controller;

import org.example.book.dto.ApiResponse;
import org.example.book.entity.Book;
import org.example.book.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public ApiResponse<List<Book>> getBooks() {
        List<Book> books = bookRepository.findAll();
        System.out.println("查询到图书数量: " + books.size());
        return ApiResponse.success(books);
    }

    @GetMapping("/{id}")
    public ApiResponse<Book> getBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
        return ApiResponse.success(book);
    }

    @PostMapping
    public ApiResponse<Book> addBook(@RequestBody Book book) {
        Book saved = bookRepository.save(book);
        return ApiResponse.success("添加成功", saved);
    }

    @PutMapping("/{id}")
    public ApiResponse<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setIsbn(book.getIsbn());
        existing.setPublisher(book.getPublisher());
        existing.setStock(book.getStock());
        existing.setTotal(book.getTotal());
        existing.setDescription(book.getDescription());
        Book updated = bookRepository.save(existing);
        return ApiResponse.success("更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}