package com.back.nbe9112team06.domain.participant.service;

import com.back.nbe9112team06.domain.meeting.entity.Meeting;
import com.back.nbe9112team06.domain.meeting.repository.MeetingRepository;
import com.back.nbe9112team06.domain.participant.dto.request.ParticipantJoinRequest;
import com.back.nbe9112team06.domain.participant.dto.response.ParticipantJoinResponse;
import com.back.nbe9112team06.domain.participant.entity.Participant;
import com.back.nbe9112team06.domain.participant.repository.ParticipantRepository;
import com.back.nbe9112team06.global.error.ErrorCode;
import com.back.nbe9112team06.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public ParticipantJoinResponse joinMeeting(String randomUrl, ParticipantJoinRequest request) {
        Meeting meeting = meetingRepository.findByRandomUrl(randomUrl)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        Participant participant = Participant.create(request.guestName(), request.guestPassword());
        meeting.addParticipant(participant);

        Participant saved = participantRepository.save(participant);
        return new ParticipantJoinResponse(saved.getId(), saved.getGuestName());
    }
}
