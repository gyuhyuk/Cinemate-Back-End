package com.capstone.cinemate.Review.domain;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Movie.domain.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@Entity
@Table(indexes = {
        @Index(columnList = "movieId")
})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Setter @ManyToOne(optional = false) private Movie movie; // 영화 (ID)
    @Setter @Column(nullable = false, length = 2000) private String content; // 리뷰 내용
    @Setter @Column(nullable = false) private Double rating; // 평점

    private Long likes = 0L; // 좋아요 수

    @Setter @ManyToOne(optional = false) private Member member; // 멤버 (ID)

    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt; // 작성일시
    @CreatedBy @Column(nullable = false, length = 100) private String createdBy; // 작성자
    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt; // 수정일시
    @LastModifiedBy @Column(nullable = false, length = 100) private String modifiedBy; // 수정자

    protected Review() {}

    private Review(Movie movie, String content, Double rating) {
        this.movie = movie;
        this.content = content;
        this.rating = rating;
    }

    public static Review of(Movie movie, String content, Double rating) {
      return new Review(movie, content, rating);
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
}
