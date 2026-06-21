package org.example.book.service;

import org.example.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    Page<Book> findAll(Pageable pageable);
    Book findById(Long id);
    Book save(Book book);
    Book update(Long id, Book book);
    void delete(Long id);
    List<Book> search(String keyword);
}