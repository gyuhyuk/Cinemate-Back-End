package com.capstone.cinemate.Movie.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Crew {
    Boolean adult;
    Long gender;
    Long id;
    String known_for_department;
    String name;
    String original_name;
    Double popularity;
    String profile_path;
    String credit_id;
    String department;
    String job;
}