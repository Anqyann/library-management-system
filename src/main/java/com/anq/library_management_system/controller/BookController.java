package com.anq.library_management_system.controller;

import com.anq.library_management_system.dto.BookDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.anq.library_management_system.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDto> getBooks() { return bookService.getAllBooks();}

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getbookById(id);
    }

    @PostMapping
    public BookDto createBook(@Valid @RequestBody BookDto bookDto ) {
        return  bookService.createBook(bookDto);
    }

    @PutMapping("/{id}")
    public BookDto updateBook(@Valid @PathVariable Long id, @Valid @RequestBody BookDto bookDto){
        return  bookService.updateBook(id, bookDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
