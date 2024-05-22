package com.capstone.cinemate.Genre.domain;

import com.capstone.cinemate.Member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

// 장르와 멤버는 다대다
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GenreMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genreId")
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Builder
    public GenreMember(Genre genre, Member member) {
        this.genre = genre;
        this.member = member;
    }
}
