package com.capstone.cinemate.common.type;

import lombok.Getter;

public enum MovieSearchType {
    TITLE("제목");

    @Getter private final String description;

    MovieSearchType(String description) {
        this.description = description;
    }
}
