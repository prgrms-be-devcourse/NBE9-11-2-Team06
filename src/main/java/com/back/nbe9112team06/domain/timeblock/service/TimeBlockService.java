package com.back.nbe9112team06.domain.timeblock.service;

import com.back.nbe9112team06.domain.meeting.entity.Meeting;
import com.back.nbe9112team06.domain.participant.entity.Participant;
import com.back.nbe9112team06.domain.timeblock.dto.TimeBlockRequest;
import com.back.nbe9112team06.domain.timeblock.entity.TimeBlock;
import com.back.nbe9112team06.domain.timeblock.repository.AvailableDateTimeRepository;
import com.back.nbe9112team06.domain.timeblock.repository.AvailableTimeRepository;
import com.back.nbe9112team06.domain.timeblock.repository.TimeBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TimeBlockService {
    //    private final MeetingRepository meetingRepository;
//    private final ParticipantRepository participantRepository;
    private final TimeBlockRepository timeBlockRepository;
    private final AvailableDateTimeRepository availableDateTimeRepository;
    private final AvailableTimeRepository availableTimeRepository;

    @Transactional
    public void registerTimeBlock(Integer meetingId, TimeBlockRequest request) {
        // 이 모임이 존재하는지
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 모임입니다."));

        // 요청한 사람이 이 모임 참여자인지
        Participant participant = participantRepository.findByMeetingAndGuestNameAndGuestPassword(
                meeting,
                request.getGuestName(),
                request.getGuestPassword())
        .orElseThrow(() -> new RuntimeException("인증 실패"));

        // 시간표를 등록한 적이 있는지
        timeBlockRepository.findByMeetingAndParticipantName(meeting, participant)
                .ifPresent(timeBlock -> {
                    throw new RuntimeException("이미 시간표가 등록되어있습니다");
                });

        // availableDateTime 테이블 검증
        validateAvailableDateTime(request.getAvailableDateTimes());
        // 저장
        TimeBlock timeBlock = TimeBlock.create(meeting, participant);
        timeBlockRepository.save(timeBlock);
    }

    // 검증 메서드
    private void validateAvailableDateTime(List<String> availableDateTimes){
        // 중복 검증
        Set<String> set = new HashSet<>(availableDateTimes);
        if(set.size() != availableDateTimes.size()){
            throw new RuntimeException("시간이 중복됩니다.");
        }

        // 날짜 형식 검증
        for(String dateTimeStr : availableDateTimes){
            LocalDateTime dateTime;
            try {
                dateTime = LocalDateTime.parse(dateTimeStr, // 문자열을 LocalDateTime 객체로 변환
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }catch (DateTimeParseException e) {
                throw new RuntimeException("잘못된 날짜/시간 형식입니다.");
            }

            // 과거 날짜 검증
            if(dateTime.isBefore(LocalDateTime.now())){
                throw new RuntimeException("현재 날짜보다 과거입니다.");
            }

            // 30분 단위 검증
            if(dateTime.getMinute()%30 != 0) {
                throw new RuntimeException("30분 단위가 아닙니다.");
            }
        }

    }

}
