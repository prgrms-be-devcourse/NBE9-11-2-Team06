package com.back.nbe9112team06.domain.meeting.repository;

import com.back.nbe9112team06.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
}
