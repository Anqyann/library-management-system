package com.anq.library_management_system.service;

import com.anq.library_management_system.dto.BorrowRecordDto;
import com.anq.library_management_system.entity.Book;
import com.anq.library_management_system.entity.BorrowRecord;
import com.anq.library_management_system.entity.User;
import com.anq.library_management_system.exception.*;
import com.anq.library_management_system.repository.BookRepository;
import com.anq.library_management_system.repository.BorrowRecordRepository;
import com.anq.library_management_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Service
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BorrowRecordService(
            BorrowRecordRepository borrowRecordRepository,
            UserRepository userRepository,
            BookRepository bookRepository) {

        this.borrowRecordRepository = borrowRecordRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    private BorrowRecordDto toDto(BorrowRecord borrowRecord) {

        BorrowRecordDto dto = new BorrowRecordDto();

        dto.setId(borrowRecord.getId());
        dto.setUserId(borrowRecord.getUser().getId());
        dto.setBookId(borrowRecord.getBook().getId());
        dto.setBorrowDate(borrowRecord.getBorrowDate());
        dto.setDueDate(borrowRecord.getDueDate());
        dto.setReturnDate(borrowRecord.getReturnDate());

        return dto;
    }

    @Transactional
    public BorrowRecordDto borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id " + userId + " not found"
                ));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(
                        "Book with id " + bookId + " not found"
                ));
        if(book.getAvailableCopies() == 0){
            throw new BookNotAvailableException(
                    "Book with id " + bookId + " is currently unavailable"
            );
        }
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(LocalDate.now());
        borrowRecord.setDueDate(LocalDate.now().plusDays(7));
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        borrowRecordRepository.save(borrowRecord);
        bookRepository.save(book);

        return toDto(borrowRecord);
    }

    @Transactional
    public BorrowRecordDto returnBook(Long borrowRecordId) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new BorrowRecordNotFoundException(
                        "Record with id " + borrowRecordId + " not found"
                ));
        if (borrowRecord.getReturnDate() != null) {
            throw new BookAlreadyReturnedException(
                    "Book from this borrow record has already been returned"
            );
        }
        Book book = borrowRecord.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        borrowRecord.setReturnDate(LocalDate.now());
        bookRepository.save(book);
        borrowRecordRepository.save(borrowRecord);
        return toDto(borrowRecord);
    }

    public List<BorrowRecordDto> getAllBorrowRecords() {

        List<BorrowRecord> borrowRecords = borrowRecordRepository.findAll();
        List<BorrowRecordDto> borrowRecordDtos = new ArrayList<>();

        for (BorrowRecord borrowRecord : borrowRecords) {
            BorrowRecordDto dto = toDto(borrowRecord);
            borrowRecordDtos.add(dto);
        }

        return borrowRecordDtos;
    }

    public BorrowRecordDto getBorrowRecordById(Long id) {

        BorrowRecord borrowRecord = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new BorrowRecordNotFoundException(
                        "Record with id " + id + " not found"
                ));

        return toDto(borrowRecord);
    }

    public List<BorrowRecordDto> getBorrowRecordsByUserId(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id " + userId + " not found"
                ));

        List<BorrowRecord> borrowRecords =
                borrowRecordRepository.findByUserId(userId);

        List<BorrowRecordDto> borrowRecordDtos = new ArrayList<>();

        for (BorrowRecord borrowRecord : borrowRecords) {
            borrowRecordDtos.add(toDto(borrowRecord));
        }

        return borrowRecordDtos;
    }

    public List<BorrowRecordDto> getActiveBorrowRecordsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id " + userId + " not found"
                ));
        List<BorrowRecord> borrowRecords =
                borrowRecordRepository.findByUserIdAndReturnDateIsNull(userId);
        List<BorrowRecordDto> borrowRecordDtos = new ArrayList<>();
        for (BorrowRecord borrowRecord : borrowRecords) {
            borrowRecordDtos.add(toDto(borrowRecord));
        }
        return borrowRecordDtos;
    }

    public List<BorrowRecordDto> getOverdueBorrowRecordsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id " + userId + " not found"
                ));
        List<BorrowRecord> borrowRecords =
                borrowRecordRepository.findByUserIdAndReturnDateIsNullAndDueDateBefore(userId,
                        LocalDate.now());
        List<BorrowRecordDto> borrowRecordDtos = new ArrayList<>();
        for (BorrowRecord borrowRecord : borrowRecords) {
            borrowRecordDtos.add(toDto(borrowRecord));
        }
        return borrowRecordDtos;
    }

    private User getUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with username " + username + " not found"
                ));
    }

    public List<BorrowRecordDto> getBorrowRecordsForCurrentUser(
            String username) {

        User user = getUserByUsername(username);

        List<BorrowRecord> borrowRecords =
                borrowRecordRepository.findByUserId(user.getId());

        List<BorrowRecordDto> borrowRecordDtos = new ArrayList<>();

        for (BorrowRecord borrowRecord : borrowRecords) {
            borrowRecordDtos.add(toDto(borrowRecord));
        }

        return borrowRecordDtos;
    }

    public List<BorrowRecordDto> getActiveBorrowRecordsForCurrentUser(
            String username) {

        User user = getUserByUsername(username);

        List<BorrowRecord> borrowRecords =
                borrowRecordRepository
                        .findByUserIdAndReturnDateIsNull(user.getId());

        List<BorrowRecordDto> borrowRecordDtos = new ArrayList<>();

        for (BorrowRecord borrowRecord : borrowRecords) {
            borrowRecordDtos.add(toDto(borrowRecord));
        }

        return borrowRecordDtos;
    }
    public List<BorrowRecordDto> getOverdueBorrowRecordsForCurrentUser(
            String username) {

        User user = getUserByUsername(username);

        List<BorrowRecord> borrowRecords =
                borrowRecordRepository
                        .findByUserIdAndReturnDateIsNullAndDueDateBefore(
                                user.getId(),
                                LocalDate.now()
                        );

        List<BorrowRecordDto> borrowRecordDtos = new ArrayList<>();

        for (BorrowRecord borrowRecord : borrowRecords) {
            borrowRecordDtos.add(toDto(borrowRecord));
        }

        return borrowRecordDtos;
    }

}