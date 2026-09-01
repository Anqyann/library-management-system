package com.anq.library_management_system.controller;

import com.anq.library_management_system.dto.BorrowRecordDto;
import com.anq.library_management_system.service.BorrowRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    @PostMapping("/borrow")
    public BorrowRecordDto borrowBook(
            @RequestParam Long userId,
            @RequestParam Long bookId) {

        return borrowRecordService.borrowBook(userId, bookId);
    }

    @PostMapping("/{id}/return")
    public BorrowRecordDto returnBook(@PathVariable Long id) {

        return borrowRecordService.returnBook(id);
    }

    @GetMapping
    public List<BorrowRecordDto> getBorrowRecords() {
        return borrowRecordService.getAllBorrowRecords();
    }

    @GetMapping("/{id}")
    public BorrowRecordDto getBorrowRecordById(@PathVariable Long id) {
        return borrowRecordService.getBorrowRecordById(id);
    }

    @GetMapping("/user/{userId}")
    public List<BorrowRecordDto> getBorrowRecordsByUserId(
            @PathVariable Long userId) {

        return borrowRecordService.getBorrowRecordsByUserId(userId);
    }

    @GetMapping("/user/{userId}/overdue")
    public List<BorrowRecordDto> getOverdueBorrowRecordsByUserId(
            @PathVariable Long userId) {

        return borrowRecordService.getOverdueBorrowRecordsByUserId(userId);
    }

    @GetMapping("/me")
    public List<BorrowRecordDto> getMyBorrowRecords(
            Authentication authentication) {

        return borrowRecordService
                .getBorrowRecordsForCurrentUser(
                        authentication.getName()
                );
    }

    @GetMapping("/me/active")
    public List<BorrowRecordDto> getMyActiveBorrowRecords(
            Authentication authentication) {

        return borrowRecordService
                .getActiveBorrowRecordsForCurrentUser(
                        authentication.getName()
                );
    }

    @GetMapping("/me/overdue")
    public List<BorrowRecordDto> getMyOverdueBorrowRecords(
            Authentication authentication) {

        return borrowRecordService
                .getOverdueBorrowRecordsForCurrentUser(
                        authentication.getName()
                );
    }
}