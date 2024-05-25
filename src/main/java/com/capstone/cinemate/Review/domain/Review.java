package com.capstone.cinemate.Review.domain;

import com.capstone.cinemate.Heart.domain.ReviewHeart;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Movie.domain.Movie;
import com.capstone.cinemate.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "movieId")
})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Setter @ManyToOne(optional = false) private Movie movie; // 영화 (ID)
    @Setter @Column(length = 2000) private String content; // 리뷰 내용
    @Setter @Column(nullable = false) private Double rating; // 평점

    @Setter private Long likes = 0L; // 좋아요 수

    @Setter @ManyToOne(optional = false) private Member member; // 멤버 (ID)

    // 리뷰는 여러 사람에게 좋아요를 받을 수 있음
    @OneToMany(mappedBy = "review")
    private List<ReviewHeart> reviewHearts = new ArrayList<>();

    public Review(Movie movie, String content, Member member, Double rating) {
        this.movie = movie;
        this.content = content;
        this.member = member;
        this.rating = rating;
    }


    public static Review of(Movie movie, String content, Member member, Double rating) {
      return new Review(movie, content, member, rating);
    }

    public static Review of(Movie movie, Member member, Double rating) {
        return new Review(movie, "", member, rating);
    }

    public Review(Movie movie, Member member, Double rating) {
        this(movie, "", member, rating);
    }

    public void contentUpdate(Movie movie, String content, Member member) {
        this.movie = movie;
        this.member = member;
        this.content = content;
    }

    public void ratingUpdate(Movie movie, Double rating, Member member) {
        this.movie = movie;
        this.member = member;
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void increaseLikes() {
        likes++;
    }

    public void decreaseLikes() {
        likes--;
    }
}
