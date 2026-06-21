package org.example.book.service;

import org.example.book.dto.BorrowRequest;
import org.example.book.entity.Borrow;

import java.util.List;

public interface BorrowService {
    Borrow applyBorrow(Long userId, BorrowRequest request);
    Borrow approveBorrow(Long borrowId, Long adminId, boolean approved);
    Borrow returnBorrow(Long borrowId, Long userId);
    Borrow renewBorrow(Long borrowId, Long userId);
    List<Borrow> getMyBorrows(Long userId);
    List<Borrow> getAllBorrows(Long adminId);
}