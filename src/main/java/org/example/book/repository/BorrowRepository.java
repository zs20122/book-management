package org.example.book.repository;

import org.example.book.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    // 查询用户的借阅记录（按时间倒序）
    List<Borrow> findByUserIdOrderByCreateTimeDesc(Long userId);

    // 查询所有借阅记录（按时间倒序）
    List<Borrow> findAllByOrderByCreateTimeDesc();

    // 查询用户某本书的特定状态
    List<Borrow> findByUserIdAndBookIdAndStatusIn(Long userId, Long bookId, List<String> statuses);

    // 查询待审核的借阅记录
    List<Borrow> findByStatus(String status);

    // 查询用户某本书是否在借阅中
    @Query("SELECT b FROM Borrow b WHERE b.book.id = :bookId AND b.user.id = :userId AND b.status IN ('PENDING', 'BORROWING')")
    Optional<Borrow> findActiveBorrow(@Param("bookId") Long bookId, @Param("userId") Long userId);

    // 统计某本书被借出的数量
    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.book.id = :bookId AND b.status IN ('PENDING', 'BORROWING')")
    Integer countActiveBorrows(@Param("bookId") Long bookId);
}