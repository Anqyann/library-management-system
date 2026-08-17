package com.anq.library_management_system.dto;

import com.anq.library_management_system.validation.CurrentOrPastYear;
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

    @NotBlank
    private String description;

    @NotEmpty
    private List<Long> authors;

    @NotEmpty
    private List<Long> genres;

    @NotNull
    @Positive
    @CurrentOrPastYear //я тебя очень люблю<3
    private Integer publicationYear;

    @NotNull
    @PositiveOrZero
    private Integer availableCopies;
}
