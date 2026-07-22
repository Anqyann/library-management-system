package com.anq.library_management_system.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    @NotBlank
    private String title;

    @NotEmpty
    private List<String> authors;

    @NotEmpty
    private List<String> genres;

    @NotNull
    @Positive
    private Integer publicationYear;

    @NotNull
    @PositiveOrZero
    private Integer availableCopies;
}
