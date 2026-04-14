package com.back.nbe9112team06.domain.member.controller;

import com.back.nbe9112team06.domain.member.entity.Member;
import com.back.nbe9112team06.domain.member.entity.TimezoneType;
import com.back.nbe9112team06.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
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
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 - 성공 시 201 Created 와 응답 데이터를 반환한다")
    void signup_success() throws Exception {
        // given
        String email = "newuser@example.com";
        String password = "securePass123";
        String nickname = "newbie";
        TimezoneType timezone = TimezoneType.ASIA_SEOUL;

        // when
        ResultActions resultActions = mvc
                .perform(
                        post("/api/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "%s",
                                            "password": "%s",
                                             "nickname": "%s",
                                            "timezone": "%s"
                                        }
                                        """.formatted(email, password, nickname,timezone.name())
                                )
                )
                .andDo(print());

        // then - 핵심 기능 검증만 수행 (handler 검증 제거)
        resultActions
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.memberId").exists())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.message").value("Successfully created account"));

        // DB 검증: 실제 저장되었는지 확인 (비즈니스 로직 검증)
        Member savedMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AssertionError("Member not saved in DB"));

        assertThat(savedMember.getNickname()).isEqualTo(nickname);
        assertThat(savedMember.getTimezone()).isEqualTo(timezone);
        // 비밀번호는 암호화되어 저장되었는지 확인 (보안 검증)
        assertThat(savedMember.getPasswordHash()).isNotEqualTo(password);
        assertThat(savedMember.getPasswordHash()).startsWith("$2a$"); // BCrypt prefix
    }
}