package com.back.nbe9112team06.domain.meeting.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class MeetingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("모임방 생성 - 성공 시 201과 랜덤 URL을 반환한다")
    void createMeeting_success() throws Exception {
        Member member = memberRepository.save(new Member(
                "creator@example.com",
                "hashedPassword",
                "creator",
                TimezoneType.ASIA_SEOUL
        ));

        ResultActions resultActions = mvc.perform(
                        post("/api/meetings")
                                .header("X-Member-Id", member.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "팀 회의",
                                          "startDate": "2026-04-20",
                                          "endDate": "2026-04-22",
                                          "duration": 60,
                                          "category": "PROJECT"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("모임방 생성 성공"))
                .andExpect(jsonPath("$.data.meetingId").isNumber())
                .andExpect(jsonPath("$.data.roomUrl").isString());
    }

    @Test
    @DisplayName("모임방 생성 - 시작일이 종료일보다 늦으면 400을 반환한다")
    void createMeeting_fail_whenStartDateAfterEndDate() throws Exception {
        Member member = memberRepository.save(new Member(
                "creator2@example.com",
                "hashedPassword",
                "creator2",
                TimezoneType.ASIA_SEOUL
        ));

        mvc.perform(
                        post("/api/meetings")
                                .header("X-Member-Id", member.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "팀 회의",
                                          "startDate": "2026-04-25",
                                          "endDate": "2026-04-22",
                                          "duration": 60,
                                          "category": "PROJECT"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("모임방 생성 - 존재하지 않는 회원이면 404를 반환한다")
    void createMeeting_fail_whenMemberNotFound() throws Exception {
        mvc.perform(
                        post("/api/meetings")
                                .header("X-Member-Id", 999999)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "팀 회의",
                                          "startDate": "2026-04-20",
                                          "endDate": "2026-04-22",
                                          "duration": 60,
                                          "category": "PROJECT"
                                        }
                                        """)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("랜덤 URL 조회 - 존재하는 모임방이면 200과 상세 정보를 반환한다")
    void getMeetingByRandomUrl_success() throws Exception {
        Member member = memberRepository.save(new Member(
                "creator3@example.com",
                "hashedPassword",
                "creator3",
                TimezoneType.ASIA_SEOUL
        ));

        String createResponse = mvc.perform(
                        post("/api/meetings")
                                .header("X-Member-Id", member.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "title": "입장 테스트 방",
                                          "startDate": "2026-04-20",
                                          "endDate": "2026-04-23",
                                          "duration": 45,
                                          "category": "STUDY"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String roomUrl = extractValue(createResponse, "\"roomUrl\":\"", "\"");

        mvc.perform(get("/api/meetings/{randomUrl}", roomUrl))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("모임방 조회 성공"))
                .andExpect(jsonPath("$.data.roomUrl").value(roomUrl))
                .andExpect(jsonPath("$.data.title").value("입장 테스트 방"))
                .andExpect(jsonPath("$.data.startDate").value("2026-04-20"))
                .andExpect(jsonPath("$.data.endDate").value("2026-04-23"));
    }

    @Test
    @DisplayName("랜덤 URL 조회 - 존재하지 않는 모임방이면 404를 반환한다")
    void getMeetingByRandomUrl_fail_whenNotFound() throws Exception {
        mvc.perform(get("/api/meetings/{randomUrl}", "notExistsUrl"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private String extractValue(String raw, String startToken, String endToken) {
        int start = raw.indexOf(startToken);
        if (start == -1) {
            throw new IllegalStateException("응답에서 roomUrl을 찾을 수 없습니다.");
        }
        start += startToken.length();
        int end = raw.indexOf(endToken, start);
        return raw.substring(start, end);
    }
}
