package com.anq.library_management_system.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordDto {

    private Long id;

    private Long userId;

    private Long bookId;

    private LocalDate borrowDate;

    private LocalDate dueDate;

    private LocalDate returnDate;
}