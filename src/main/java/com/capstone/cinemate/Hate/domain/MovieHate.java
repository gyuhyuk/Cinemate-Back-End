package com.capstone.cinemate.Hate.domain;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Movie.domain.Movie;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieHate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movieId")
    private Movie movie;

    @Builder
    public MovieHate(Member member, Movie movie) {
        this.member = member;
        this.movie = movie;
    }
}
