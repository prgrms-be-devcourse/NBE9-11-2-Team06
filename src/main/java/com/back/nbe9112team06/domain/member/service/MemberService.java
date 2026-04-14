package com.back.nbe9112team06.domain.member.service;

import com.back.nbe9112team06.domain.member.dto.SignupRequest;
import com.back.nbe9112team06.domain.member.dto.SignupResponse;
import com.back.nbe9112team06.domain.member.dto.request.CheckEmailRequest;
import com.back.nbe9112team06.domain.member.dto.response.AvailabilityResponse;
import com.back.nbe9112team06.domain.member.entity.Member;
import com.back.nbe9112team06.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request){

//        if (memberRepository.existsByEmail(request.email())) {
//            throw new ConflictException("This email " + request.email() + " is already existed!");
//        }
//
//        if (memberRepository.existsByNickname(request.nickname())) {
//            throw new ConflictException("This nickname " + request.nickname() + " is already existed!");
//        }

        // 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(request.password());

        // 사용자 생성 및 저장
        Member user = new Member(request.email(), hashedPassword, request.nickname(), request.timezone());
        Member saved = memberRepository.save(user);

        return new SignupResponse(saved.getId(), saved.getEmail(), "Successfully created account");
    }

    public AvailabilityResponse checkEmail(CheckEmailRequest request) {
        return new AvailabilityResponse(memberRepository.existsByEmail(request.email()));
    }

//   Jwt 인증 구현 후에
//    @Transactional
//    public MemberDeleteResponse deleteUser(int memberId) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new NotFoundException("This user could not be found."));
//
//        memberRepository.delete(member);
//        return new MemberDeleteResponse("Successfully deleted account", true);
//    }

//    추후에 비밀번호 변경 지원시
//    @Transactional
//    public MessageResponse updatePassword(int memberId, UpdatePasswordRequest request) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new NotFoundException("This user could not be found."));
//
//        String hashedPassword = passwordEncoder.encode(request.password());
//        member.updatePassword(hashedPassword);
//        return new MessageResponse("Passcode change successful.");
//    }
}
