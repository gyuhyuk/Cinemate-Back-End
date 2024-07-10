package com.capstone.cinemate.Movie.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Cast {
    Boolean adult;
    Long gender;
    Long id;
    String known_for_department;
    String name;
    String original_name;
    Double popularity;
    String profile_path;
    Long cast_id;
    String character;
    String credit_id;
    Long order;
}
