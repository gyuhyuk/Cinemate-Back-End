package com.capstone.cinemate.Genre.controller;

import com.capstone.cinemate.Genre.dto.GenreDto;
import com.capstone.cinemate.Genre.dto.GenresResponse;
import com.capstone.cinemate.Genre.service.GenreService;
import com.capstone.cinemate.Member.controller.helper.TokenInformation;
import com.capstone.cinemate.common.response.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/api/genres")
    public ResponseEntity<CustomResponse<List<GenreDto>>> getGenres() {
        List<GenreDto> genres = genreService.getAllGenre();
        CustomResponse<List<GenreDto>> response = new CustomResponse<>(HttpStatus.OK.value(), "Success", genres);

        return ResponseEntity.ok().body(response);
    }

    // 멤버-장르 조회
    @GetMapping("/api/member-genres")
    public ResponseEntity<CustomResponse<GenresResponse>> getMemberGenres(@TokenInformation Long memberId) {
        GenresResponse response = genreService.getMemberGenres(memberId);

        CustomResponse<GenresResponse> customResponse = new CustomResponse<>(HttpStatus.OK.value(), "Success", response);
        return ResponseEntity.ok().body(customResponse);
    }

    // 멤버의 장르 수정
    @PatchMapping("/api/survey/genre")
    public ResponseEntity<CustomResponse<Map<String, List<Long>>>> updateMemberGenres(@RequestParam List<Long> genreIds, @TokenInformation Long memberId) {
        genreService.updateMemberGenres(genreIds, memberId);

        Map<String, List<Long>> result = new HashMap<>();
        result.put("genreIds", genreIds);

        CustomResponse<Map<String, List<Long>>> response = new CustomResponse<>(HttpStatus.OK.value(), "Success", result);
        return ResponseEntity.ok().body(response);
    }
}
