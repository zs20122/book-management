package org.example.book.service.impl;

import org.example.book.dto.BorrowRequest;
import org.example.book.entity.Book;
import org.example.book.entity.Borrow;
import org.example.book.entity.User;
import org.example.book.exception.BusinessException;
import org.example.book.repository.BookRepository;
import org.example.book.repository.BorrowRepository;
import org.example.book.repository.UserRepository;
import org.example.book.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private static final int MAX_BORROW_DAYS = 30;

    @Override
    @Transactional
    public Borrow applyBorrow(Long userId, BorrowRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BusinessException("图书不存在"));

        // 业务规则1：检查库存
        if (book.getStock() <= 0) {
            throw new BusinessException("库存不足，无法借阅");
        }

        // 业务规则2：检查用户是否已借阅该书未归还
        borrowRepository.findActiveBorrow(request.getBookId(), userId)
                .ifPresent(b -> {
                    throw new BusinessException("您已借阅该书，请先归还");
                });

        // 业务规则3：借阅天数限制
        int days = request.getDays() == null ? 7 : request.getDays();
        if (days > MAX_BORROW_DAYS) {
            throw new BusinessException("借阅天数不能超过" + MAX_BORROW_DAYS + "天");
        }

        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setDueDate(LocalDateTime.now().plusDays(days));
        borrow.setStatus("PENDING");
        borrow.setFine(BigDecimal.ZERO);

        return borrowRepository.save(borrow);
    }

    @Override
    @Transactional
    public Borrow approveBorrow(Long borrowId, Long adminId, boolean approved) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BusinessException("借阅记录不存在"));

        // 业务规则：只有待审核状态才能审核
        if (!"PENDING".equals(borrow.getStatus())) {
            throw new BusinessException("该借阅记录已处理，无法重复审核");
        }

        if (approved) {
            Book book = borrow.getBook();
            // 再次检查库存（防止并发）
            if (book.getStock() <= 0) {
                throw new BusinessException("库存不足，审核失败");
            }
            // 扣减库存
            book.setStock(book.getStock() - 1);
            bookRepository.save(book);

            borrow.setStatus("BORROWING");
            borrow.setBorrowDate(LocalDateTime.now());
            borrow.setDueDate(LocalDateTime.now().plusDays(30));
        } else {
            borrow.setStatus("REJECTED");
        }

        return borrowRepository.save(borrow);
    }

    @Override
    @Transactional
    public Borrow returnBorrow(Long borrowId, Long userId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BusinessException("借阅记录不存在"));

        // 业务规则：只能归还自己的书
        if (!borrow.getUser().getId().equals(userId)) {
            throw new BusinessException("只能归还自己借阅的图书");
        }

        // 业务规则：只有借阅中状态才能归还
        if (!"BORROWING".equals(borrow.getStatus())) {
            throw new BusinessException("该图书当前状态无法归还");
        }

        // 计算罚金（逾期）
        LocalDateTime now = LocalDateTime.now();
        borrow.setReturnDate(now);
        if (now.isAfter(borrow.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(borrow.getDueDate(), now);
            BigDecimal fine = BigDecimal.valueOf(daysLate * 0.5); // 每天0.5元
            borrow.setFine(fine);
        }

        borrow.setStatus("RETURNED");
        Book book = borrow.getBook();
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);

        return borrowRepository.save(borrow);
    }

    @Override
    @Transactional
    public Borrow renewBorrow(Long borrowId, Long userId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BusinessException("借阅记录不存在"));

        if (!borrow.getUser().getId().equals(userId)) {
            throw new BusinessException("只能续借自己的书");
        }

        if (!"BORROWING".equals(borrow.getStatus())) {
            throw new BusinessException("只有借阅中的图书才能续借");
        }

        // 续借规则：只能续借一次，延长14天
        LocalDateTime newDue = borrow.getDueDate().plusDays(14);
        if (newDue.isAfter(LocalDateTime.now().plusDays(MAX_BORROW_DAYS))) {
            throw new BusinessException("续借后总天数不能超过" + MAX_BORROW_DAYS + "天");
        }
        borrow.setDueDate(newDue);
        return borrowRepository.save(borrow);
    }

    @Override
    public List<Borrow> getMyBorrows(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return borrowRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public List<Borrow> getAllBorrows(Long adminId) {
        return borrowRepository.findAllByOrderByCreateTimeDesc();
    }
}