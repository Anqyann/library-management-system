package com.anq.library_management_system.service;

import com.anq.library_management_system.dto.BookDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BookService {
    private final List<BookDto> books = new ArrayList<>();

    public BookService() {
        books.add(new BookDto(1L, "Java", List.of("Marc Loy"), List.of("Programming"), 2023, 3));
        books.add(new BookDto(2L, "C#", List.of("Loy Marc"), List.of("Programming"), 2023, 3)); // Изменили ID на 2L
    }


    public List<BookDto> getAllBooks() {
        return books;
    }

    public BookDto getbookById(Long id) {

        for (BookDto book : books) {
            if (Objects.equals(id, book.getId())) {
                return book;
            }
        }

        return null;
    }

    public BookDto createBook(BookDto bookDto){
        Long maxId = 0L;

        for (BookDto book : books) {
            if (book.getId() > maxId) {
                maxId = book.getId();
            }
        }
        bookDto.setId(maxId + 1);
        books.add(bookDto);
        return bookDto;

    }

    public BookDto updateBook(Long id, BookDto bookDto){
        for (BookDto book : books) {
            if (Objects.equals(id, book.getId())) {
                book.setTitle(bookDto.getTitle());
                book.setAuthors(bookDto.getAuthors());
                book.setGenres(bookDto.getGenres());
                book.setAvailableCopies(bookDto.getAvailableCopies());
                book.setPublicationYear(bookDto.getPublicationYear());
                return book;
            }
        }
        return null;
    }

    public boolean deleteBook(Long id){
        for (int i = 0; i < books.size(); i++) {
            if (Objects.equals(id, books.get(i).getId())) {
                books.remove(i);
                return true;
            }
        }
        return false;
    }
}