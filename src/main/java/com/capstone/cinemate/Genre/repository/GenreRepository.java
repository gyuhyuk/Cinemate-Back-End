package com.capstone.cinemate.Genre.repository;

import com.capstone.cinemate.Genre.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}
