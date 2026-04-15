package com.back.nbe9112team06.domain.participant.repository;

import com.back.nbe9112team06.domain.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
}

