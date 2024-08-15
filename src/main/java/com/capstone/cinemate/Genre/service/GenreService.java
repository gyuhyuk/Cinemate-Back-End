package com.capstone.cinemate.Genre.service;

import com.capstone.cinemate.Genre.domain.Genre;
import com.capstone.cinemate.Genre.domain.GenreMember;
import com.capstone.cinemate.Genre.dto.GenreDto;
import com.capstone.cinemate.Genre.dto.GenreResponse;
import com.capstone.cinemate.Genre.dto.GenresResponse;
import com.capstone.cinemate.Genre.repository.GenreMemberRepository;
import com.capstone.cinemate.Genre.repository.GenreRepository;
import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMemberRepository genreMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<GenreDto> getAllGenre() {
        List<Genre> genres = genreRepository.findAll();
        return genres.stream()
                .map(genre -> new GenreDto(genre.getId(), genre.getGenreName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GenresResponse getMemberGenres(Long memberId) {
        List<GenreResponse> response = genreMemberRepository.findGenreMemberByMemberId(memberId).stream()
                .map(GenreResponse::of)
                .toList();

        return new GenresResponse(response);
    }

    @Transactional
    public void updateMemberGenres(List<Long> genreIds, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);

        genreMemberRepository.deleteByMemberId(memberId);
        genreIds.forEach(id-> {
            Genre genre = genreRepository.findById(id)
                    .orElseThrow(IllegalArgumentException::new);
            genreMemberRepository.save(new GenreMember(genre, member));
        });
    }
}
