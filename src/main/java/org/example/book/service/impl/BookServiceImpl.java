package org.example.book.service.impl;

import org.example.book.entity.Book;
import org.example.book.exception.BusinessException;
import org.example.book.repository.BookRepository;
import org.example.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("图书不存在"));
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book update(Long id, Book book) {
        Book existing = findById(id);
        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setIsbn(book.getIsbn());
        existing.setPublisher(book.getPublisher());
        existing.setCategory(book.getCategory());
        existing.setStock(book.getStock());
        existing.setTotal(book.getTotal());
        existing.setDescription(book.getDescription());
        existing.setCover(book.getCover());
        return bookRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> search(String keyword) {
        // 简化实现，实际可以用JPA Specification
        return bookRepository.findAll();
    }
}