package com.capstone.cinemate.Genre.service;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Transactional(readOnly = true)
    public List<Genre> getAllGenre() {
        return genreRepository.findAll();
    }
}
