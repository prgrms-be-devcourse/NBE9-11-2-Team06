package com.back.nbe9112team06.domain.auth.controller;

import com.back.nbe9112team06.domain.auth.dto.LoginRequest;
import com.back.nbe9112team06.domain.auth.dto.LoginResponse;
import com.back.nbe9112team06.domain.auth.dto.LoginResult;
import com.back.nbe9112team06.domain.auth.service.AuthService;
import com.back.nbe9112team06.domain.member.entity.Member;
import com.back.nbe9112team06.global.response.ApiResponse;
import com.back.nbe9112team06.global.rq.Rq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final Rq rq;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request){

       LoginResult result = authService.login(request);
        rq.issueAccessTokenCookie(result.accessToken());

        return new ApiResponse<>("201-1","로그인 성공",
                new LoginResponse(result.memberId(), result.nickname())
        );
    }

    @GetMapping("/me")
    public ApiResponse<LoginResponse> getMyInfo() {
        Member actor = rq.getActor();
        return new ApiResponse<>(
                "200-1","조회 성공", new LoginResponse(actor.getId(), actor.getNickname())
        ); // 추후에 개인정보 추가
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        rq.clearAccessTokenCookie();
        return new ApiResponse<>("200-1", "로그아웃 성공",null);
    }
}