package com.anq.library_management_system.repository;

import com.anq.library_management_system.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}