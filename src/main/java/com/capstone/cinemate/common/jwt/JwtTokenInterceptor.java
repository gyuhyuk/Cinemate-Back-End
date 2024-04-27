package com.capstone.cinemate.common.jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final TokenUtils tokenUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        System.out.println("JwtToken 호출");
        String accessToken = request.getHeader("accessToken");
        System.out.println("AccessToken:" + accessToken);
//        String refreshToken = request.getHeader("REFRESH_TOKEN");
//        System.out.println("RefreshToken:" + refreshToken);

        if (accessToken != null) {
            if (tokenUtils.isValidToken(accessToken)) {
                return true;
            }
        }
        response.setStatus(401);
        response.setHeader("ACCESS_TOKEN", accessToken);
//        response.setHeader("REFRESH_TOKEN", refreshToken);
        response.setHeader("msg", "Check the tokens.");
        return false;
    }
}