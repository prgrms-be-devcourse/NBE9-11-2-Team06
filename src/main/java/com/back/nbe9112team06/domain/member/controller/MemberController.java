package com.back.nbe9112team06.domain.member.controller;

import com.back.nbe9112team06.domain.member.dto.SignupRequest;
import com.back.nbe9112team06.domain.member.dto.SignupResponse;
import com.back.nbe9112team06.domain.member.dto.request.CheckEmailRequest;
import com.back.nbe9112team06.domain.member.dto.response.AvailabilityResponse;
import com.back.nbe9112team06.domain.member.entity.Member;
import com.back.nbe9112team06.domain.member.service.MemberService;
import com.back.nbe9112team06.global.response.ApiResponse;
import com.back.nbe9112team06.global.rq.Rq;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "사용자 관리 API")
public class MemberController {
    private final MemberService memberService;
    private final Rq rq;

    @PostMapping
    public ApiResponse<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        Member member = memberService.signup(request);
        return new ApiResponse<>(
                "201-1", "회원가입에 성공하셨습니다",
                new SignupResponse(member.getId(), member.getEmail(),member.getNickname())
        );
    }

    @PostMapping("/check-email")
    public ApiResponse<AvailabilityResponse> checkEmail(@RequestBody @Valid CheckEmailRequest request) {
        boolean isAvailable = !memberService.checkEmail(request);

        return new ApiResponse<>(
                "200-1",
                isAvailable ? "사용 가능한 이메일입니다." : "이미 등록된 이메일입니다.",
                new AvailabilityResponse(isAvailable)
        );
    }

    @DeleteMapping
    public ApiResponse<Void> deleteMember() {
        Member actor = rq.getActor();
        memberService.deleteMember(actor.getId());
        rq.clearAccessTokenCookie();

        return new ApiResponse<>("200-1", "회원 탈퇴가 완료되었습니다.", null);
    }
}
