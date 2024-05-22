package com.capstone.cinemate.common.exception;

import lombok.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_INPUT_VALUE(400, "BAD_REQUEST", "입력값이 올바르지 않습니다."),
    BAD_REQUEST(400, "BAD_REQUEST", "잘못된 요청입니다."),
    WRONG_PASSWORD(400, "BAD_REQUEST", "잘못된 비밀번호입니다."),
    INVALID_QUERY(400, "BAD_REQUEST", "인코딩에 실패했습니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    UNAUTHENTICATED_USERS(401, "UNAUTHORIZED", "인증이 필요합니다."),
    EXPIRED_TOKEN(401, "UNAUTHORIZED", "만료된 토큰입니다."),
    INVALID_TOKEN(401, "UNAUTHORIZED", "잘못된 JWT 서명입니다."),
    UNAUTHORIZED_USER(401, "UNAUTHORIZED", "권한이 없습니다."),

    /* 403 FORBIDDEN : 접근권한 없음 */
    ACCESS_DENIED(403, "FORBIDDEN", "접근이 거부되었습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(404, "NOT_FOUND", "해당 유저 정보를 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(404, "NOT_FOUND", "해당 정보를 찾을 수 없습니다."),
    MOVIE_NOT_FOUND(404, "NOT_FOUND", "해당 영화를 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(404, "NOT_FOUND", "해당 리뷰를 찾을 수 없습니다."),

    /* 405 METHOD_NOT_ALLOWED : 지원하지 않는 HTTP Method */
    METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED", "허용되지 않은 요청입니다."),

    /* 409 CONFLICT : 데이터 중복 */
    DUPLICATE_EMAIL_RESOURCE(409, "CONFLICT", "이메일이 이미 존재합니다"),
    DUPLICATE_NICKNAME_RESOURCE(409, "CONFLICT", "닉네임이 이미 존재합니다"),
    REVIEW_ALREADY_EXISTS(409, "CONFLICT", "리뷰가 이미 존재합니다."),

    /* 500 INTERNAL_SERVER_ERROR */
    SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "예기치 못한 오류가 발생하였습니다."),
    IMAGE_UPLOAD_FAIL(500, "IMAGE_UPLOAD_FAIL", "이미지 업로드에 실패했습니다.");

    private final int status;
    private final String code;
    private final String cause;
}