package org.example.book.service;

import org.example.book.entity.User;

public interface UserService {
    User findByUsername(String username);
    User findById(Long id);
    User save(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}