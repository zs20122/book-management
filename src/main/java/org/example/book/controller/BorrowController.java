package org.example.book.controller;

import org.example.book.dto.ApiResponse;
import org.example.book.dto.BorrowRequest;
import org.example.book.entity.Borrow;
import org.example.book.entity.Book;
import org.example.book.entity.User;
import org.example.book.repository.BorrowRepository;
import org.example.book.repository.UserRepository;
import org.example.book.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    // 根据 userId 获取用户
    private User getUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("用户ID不能为空");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    // 1. 提交借阅申请
    @PostMapping
    public ApiResponse<?> applyBorrow(@RequestBody BorrowRequest request,
                                      @RequestParam Long userId) {
        try {
            User user = getUser(userId);
            System.out.println("借阅用户: " + user.getUsername() + ", ID: " + user.getId());

            Book book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new RuntimeException("图书不存在"));

            if (book.getStock() <= 0) {
                return ApiResponse.error(400, "库存不足，无法借阅");
            }

            List<Borrow> existing = borrowRepository.findByUserIdAndBookIdAndStatusIn(
                    user.getId(), request.getBookId(), List.of("BORROWING", "PENDING")
            );
            if (!existing.isEmpty()) {
                return ApiResponse.error(400, "您已借阅该书，请先归还");
            }

            Borrow borrow = new Borrow();
            borrow.setUser(user);
            borrow.setBook(book);
            borrow.setBorrowDate(LocalDateTime.now());
            borrow.setDueDate(LocalDateTime.now().plusDays(7));
            borrow.setStatus("PENDING");
            borrow.setFine(BigDecimal.ZERO);

            borrowRepository.save(borrow);
            return ApiResponse.success("借阅申请提交成功，等待管理员审核", borrow);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "借阅失败：" + e.getMessage());
        }
    }

    // 2. 获取当前用户的借阅记录（我的借阅）
    @GetMapping("/me")
    public ApiResponse<?> getMyBorrows(@RequestParam Long userId) {
        try {
            User user = getUser(userId);
            List<Borrow> borrows = borrowRepository.findByUserIdOrderByCreateTimeDesc(user.getId());
            return ApiResponse.success(borrows);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "获取借阅记录失败：" + e.getMessage());
        }
    }

    // 3. 管理员获取所有借阅记录（借阅审核）
    @GetMapping("/all")
    public ApiResponse<?> getAllBorrows() {
        try {
            List<Borrow> borrows = borrowRepository.findAllByOrderByCreateTimeDesc();
            return ApiResponse.success(borrows);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "获取借阅记录失败：" + e.getMessage());
        }
    }

    // 4. 管理员审核借阅
    @PutMapping("/{id}/approve")
    public ApiResponse<?> approveBorrow(@PathVariable Long id, @RequestParam boolean approved) {
        try {
            Borrow borrow = borrowRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

            if (!"PENDING".equals(borrow.getStatus())) {
                return ApiResponse.error(400, "该记录已处理");
            }

            if (approved) {
                Book book = borrow.getBook();
                if (book.getStock() <= 0) {
                    return ApiResponse.error(400, "库存不足，无法批准");
                }
                book.setStock(book.getStock() - 1);
                bookRepository.save(book);
                borrow.setStatus("BORROWING");
                borrow.setBorrowDate(LocalDateTime.now());
                borrow.setDueDate(LocalDateTime.now().plusDays(30));
            } else {
                borrow.setStatus("REJECTED");
            }

            borrowRepository.save(borrow);
            return ApiResponse.success(approved ? "审核通过" : "已拒绝", borrow);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "审核失败：" + e.getMessage());
        }
    }

    // 5. 归还图书
    @PutMapping("/{id}/return")
    public ApiResponse<?> returnBook(@PathVariable Long id, @RequestParam Long userId) {
        try {
            User user = getUser(userId);
            Borrow borrow = borrowRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

            if (!borrow.getUser().getId().equals(user.getId())) {
                return ApiResponse.error(403, "只能归还自己的图书");
            }

            if (!"BORROWING".equals(borrow.getStatus())) {
                return ApiResponse.error(400, "该图书状态无法归还");
            }

            Book book = borrow.getBook();
            book.setStock(book.getStock() + 1);
            bookRepository.save(book);

            borrow.setStatus("RETURNED");
            borrow.setReturnDate(LocalDateTime.now());
            borrowRepository.save(borrow);
            return ApiResponse.success("归还成功", borrow);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "归还失败：" + e.getMessage());
        }
    }

    // 6. 续借
    @PutMapping("/{id}/renew")
    public ApiResponse<?> renewBorrow(@PathVariable Long id, @RequestParam Long userId) {
        try {
            User user = getUser(userId);
            Borrow borrow = borrowRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

            if (!borrow.getUser().getId().equals(user.getId())) {
                return ApiResponse.error(403, "只能续借自己的图书");
            }

            if (!"BORROWING".equals(borrow.getStatus())) {
                return ApiResponse.error(400, "只有借阅中的图书才能续借");
            }

            borrow.setDueDate(borrow.getDueDate().plusDays(14));
            borrowRepository.save(borrow);
            return ApiResponse.success("续借成功", borrow);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "续借失败：" + e.getMessage());
        }
    }
}