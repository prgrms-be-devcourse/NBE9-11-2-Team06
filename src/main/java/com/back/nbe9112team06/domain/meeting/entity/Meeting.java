package com.back.nbe9112team06.domain.meeting.entity;

import com.back.nbe9112team06.domain.member.entity.Member;
import com.back.nbe9112team06.domain.participant.entity.Participant;
import com.back.nbe9112team06.domain.timeblock.entity.TimeBlock;
import com.back.nbe9112team06.domain.timetable.entity.TimeTable;
import com.back.nbe9112team06.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Meeting extends BaseEntity {

    private String title;
    private String category;

    @Column(name = "local_time")
    private String localTime;

    private String status;

    @Column(name = "random_url")
    private String randomUrl;

    private Integer duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "meeting", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "meeting", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<MeetingsDate> meetingsDates = new ArrayList<>();

    @OneToMany(mappedBy = "meeting", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<TimeBlock> timeBlocks = new ArrayList<>();

    @OneToMany(mappedBy = "meeting", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<TimeTable> timeTables = new ArrayList<>();

    public static Meeting create(String title, String category, Integer duration, Member member, String randomUrl) {
        Meeting meeting = new Meeting();
        meeting.title = title;
        meeting.category = category;
        meeting.duration = duration;
        meeting.member = member;
        meeting.randomUrl = randomUrl;
        meeting.status = "OPEN";
        return meeting;
    }

    public void addMeetingsDate(MeetingsDate meetingsDate) {
        meetingsDates.add(meetingsDate);
        meetingsDate.assignMeeting(this);
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.assignMeeting(this);
    }
}
