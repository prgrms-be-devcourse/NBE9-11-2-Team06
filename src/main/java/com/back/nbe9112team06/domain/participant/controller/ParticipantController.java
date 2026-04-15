package com.back.nbe9112team06.domain.participant.controller;

import com.back.nbe9112team06.domain.participant.dto.request.ParticipantJoinRequest;
import com.back.nbe9112team06.domain.participant.dto.response.ParticipantJoinResponse;
import com.back.nbe9112team06.domain.participant.service.ParticipantService;
import com.back.nbe9112team06.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meetings/{randomUrl}/participants")
@RequiredArgsConstructor
@Tag(name = "Participants", description = "비회원 모임방 참가 API")
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "모임방 비회원 참가")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "참가 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임방을 찾을 수 없음")
    })
    public ApiResponse<ParticipantJoinResponse> joinMeeting(
            @PathVariable String randomUrl,
            @RequestBody @Valid ParticipantJoinRequest request
    ) {
        ParticipantJoinResponse response = participantService.joinMeeting(randomUrl, request);
        return ApiResponse.success(HttpStatus.CREATED.value(), "모임방 참가 성공", response);
    }
}

