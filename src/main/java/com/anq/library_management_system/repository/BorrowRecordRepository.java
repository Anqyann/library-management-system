package com.anq.library_management_system.repository;

import com.anq.library_management_system.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
        List<BorrowRecord> findByUserId(Long userId);
        List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);
        List<BorrowRecord> findByUserIdAndReturnDateIsNullAndDueDateBefore(
                Long userId,
                LocalDate date
        );
}