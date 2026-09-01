package com.anq.library_management_system.service;

import com.anq.library_management_system.dto.BorrowRecordDto;
import com.anq.library_management_system.entity.Book;
import com.anq.library_management_system.entity.BorrowRecord;
import com.anq.library_management_system.entity.User;
import com.anq.library_management_system.exception.BookAlreadyReturnedException;
import com.anq.library_management_system.exception.BookNotAvailableException;
import com.anq.library_management_system.exception.BookNotFoundException;
import com.anq.library_management_system.exception.BorrowRecordNotFoundException;
import com.anq.library_management_system.exception.UserNotFoundException;
import com.anq.library_management_system.repository.BookRepository;
import com.anq.library_management_system.repository.BorrowRecordRepository;
import com.anq.library_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowRecordServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    private BorrowRecordService borrowRecordService;

    @BeforeEach
    void setUp() {

        borrowRecordService = new BorrowRecordService(
                borrowRecordRepository,
                userRepository,
                bookRepository
        );
    }

    @Test
    void borrowBook_shouldBorrowBookSuccessfully() {

        Long userId = 1L;
        Long bookId = 2L;

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(bookId);
        book.setAvailableCopies(3);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        BorrowRecordDto result =
                borrowRecordService.borrowBook(userId, bookId);

        assertAll(
                () -> assertEquals(userId, result.getUserId()),
                () -> assertEquals(bookId, result.getBookId()),
                () -> assertEquals(LocalDate.now(), result.getBorrowDate()),
                () -> assertEquals(
                        LocalDate.now().plusDays(7),
                        result.getDueDate()
                ),
                () -> assertNull(result.getReturnDate()),
                () -> assertEquals(2, book.getAvailableCopies())
        );

        verify(userRepository).findById(userId);
        verify(bookRepository).findById(bookId);
        verify(borrowRecordRepository)
                .save(any(BorrowRecord.class));
        verify(bookRepository).save(book);
    }

    @Test
    void borrowBook_shouldThrowExceptionWhenUserNotFound() {

        Long userId = 100L;
        Long bookId = 2L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> borrowRecordService.borrowBook(userId, bookId)
        );

        assertEquals(
                "User with id " + userId + " not found",
                exception.getMessage()
        );

        verify(userRepository).findById(userId);

        verifyNoInteractions(bookRepository);
        verifyNoInteractions(borrowRecordRepository);
    }

    @Test
    void borrowBook_shouldThrowExceptionWhenBookNotFound() {

        Long userId = 1L;
        Long bookId = 100L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(
                BookNotFoundException.class,
                () -> borrowRecordService.borrowBook(userId, bookId)
        );

        assertEquals(
                "Book with id " + bookId + " not found",
                exception.getMessage()
        );

        verify(userRepository).findById(userId);
        verify(bookRepository).findById(bookId);

        verifyNoInteractions(borrowRecordRepository);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void borrowBook_shouldThrowExceptionWhenBookIsUnavailable() {

        Long userId = 1L;
        Long bookId = 2L;

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(bookId);
        book.setAvailableCopies(0);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        BookNotAvailableException exception = assertThrows(
                BookNotAvailableException.class,
                () -> borrowRecordService.borrowBook(userId, bookId)
        );

        assertEquals(
                "Book with id " + bookId
                        + " is currently unavailable",
                exception.getMessage()
        );

        assertEquals(0, book.getAvailableCopies());

        verify(borrowRecordRepository, never())
                .save(any(BorrowRecord.class));

        verify(bookRepository, never())
                .save(any(Book.class));
    }

    @Test
    void returnBook_shouldReturnBookSuccessfully() {

        Long borrowRecordId = 1L;

        User user = new User();
        user.setId(10L);

        Book book = new Book();
        book.setId(20L);
        book.setAvailableCopies(2);

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(borrowRecordId);
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setBorrowDate(LocalDate.now().minusDays(2));
        borrowRecord.setDueDate(LocalDate.now().plusDays(5));
        borrowRecord.setReturnDate(null);

        when(borrowRecordRepository.findById(borrowRecordId))
                .thenReturn(Optional.of(borrowRecord));

        BorrowRecordDto result =
                borrowRecordService.returnBook(borrowRecordId);

        assertAll(
                () -> assertEquals(
                        borrowRecordId,
                        result.getId()
                ),
                () -> assertEquals(
                        user.getId(),
                        result.getUserId()
                ),
                () -> assertEquals(
                        book.getId(),
                        result.getBookId()
                ),
                () -> assertEquals(
                        LocalDate.now(),
                        result.getReturnDate()
                ),
                () -> assertEquals(
                        3,
                        book.getAvailableCopies()
                )
        );

        verify(borrowRecordRepository)
                .findById(borrowRecordId);

        verify(bookRepository).save(book);

        verify(borrowRecordRepository)
                .save(borrowRecord);
    }

    @Test
    void returnBook_shouldThrowExceptionWhenBorrowRecordNotFound() {

        Long borrowRecordId = 100L;

        when(borrowRecordRepository.findById(borrowRecordId))
                .thenReturn(Optional.empty());

        BorrowRecordNotFoundException exception = assertThrows(
                BorrowRecordNotFoundException.class,
                () -> borrowRecordService.returnBook(borrowRecordId)
        );

        assertEquals(
                "Record with id " + borrowRecordId + " not found",
                exception.getMessage()
        );

        verify(borrowRecordRepository)
                .findById(borrowRecordId);

        verifyNoInteractions(bookRepository);

        verify(borrowRecordRepository, never())
                .save(any(BorrowRecord.class));
    }

    @Test
    void returnBook_shouldThrowExceptionWhenBookAlreadyReturned() {

        Long borrowRecordId = 1L;

        User user = new User();
        user.setId(10L);

        Book book = new Book();
        book.setId(20L);
        book.setAvailableCopies(3);

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(borrowRecordId);
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setReturnDate(LocalDate.now().minusDays(1));

        when(borrowRecordRepository.findById(borrowRecordId))
                .thenReturn(Optional.of(borrowRecord));

        BookAlreadyReturnedException exception = assertThrows(
                BookAlreadyReturnedException.class,
                () -> borrowRecordService.returnBook(borrowRecordId)
        );

        assertEquals(
                "Book from this borrow record has already been returned",
                exception.getMessage()
        );

        assertEquals(3, book.getAvailableCopies());

        verify(bookRepository, never())
                .save(any(Book.class));

        verify(borrowRecordRepository, never())
                .save(any(BorrowRecord.class));
    }

    @Test
    void getAllBorrowRecords_shouldReturnAllRecords() {

        User user = new User();
        user.setId(1L);

        Book firstBook = new Book();
        firstBook.setId(10L);

        Book secondBook = new Book();
        secondBook.setId(20L);

        BorrowRecord firstRecord = new BorrowRecord();
        firstRecord.setId(1L);
        firstRecord.setUser(user);
        firstRecord.setBook(firstBook);

        BorrowRecord secondRecord = new BorrowRecord();
        secondRecord.setId(2L);
        secondRecord.setUser(user);
        secondRecord.setBook(secondBook);

        when(borrowRecordRepository.findAll())
                .thenReturn(List.of(firstRecord, secondRecord));

        List<BorrowRecordDto> result =
                borrowRecordService.getAllBorrowRecords();

        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(borrowRecordRepository).findAll();
    }

    @Test
    void getBorrowRecordById_shouldReturnRecord() {

        Long borrowRecordId = 1L;

        User user = new User();
        user.setId(10L);

        Book book = new Book();
        book.setId(20L);

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(borrowRecordId);
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);

        when(borrowRecordRepository.findById(borrowRecordId))
                .thenReturn(Optional.of(borrowRecord));

        BorrowRecordDto result =
                borrowRecordService.getBorrowRecordById(
                        borrowRecordId
                );

        assertAll(
                () -> assertEquals(
                        borrowRecordId,
                        result.getId()
                ),
                () -> assertEquals(
                        user.getId(),
                        result.getUserId()
                ),
                () -> assertEquals(
                        book.getId(),
                        result.getBookId()
                )
        );

        verify(borrowRecordRepository)
                .findById(borrowRecordId);
    }

    @Test
    void getBorrowRecordById_shouldThrowExceptionWhenNotFound() {

        Long borrowRecordId = 100L;

        when(borrowRecordRepository.findById(borrowRecordId))
                .thenReturn(Optional.empty());

        assertThrows(
                BorrowRecordNotFoundException.class,
                () -> borrowRecordService
                        .getBorrowRecordById(borrowRecordId)
        );

        verify(borrowRecordRepository)
                .findById(borrowRecordId);
    }

    @Test
    void getBorrowRecordsByUserId_shouldReturnUserRecords() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(2L);

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setId(3L);
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(borrowRecordRepository.findByUserId(userId))
                .thenReturn(List.of(borrowRecord));

        List<BorrowRecordDto> result =
                borrowRecordService
                        .getBorrowRecordsByUserId(userId);

        assertEquals(1, result.size());

        assertEquals(
                userId,
                result.getFirst().getUserId()
        );

        assertEquals(
                book.getId(),
                result.getFirst().getBookId()
        );

        verify(userRepository).findById(userId);

        verify(borrowRecordRepository)
                .findByUserId(userId);
    }

    @Test
    void getBorrowRecordsByUserId_shouldThrowExceptionWhenUserNotFound() {

        Long userId = 100L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> borrowRecordService
                        .getBorrowRecordsByUserId(userId)
        );

        verify(userRepository).findById(userId);

        verify(borrowRecordRepository, never())
                .findByUserId(anyLong());
    }

    @Test
    void getActiveBorrowRecordsByUserId_shouldReturnOnlyActiveRecords() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(2L);

        BorrowRecord activeRecord = new BorrowRecord();
        activeRecord.setId(3L);
        activeRecord.setUser(user);
        activeRecord.setBook(book);
        activeRecord.setReturnDate(null);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(
                borrowRecordRepository
                        .findByUserIdAndReturnDateIsNull(userId)
        ).thenReturn(List.of(activeRecord));

        List<BorrowRecordDto> result =
                borrowRecordService
                        .getActiveBorrowRecordsByUserId(userId);

        assertEquals(1, result.size());
        assertNull(result.getFirst().getReturnDate());

        verify(
                borrowRecordRepository
        ).findByUserIdAndReturnDateIsNull(userId);
    }

    @Test
    void getOverdueBorrowRecordsByUserId_shouldReturnOverdueRecords() {

        Long userId = 1L;

        LocalDate today = LocalDate.now();

        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(2L);

        BorrowRecord overdueRecord = new BorrowRecord();
        overdueRecord.setId(3L);
        overdueRecord.setUser(user);
        overdueRecord.setBook(book);
        overdueRecord.setBorrowDate(
                today.minusDays(10)
        );
        overdueRecord.setDueDate(
                today.minusDays(3)
        );
        overdueRecord.setReturnDate(null);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(
                borrowRecordRepository
                        .findByUserIdAndReturnDateIsNullAndDueDateBefore(
                                userId,
                                today
                        )
        ).thenReturn(List.of(overdueRecord));

        List<BorrowRecordDto> result =
                borrowRecordService
                        .getOverdueBorrowRecordsByUserId(userId);

        assertEquals(1, result.size());

        assertTrue(
                result.getFirst()
                        .getDueDate()
                        .isBefore(today)
        );

        assertNull(
                result.getFirst()
                        .getReturnDate()
        );

        verify(
                borrowRecordRepository
        ).findByUserIdAndReturnDateIsNullAndDueDateBefore(
                userId,
                today
        );
    }
}