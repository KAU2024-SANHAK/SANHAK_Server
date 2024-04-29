package org.kau.kkoolbeeServer.global.auth.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AuthWhiteList {

    public static final List<String> AUTH_WHITELIST_DEFALUT = Arrays.asList(
            "/loading", "/error", "/api/login", "/api/reissue",
            "/health", "/actuator/health", "/"
    );

    public static final List<String> AUTH_WHITELIST_WILDCARD = Arrays.asList(
            "/api/kakao/**", "/api/test/**", "/api/**", //일단 API 다 필터에 안 걸리도록 설정, 나중에 바꾸기
            "/swagger-ui/**", "/swagger-resources/**", "/api-docs/**",
            "/v3/api-docs/**", "/webjars/**"
    );

    public static final String[] AUTH_WHITELIST = Stream.concat(
            AUTH_WHITELIST_DEFALUT.stream(),
            AUTH_WHITELIST_WILDCARD.stream()
    ).toArray(String[]::new);
}
