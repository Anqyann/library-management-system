package com.anq.library_management_system.service;

import com.anq.library_management_system.dto.GenreDto;
import com.anq.library_management_system.entity.Genre;
import com.anq.library_management_system.exception.GenreNotFoundException;
import com.anq.library_management_system.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    private GenreDto toDto(Genre genre) {

        GenreDto dto = new GenreDto();

        dto.setId(genre.getId());
        dto.setName(genre.getName());

        return dto;
    }

    private Genre fromDto(GenreDto genreDto) {

        Genre genre = new Genre();

        genre.setName(genreDto.getName());

        return genre;
    }

    public List<GenreDto> getAllGenres() {

        List<Genre> genres = genreRepository.findAll();
        List<GenreDto> genreDtos = new ArrayList<>();

        for (Genre genre : genres) {

            GenreDto genreDto = toDto(genre);

            genreDtos.add(genreDto);
        }

        return genreDtos;
    }

    public GenreDto getGenreById(Long id) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(
                        "Genre with id " + id + " not found"
                ));

        return toDto(genre);
    }

    public GenreDto createGenre(GenreDto genreDto) {

        Genre genre = fromDto(genreDto);

        Genre savedGenre = genreRepository.save(genre);

        return toDto(savedGenre);
    }

    public GenreDto updateGenre(Long id, GenreDto genreDto) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(
                        "Genre with id " + id + " not found"
                ));

        genre.setName(genreDto.getName());

        Genre updatedGenre = genreRepository.save(genre);

        return toDto(updatedGenre);
    }

    public void deleteGenre(Long id) {

        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException(
                        "Genre with id " + id + " not found"
                ));

        genreRepository.delete(genre);
    }
}