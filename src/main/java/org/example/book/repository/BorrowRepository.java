package org.example.book.repository;

import org.example.book.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findByUserIdOrderByCreateTimeDesc(Long userId);
    List<Borrow> findAllByOrderByCreateTimeDesc();
    List<Borrow> findByUserIdAndBookIdAndStatusIn(Long userId, Long bookId, List<String> statuses);
    List<Borrow> findByStatus(String status);
}