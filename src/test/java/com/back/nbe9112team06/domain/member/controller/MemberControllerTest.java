package com.back.nbe9112team06.domain.member.controller;

import com.back.nbe9112team06.domain.member.entity.Member;
import com.back.nbe9112team06.domain.member.entity.TimezoneType;
import com.back.nbe9112team06.domain.member.repository.MemberRepository;
import com.back.nbe9112team06.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    // ✅ 공통 테스트 데이터
    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD = "securePass123!";
    private static final String VALID_NICKNAME = "tester";
    private static final TimezoneType VALID_TIMEZONE = TimezoneType.ASIA_SEOUL;

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("회원가입 - 성공 시 201 Created 와 응답 데이터를 반환한다")
        void signup_success() throws Exception {

            ResultActions result = performSignup(VALID_EMAIL, VALID_PASSWORD, VALID_NICKNAME, VALID_TIMEZONE);


            result
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.memberId").exists())
                    .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                    .andExpect(jsonPath("$.message").value("Successfully created account"));

            // DB 검증: 실제 저장되었는지 확인 (비즈니스 로직 검증)
            Member savedMember = memberRepository.findByEmail(VALID_EMAIL)
                    .orElseThrow(() -> new AssertionError("Member not saved in DB"));

            assertThat(savedMember.getNickname()).isEqualTo(VALID_NICKNAME);
            assertThat(savedMember.getTimezone()).isEqualTo(VALID_TIMEZONE);
            // 비밀번호는 암호화되어 저장되었는지 확인 (보안 검증)
            assertThat(savedMember.getPasswordHash()).startsWith("$2a$"); // BCrypt prefix
        }

        @Test
        @DisplayName("이메일 중복 시 409 Conflict 와 오류 코드를 반환한다")
        void signup_duplicate_email() throws Exception {
            // given: 기존 사용자 저장
            memberRepository.save(new Member(VALID_EMAIL, "encoded", "existing", VALID_TIMEZONE));

            // when: 동일 이메일로 가입 시도
            ResultActions result = performSignup(VALID_EMAIL, VALID_PASSWORD, "newbie", VALID_TIMEZONE);

            // then
            result
                    .andExpect(status().isConflict())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.errorCode").value(ErrorCode.DUPLICATE_EMAIL.getCode()))
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.detail").value("이미 등록된 이메일입니다."));
        }

        @Test
        @DisplayName("닉네임 중복은 허용되어 201 Created 를 반환한다")
        void signup_duplicate_nickname_allowed() throws Exception {
            // given: 기존 사용자 저장 (동일 닉네임)
            memberRepository.save(new Member("other@example.com", "encoded", VALID_NICKNAME, VALID_TIMEZONE));

            // when: 동일 닉네임으로 가입 시도 (이메일은 다르게)
            ResultActions result = performSignup("another@example.com", VALID_PASSWORD, VALID_NICKNAME, VALID_TIMEZONE);

            // then: 성공해야 함 (닉네임 중복 허용 정책)
            result
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("another@example.com"));
        }

        @Test
        @DisplayName("이메일 형식이 잘못되면 400 Bad Request 를 반환한다")
        void signup_invalid_email_format() throws Exception {
            // when
            ResultActions result = performSignup("not-an-email", VALID_PASSWORD, VALID_NICKNAME, VALID_TIMEZONE);

            // then
            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(ErrorCode.VALIDATION_FAILED.getCode()))
                    .andExpect(jsonPath("$.validationErrors[?(@.field=='email')]").exists());
        }

        @Test
        @DisplayName("필수 필드가 누락되면 400 Bad Request 를 반환한다")
        void signup_missing_required_fields() throws Exception {
            // when: email 필드 누락
            String invalidJson = """
                    {
                        "password": "%s",
                        "nickname": "%s",
                        "timezone": "%s"
                    }
                    """.formatted(VALID_PASSWORD, VALID_NICKNAME, VALID_TIMEZONE.name());

            ResultActions result = mvc.perform(post("/api/members")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andDo(print());

            // then
            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value(ErrorCode.VALIDATION_FAILED.getCode()))
                    .andExpect(jsonPath("$.validationErrors").isArray())
                    .andExpect(jsonPath("$.validationErrors[?(@.field=='email')]").exists());
        }

        @Test
        @DisplayName("비밀번호가 8자 미만이면 400 Bad Request 를 반환한다")
        void signup_short_password() throws Exception {
            // when
            ResultActions result = performSignup(VALID_EMAIL, "short", VALID_NICKNAME, VALID_TIMEZONE);

            // then
            result
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors[?(@.field=='password')].message")
                            .value("비밀번호는 8자 이상 20자 이하여야 합니다."));
        }
    }

    // 헬퍼 메서드
    private ResultActions performSignup(String email, String password, String nickname, TimezoneType timezone) throws Exception {
        return mvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "%s",
                                    "password": "%s",
                                    "nickname": "%s",
                                    "timezone": "%s"
                                }
                                """.formatted(email, password, nickname, timezone.name())))
                .andDo(print());
    }
}