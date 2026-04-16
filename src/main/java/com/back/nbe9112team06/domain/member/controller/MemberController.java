package com.back.nbe9112team06.domain.member.controller;

import com.back.nbe9112team06.domain.member.dto.SignupRequest;
import com.back.nbe9112team06.domain.member.dto.SignupResponse;
import com.back.nbe9112team06.domain.member.dto.request.CheckEmailRequest;
import com.back.nbe9112team06.domain.member.dto.response.AvailabilityResponse;
import com.back.nbe9112team06.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "사용자 관리 API")
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패 - VALIDATION-001"),
            @ApiResponse(responseCode = "409", description = "이메일 중복 시 발생 - MEMBER-001")
    })
    public SignupResponse signup(@RequestBody @Valid SignupRequest request) {
        return memberService.signup(request);
    }

    @PostMapping("/check-email")
    @Operation(summary = "이메일 중복 체크")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "체크 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패")
    })
    public AvailabilityResponse checkEmail(@RequestBody @Valid CheckEmailRequest request) {
        return memberService.checkEmail(request);
    }

//    @DeleteMapping("/me")
//    @Operation(summary = "회원 탈퇴 (자기 자신)")
//    @SecurityRequirement(name = "bearerAuth")
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "탈퇴 성공"),
//            @ApiResponse(responseCode = "401", description = "인증 실패"),
//            @ApiResponse(responseCode = "404", description = "회원 없음")
//    })
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void withdrawMyAccount(
//            @AuthenticationPrincipal CustomUserDetails userDetails
//    ) {
//        memberService.withdraw(userDetails.getMemberId());
//        // ✅ 204 No Content 반환 (body 없음)
//    }

}
