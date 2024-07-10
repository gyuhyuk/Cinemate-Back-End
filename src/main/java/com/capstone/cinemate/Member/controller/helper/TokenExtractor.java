package com.capstone.cinemate.Member.controller.helper;

import com.capstone.cinemate.common.exception.CustomException;

import java.util.Optional;

import static com.capstone.cinemate.common.exception.ErrorCode.INVALID_TOKEN;

public class TokenExtractor {

    private static final String HEADER_DELIMITER = " ";
    private static final String PREFIX = "Bearer";
    private static final int VALID_AUTHORIZATION_LENGTH = 2;
    private static final int TOKEN_INDEX = 1;
    private static final int PREFIX_INDEX = 0;

    public static String extractToken(final Optional<String> authorization)
            throws RuntimeException {
        if (authorization.isEmpty()) {
            throw new CustomException(INVALID_TOKEN);
        }

        String[] splitAuthorization = authorization.get().split(HEADER_DELIMITER);

        validateAuthorization(splitAuthorization);
        return splitAuthorization[TOKEN_INDEX];
    }

    private static void validateAuthorization(final String[] splitAuthorization)
            throws RuntimeException {
        if (splitAuthorization.length != VALID_AUTHORIZATION_LENGTH) {
            throw new CustomException(INVALID_TOKEN);
        }

        if (!splitAuthorization[PREFIX_INDEX].equals(PREFIX)) {
            throw new CustomException(INVALID_TOKEN);
        }
    }
}
