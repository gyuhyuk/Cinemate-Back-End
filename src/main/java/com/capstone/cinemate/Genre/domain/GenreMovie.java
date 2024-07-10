package com.capstone.cinemate.Genre.domain;

import com.capstone.cinemate.Movie.domain.Movie;
import jakarta.persistence.*;
import lombok.*;

// 영화와 장르는 다대일 (한개의 영화에는 여러개의 장르가 포함)
@Entity
@Getter
@Setter
public class GenreMovie {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "genreId")
        private Genre genre;

        @ManyToOne
        @JoinColumn(name = "movieId")
        private Movie movie;
}
