//package com.capstone.cinemate.Movie.dto;
//
//import com.capstone.cinemate.Member.dto.MemberDto;
//import com.capstone.cinemate.Movie.domain.Movie;
//import com.capstone.cinemate.Review.dto.MovieReviewDto;
//
//import java.time.LocalDateTime;
//import java.util.Set;
//
//public record MovieWithReviewsDto(Long id, MemberDto memberDto, Set<MovieReviewDto> movieReviewDtos,
//                                  Long movieId, Double rating, String backdropPath, String originalTitle,
//                                  String movieTitle, LocalDateTime releaseDate, String posterPath,
//                                  String overview) {
//    public static MovieWithReviewsDto of(Long id, MemberDto memberDto, Set<MovieReviewDto> movieReviewDtos,
//                                         Long movieId, Double rating, String backdropPath, String originalTitle,
//                                         String movieTitle, LocalDateTime releaseDate, String posterPath,
//                                         String overview) {
//        return new MovieWithReviewsDto(id, memberDto, movieReviewDtos, movieId, rating, backdropPath, originalTitle, movieTitle, releaseDate, posterPath, overview);
//    }
//
//    public static MovieWithReviewsDto from(Movie entity) {
//        return new MovieWithReviewsDto(
//                entity.getId(),
//
//        );
//    }
//}
