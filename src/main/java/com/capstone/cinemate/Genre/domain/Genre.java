package com.capstone.cinemate.Genre.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String genreName;

    // 장르와 멤버
    @OneToMany(mappedBy = "genre")
    private List<GenreMember> genreMembers = new ArrayList<>();

    // 장르와 영화
    @OneToMany(mappedBy = "genre")
    private List<GenreMovie> genreMovies = new ArrayList<>();
}
