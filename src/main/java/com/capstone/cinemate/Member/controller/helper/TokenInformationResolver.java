package com.capstone.cinemate.Member.controller.helper;

import java.util.Optional;

import com.capstone.cinemate.Member.domain.Member;
import com.capstone.cinemate.Member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenInformationResolver implements HandlerMethodArgumentResolver {

    private static final String HEADER_NAME = "Authorization";

    private final MemberService memberService;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(TokenInformation.class)
                && parameter.getGenericParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory)
            throws Exception {
        Optional<String> authorization = Optional.ofNullable(webRequest.getHeader(HEADER_NAME));
        String token = TokenExtractor.extractToken(authorization);
        Member member = memberService.findMemberByJwt(token);
        return member.getId();
    }
}
