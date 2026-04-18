package com.back.nbe9112team06.global.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // /api/ 경로가 아니면 필터 스킵
        if (!uri.startsWith("/api/")) return true;

        // 인증이 필요한 경로만 필터 실행
        // 나머지는 토큰 있으면 인증, 없으면 익명으로 자연스럽게 통과
        return false;
        // shouldNotFilter = false → 모든 /api/ 경로에서 필터 실행
        // 토큰 있으면 SecurityContext 채움, 없으면 그냥 통과
        // → 인증 필요 경로는 SecurityConfig가 차단
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveTokenFromCookie(request);

        if (token != null) {
            Claims payload = jwtTokenProvider.getPayload(token);

            if (payload != null) {
                int id          = payload.get("id", Integer.class);
                String nickname = payload.get("nickname", String.class);

                SecurityUser securityUser = new SecurityUser(id, nickname);

                // authorities 빈 리스트 — 역할 검증 없음
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        securityUser,
                        null,
                        List.of()
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> "accessToken".equals(c.getName()))
                .map(Cookie::getValue)
                .filter(v -> !v.isBlank())
                .findFirst()
                .orElse(null);
    }
}