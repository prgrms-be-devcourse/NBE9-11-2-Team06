package com.back.nbe9112team06.domain.timeblock.entity;

import com.back.nbe9112team06.domain.meeting.entity.Meeting;
import com.back.nbe9112team06.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class AvailableTime extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "available_date_time_id")
    private AvailableDateTime availableDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_block_id")
    private TimeBlock timeBlock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    private LocalTime time;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;
}