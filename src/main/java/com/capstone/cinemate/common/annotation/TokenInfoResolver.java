package com.capstone.cinemate.common.annotation;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class TokenInfoResolver implements HandlerMethodArgumentResolver {

    private static final int BEARER_TOKEN_PREFIX_LENGTH = 7;
    private final MemberService memberService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(TokenInfo.class);
    }

    @Override
    public Member resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String jwt = getJwtFromAuthorization(authorization);
        return memberService.findMemberByJwt(jwt);
    }

    private String getJwtFromAuthorization(String authorization) {
        return authorization.substring(BEARER_TOKEN_PREFIX_LENGTH);
    }
}
