package com.back.nbe9112team06.domain.timeblock.repository;

import com.back.nbe9112team06.domain.participant.entity.Participant;
import com.back.nbe9112team06.domain.timeblock.entity.TimeBlock;
import com.back.nbe9112team06.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeBlockRepository extends JpaRepository<TimeBlock, Integer> {

    // 중복등록체크 (같은 모임에 같은 이름으로 이미 등록했는지)
    Optional<TimeBlock> findByMeetingAndParticipant(Meeting meeting, Participant participant);


}