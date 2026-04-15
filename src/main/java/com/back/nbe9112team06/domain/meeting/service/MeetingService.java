package com.back.nbe9112team06.domain.meeting.service;

import com.back.nbe9112team06.domain.meeting.dto.request.MeetingCreateRequest;
import com.back.nbe9112team06.domain.meeting.dto.response.MeetingCreateResponse;
import com.back.nbe9112team06.domain.meeting.dto.response.MeetingEntryResponse;
import com.back.nbe9112team06.domain.meeting.entity.Meeting;
import com.back.nbe9112team06.domain.meeting.entity.MeetingsDate;
import com.back.nbe9112team06.domain.meeting.repository.MeetingRepository;
import com.back.nbe9112team06.domain.member.entity.Member;
import com.back.nbe9112team06.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private static final String URL_CHAR_POOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int URL_LENGTH = 10;

    private final MeetingRepository meetingRepository;
    private final MemberRepository memberRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public MeetingCreateResponse createMeeting(Integer memberId, MeetingCreateRequest request) {
        /*
         * 임시 인증 연동 메모 (팀 협업용):
         * - 현재 프로젝트의 JWT 인증 파트는 다른 팀원이 구현 예정이라, 이 시점에서는 SecurityContext에서
         *   사용자 식별값(memberId/email)을 꺼낼 수 없습니다.
         * - 그래서 지금은 컨트롤러에서 임시로 전달받은 memberId를 사용해 생성자를 식별합니다.
         * - JWT가 붙으면 이 메서드 시그니처는 유지하고, 컨트롤러에서
         *   "토큰 -> 인증 객체 -> memberId 추출"로 주입 방식만 교체하면 됩니다.
         * - 즉, 방 생성 비즈니스 로직(검증/URL 생성/저장)은 지금 완성하고, 인증 연결점만 추후 교체 가능한 구조입니다.
         */
        if (request.startDate().isAfter(request.endDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 늦을 수 없습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."));

        String randomUrl = generateUniqueUrl();
        Meeting meeting = Meeting.create(
                request.title(),
                request.category(),
                request.duration(),
                member,
                randomUrl
        );

        LocalDate cursor = request.startDate();
        while (!cursor.isAfter(request.endDate())) {
            MeetingsDate meetingsDate = MeetingsDate.create(cursor, member.getEmail());
            meeting.addMeetingsDate(meetingsDate);
            cursor = cursor.plusDays(1);
        }

        Meeting saved = meetingRepository.save(meeting);
        return new MeetingCreateResponse(saved.getId(), saved.getRandomUrl());
    }

    @Transactional(readOnly = true)
    public MeetingEntryResponse getMeetingByRandomUrl(String randomUrl) {
        Meeting meeting = meetingRepository.findByRandomUrl(randomUrl)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 모임방을 찾을 수 없습니다."));

        LocalDate startDate = meeting.getMeetingsDates().stream()
                .map(MeetingsDate::getDate)
                .min(Comparator.naturalOrder())
                .orElse(null);

        LocalDate endDate = meeting.getMeetingsDates().stream()
                .map(MeetingsDate::getDate)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return new MeetingEntryResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getCategory(),
                meeting.getDuration(),
                meeting.getStatus(),
                meeting.getRandomUrl(),
                startDate,
                endDate
        );
    }

    private String generateUniqueUrl() {
        String candidate = randomString(URL_LENGTH);
        while (meetingRepository.existsByRandomUrl(candidate)) {
            candidate = randomString(URL_LENGTH);
        }
        return candidate;
    }

    private String randomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = secureRandom.nextInt(URL_CHAR_POOL.length());
            builder.append(URL_CHAR_POOL.charAt(idx));
        }
        return builder.toString();
    }
}
