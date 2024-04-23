package com.capstone.cinemate.Genre.domain;

import com.capstone.cinemate.Member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GenreMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "genreId")
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;
}
