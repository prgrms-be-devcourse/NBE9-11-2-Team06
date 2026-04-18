package com.back.nbe9112team06.global.security;

import com.back.nbe9112team06.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) ->
                writeProblemDetail(response, request, ErrorCode.UNAUTHORIZED);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) ->
                writeProblemDetail(response, request, ErrorCode.ACCESS_DENIED);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/members").authenticated()
                        // hasRole 제거 — 로그인 여부만 확인
                        .anyRequest().permitAll())
                .csrf(csrf -> csrf.disable()
                        .headers((headers) -> headers
                                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .addFilterBefore(jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                ));

        return http.build();
    }
    /**
     * ✅ Spring Security 예외 발생 시 ProblemDetail(JSON) 응답 작성
     * - ErrorCode 의 toProblemDetail() 사용
     * - getProperties() Map 을 통해 커스텀 필드 접근
     */
    private void writeProblemDetail(
            jakarta.servlet.http.HttpServletResponse response,
            jakarta.servlet.http.HttpServletRequest request,
            ErrorCode errorCode) throws IOException {

        ProblemDetail problemDetail = errorCode.toProblemDetail(
                errorCode.getMessage(),  // detail
                request.getRequestURI()  // instance
        );

        Map<String, Object> properties = problemDetail.getProperties();

        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setStatus(errorCode.getStatus().value());
        response.setCharacterEncoding("UTF-8");

        String json = """
                {
                  "type": "%s",
                  "title": "%s",
                  "status": %d,
                  "detail": "%s",
                  "errorCode": "%s",
                  "timestamp": "%s",
                  "instance": "%s"
                }
                """.formatted(
                problemDetail.getType(),
                problemDetail.getTitle(),
                problemDetail.getStatus(),
                problemDetail.getDetail(),
                Objects.toString(properties.get("errorCode"), errorCode.getCode()), // ✅ getProperties().get() 사용
                Objects.toString(properties.get("timestamp"), ""),
                problemDetail.getInstance()
        );

        response.getWriter().write(json);
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of( "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}