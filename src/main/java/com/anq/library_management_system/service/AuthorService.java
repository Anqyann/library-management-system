package com.anq.library_management_system.service;

import com.anq.library_management_system.dto.AuthorDto;
import com.anq.library_management_system.entity.Author;
import com.anq.library_management_system.exception.AuthorNotFoundException;
import com.anq.library_management_system.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    private AuthorDto toDto(Author author) {

        AuthorDto dto = new AuthorDto();

        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());

        return dto;
    }

    private Author fromDto(AuthorDto authorDto) {

        Author author = new Author();

        author.setFirstName(authorDto.getFirstName());
        author.setLastName(authorDto.getLastName());

        return author;
    }

    public List<AuthorDto> getAllAuthors() {

        List<Author> authors = authorRepository.findAll();
        List<AuthorDto> authorDtos = new ArrayList<>();

        for (Author author : authors) {

            AuthorDto authorDto = toDto(author);

            authorDtos.add(authorDto);
        }

        return authorDtos;
    }

    public AuthorDto getAuthorById(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(
                        "Author with id " + id + " not found"
                ));


        AuthorDto authorDto = toDto(author);

        return authorDto;
    }

    public AuthorDto createAuthor(AuthorDto authorDto) {

        Author author = fromDto(authorDto);

        Author savedAuthor = authorRepository.save(author);

        AuthorDto savedAuthorDto = toDto(savedAuthor);

        return savedAuthorDto;
    }

    public AuthorDto updateAuthor(Long id, AuthorDto authorDto) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(
                        "Author with id " + id + " not found"
                ));

        author.setFirstName(authorDto.getFirstName());
        author.setLastName(authorDto.getLastName());


        Author updatedAuthor = authorRepository.save(author);

        AuthorDto updatedAuthorDto = toDto(updatedAuthor);

        return updatedAuthorDto;
    }

    public void deleteAuthor(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(
                        "Author with id " + id + " not found"
                ));

        authorRepository.delete(author);
    }
}