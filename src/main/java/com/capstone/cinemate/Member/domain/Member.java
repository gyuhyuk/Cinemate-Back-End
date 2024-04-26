package com.capstone.cinemate.Member.domain;

import com.capstone.cinemate.Genre.domain.GenreMember;
import com.capstone.cinemate.Movie.domain.MemberMovie;
import com.capstone.cinemate.Review.domain.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) private String memberId;

    private String password;

    @Column(unique = true) private String nickName;

    // 멤버와 장르 (유저는 여러개의 장르를 가짐)
    @OneToMany(mappedBy = "member")
    private final List<GenreMember> members = new ArrayList<>();

//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    // 멤버와 영화 (유저는 여러개의 영화를 가짐)
    @OneToMany(mappedBy = "member")
    @ToString.Exclude
    private final Set<MemberMovie> memberMovies = new LinkedHashSet<>();

    // 멤버와 리뷰 (유저는 여러개의 리뷰 작성)
    @OneToMany(mappedBy = "member")
    @ToString.Exclude
    private final Set<Review> memberReviews = new LinkedHashSet<>();

    @Builder
    public Member(String memberId, String password, String nickName) {
        this.memberId = memberId;
        this.password = password;
        this.nickName = nickName;
    }
}