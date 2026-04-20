package com.back.nbe9112team06.domain.meeting.service;

import com.back.nbe9112team06.domain.meeting.dto.ConfirmedScheduleResponse;
import com.back.nbe9112team06.domain.meeting.dto.FinalizeRequest;
import com.back.nbe9112team06.domain.meeting.entity.Meeting;
import com.back.nbe9112team06.domain.meeting.entity.MeetingStatus;
import com.back.nbe9112team06.domain.meeting.repository.MeetingRepository;
import com.back.nbe9112team06.global.error.ErrorCode;
import com.back.nbe9112team06.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    @Transactional
    public ConfirmedScheduleResponse confirm(Integer meetingId, Integer memberId, FinalizeRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEETING_NOT_FOUND));

        if (!meeting.isHost(memberId)) {
            throw new BusinessException(ErrorCode.NOT_MEETING_HOST);
        }

        if (meeting.getStatus() == MeetingStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.ALREADY_CONFIRMED);
        }

        meeting.confirm(request.date(), request.time());
        return ConfirmedScheduleResponse.from(request.date(), request.time(), MeetingStatus.CONFIRMED, meeting.getTitle());
    }

    @Transactional
    public void cancelConfirm(Integer meetingId, Integer memberId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEETING_NOT_FOUND));

        if (!meeting.isHost(memberId)) {
            throw new BusinessException(ErrorCode.NOT_MEETING_HOST);
        }

        if (meeting.getStatus() != MeetingStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.NOT_CONFIRMED);
        }

        meeting.cancelConfirm();
    }

    @Transactional(readOnly = true)
    public ConfirmedScheduleResponse getConfirmedSchedule(Integer meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEETING_NOT_FOUND));

        if (meeting.getStatus() != MeetingStatus.CONFIRMED || meeting.getConfirmedDate() == null) {
            throw new BusinessException(ErrorCode.NOT_CONFIRMED);
        }

        return ConfirmedScheduleResponse.from(
                meeting.getConfirmedDate(),
                meeting.getConfirmedTime(),
                meeting.getStatus(),
                meeting.getTitle()
        );
    }
}