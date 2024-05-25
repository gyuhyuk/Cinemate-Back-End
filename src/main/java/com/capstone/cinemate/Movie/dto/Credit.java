package com.capstone.cinemate.Movie.dto;


import java.util.List;

public record Credit(Long id, List<Cast> cast, List<Crew> crew) {
}
