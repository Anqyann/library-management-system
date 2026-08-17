package com.anq.library_management_system.service;

import com.anq.library_management_system.dto.BookDto;
import com.anq.library_management_system.entity.Author;
import com.anq.library_management_system.entity.Book;
import com.anq.library_management_system.entity.Genre;
import com.anq.library_management_system.exception.AuthorNotFoundException;
import com.anq.library_management_system.exception.BookNotFoundException;
import com.anq.library_management_system.exception.GenreNotFoundException;
import com.anq.library_management_system.repository.AuthorRepository;
import com.anq.library_management_system.repository.BookRepository;
import com.anq.library_management_system.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookService(BookRepository bookRepository,AuthorRepository authorRepository,
                       GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    private BookDto toDto(Book book) {

        BookDto dto = new BookDto();

        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDescription(book.getDescription());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setAvailableCopies(book.getAvailableCopies());

        List<Long> authorIds = new ArrayList<>();

        for (Author author : book.getAuthors()) {
            authorIds.add(author.getId());
        }

        dto.setAuthors(authorIds);

        List<Long> genreIds = new ArrayList<>();

        for (Genre genre : book.getGenres()) {
            genreIds.add(genre.getId());
        }

        dto.setGenres(genreIds);

        return dto;
    }

    private Book fromDto(BookDto bookDto) {

        Book book = new Book();

        book.setTitle(bookDto.getTitle());
        book.setDescription(bookDto.getDescription());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setAvailableCopies(bookDto.getAvailableCopies());

        List<Author> authors = getAuthorsByIds(bookDto.getAuthors());
        List<Genre> genres = getGenresByIds(bookDto.getGenres());

        book.setAuthors(authors);
        book.setGenres(genres);

        return book;
    }
    private List<Author> getAuthorsByIds(List<Long> authorIds) {

        List<Author> authors = authorRepository.findAllById(authorIds);

        if (authors.size() != authorIds.size()) {
            throw new AuthorNotFoundException(
                    "One or more authors not found"
            );
        }

        return authors;
    }
    private List<Genre> getGenresByIds(List<Long> genreIds) {

        List<Genre> genres = genreRepository.findAllById(genreIds);

        if (genres.size() != genreIds.size()) {
            throw new GenreNotFoundException(
                    "One or more genres not found"
            );
        }

        return genres;
    }
    public List<BookDto> getAllBooks() {

        List<Book> books = bookRepository.findAll();
        List<BookDto> bookDtos = new ArrayList<>();

        for (Book book : books) {

            BookDto bookDto = toDto(book);

            bookDtos.add(bookDto);
        }

        return bookDtos;
    }

    public BookDto getbookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(
                        "Book with id " + id + " not found"
                ));


        BookDto bookDto = toDto(book);

        return bookDto;
    }

    public BookDto createBook(BookDto bookDto) {

        Book book = fromDto(bookDto);

        Book savedBook = bookRepository.save(book);

        BookDto savedBookDto = toDto(savedBook);

        return savedBookDto;
    }

    public BookDto updateBook(Long id, BookDto bookDto) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(
                        "Book with id " + id + " not found"
                ));

        book.setTitle(bookDto.getTitle());
        book.setDescription(bookDto.getDescription());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setAvailableCopies(bookDto.getAvailableCopies());

        List<Author> authors = getAuthorsByIds(bookDto.getAuthors());
        List<Genre> genres = getGenresByIds(bookDto.getGenres());

        book.setAuthors(authors);
        book.setGenres(genres);

        Book updatedBook = bookRepository.save(book);

        BookDto updatedBookDto = toDto(updatedBook);

        return updatedBookDto;
    }

    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(
                        "Book with id " + id + " not found"
                ));

        bookRepository.delete(book);
    }
}