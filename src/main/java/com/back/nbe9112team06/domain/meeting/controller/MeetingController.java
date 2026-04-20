package com.back.nbe9112team06.domain.meeting.controller;

import com.back.nbe9112team06.domain.meeting.dto.request.MeetingCreateRequest;
import com.back.nbe9112team06.domain.meeting.dto.response.MeetingCreateResponse;
import com.back.nbe9112team06.domain.meeting.dto.response.MeetingEntryResponse;
import com.back.nbe9112team06.domain.meeting.service.MeetingService;
import com.back.nbe9112team06.domain.member.entity.Member;
import com.back.nbe9112team06.global.response.ApiResponse;
import com.back.nbe9112team06.global.rq.Rq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Tag(name = "Meetings", description = "모임방 생성 API")
public class MeetingController {

    private final MeetingService meetingService;
    private final Rq rq;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "모임방 생성")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "모임방 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    public ApiResponse<MeetingCreateResponse> createMeeting(
            @RequestBody @Valid MeetingCreateRequest request
    ) {
        Member actor = rq.getActor();
        Integer memberId = actor.getId();
        MeetingCreateResponse response = meetingService.createMeeting(memberId, request);
        return new ApiResponse<>("201-1", "모임방 생성 성공", response);
    }

    @GetMapping("/{randomUrl}")
    @Operation(summary = "랜덤 URL로 모임방 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "모임방 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임방을 찾을 수 없음")
    })
    public ApiResponse<MeetingEntryResponse> getMeetingByRandomUrl(@PathVariable String randomUrl) {
        MeetingEntryResponse response = meetingService.getMeetingByRandomUrl(randomUrl);
        return new ApiResponse<>("200-1", "모임방 조회 성공", response);
    }
}
