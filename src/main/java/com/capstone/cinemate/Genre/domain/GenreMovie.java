package com.capstone.cinemate.Genre.domain;

import com.capstone.cinemate.Movie.domain.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
