package com.capstone.cinemate.Genre.service;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.dto.GenreDto;
import com.capstone.cinemate.Genre.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    public List<GenreDto> getAllGenre() {
        List<Genre> genres = genreRepository.findAll();
        return genres.stream()
                .map(genre -> new GenreDto(genre.getId(), genre.getGenreName()))
                .collect(Collectors.toList());
    }
}
