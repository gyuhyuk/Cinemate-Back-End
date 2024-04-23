package com.capstone.cinemate.Movie.domain;

import com.capstone.cinemate.Member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class MemberMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "movieId")
    private Movie movie;
}
