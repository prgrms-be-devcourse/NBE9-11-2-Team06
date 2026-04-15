package com.back.nbe9112team06.domain.meeting.controller;

import com.back.nbe9112team06.domain.meeting.dto.request.MeetingCreateRequest;
import com.back.nbe9112team06.domain.meeting.dto.response.MeetingCreateResponse;
import com.back.nbe9112team06.domain.meeting.dto.response.MeetingEntryResponse;
import com.back.nbe9112team06.domain.meeting.service.MeetingService;
import com.back.nbe9112team06.global.dto.ApiResponse;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "모임방 생성")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "모임방 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    public ApiResponse<MeetingCreateResponse> createMeeting(
            @RequestHeader("X-Member-Id") Integer memberId,
            @RequestBody @Valid MeetingCreateRequest request
    ) {
        MeetingCreateResponse response = meetingService.createMeeting(memberId, request);
        return ApiResponse.success(HttpStatus.CREATED.value(), "모임방 생성 성공", response);
    }

    @GetMapping("/{randomUrl}")
    @Operation(summary = "랜덤 URL로 모임방 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "모임방 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임방을 찾을 수 없음")
    })
    public ApiResponse<MeetingEntryResponse> getMeetingByRandomUrl(@PathVariable String randomUrl) {
        MeetingEntryResponse response = meetingService.getMeetingByRandomUrl(randomUrl);
        return ApiResponse.success(HttpStatus.OK.value(), "모임방 조회 성공", response);
    }
}
