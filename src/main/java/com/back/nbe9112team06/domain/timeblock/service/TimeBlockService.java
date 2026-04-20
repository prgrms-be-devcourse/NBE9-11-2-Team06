package com.back.nbe9112team06.domain.timeblock.service;

import com.back.nbe9112team06.domain.timeblock.dto.ParticipantsScheduleResponse;
import com.back.nbe9112team06.domain.timeblock.dto.TimeRangeResponse;
import com.back.nbe9112team06.domain.timeblock.entity.AvailableDateTime;
import com.back.nbe9112team06.domain.timeblock.entity.AvailableTime;
import com.back.nbe9112team06.domain.timeblock.entity.TimeBlock;
import com.back.nbe9112team06.domain.timeblock.repository.TimeBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class TimeBlockService {

    private final TimeBlockRepository timeBlockRepository;

    @Transactional(readOnly = true)
    public List<ParticipantsScheduleResponse> getParticipantSchedules(Integer meetingId) {
        List<TimeBlock> timeBlocks = timeBlockRepository.findWithAll(meetingId);
        List<ParticipantsScheduleResponse> result = new ArrayList<>();

        for (TimeBlock timeBlock : timeBlocks) {
            String name = timeBlock.getParticipant().getGuestName();

            Map<LocalDate, List<LocalTime>> dateToSlots = new TreeMap<>();
            for (AvailableDateTime adt : timeBlock.getAvailableDateTimes()) {
                adt.getAvailableTimes().stream()
                        .map(AvailableTime::getTime)
                        .forEach(t -> dateToSlots
                                .computeIfAbsent(adt.getDate(), k -> new ArrayList<>())
                                .add(t));
            }

            List<TimeRangeResponse> ranges = new ArrayList<>();
            for (var entry : dateToSlots.entrySet()) {
                List<LocalTime> sorted = entry.getValue().stream().sorted().toList();
                ranges.addAll(toRanges(entry.getKey(), sorted));
            }

            result.add(new ParticipantsScheduleResponse(name, ranges));
        }

        return result;
    }

    private List<TimeRangeResponse> toRanges(LocalDate date, List<LocalTime> slots) {
        List<TimeRangeResponse> ranges = new ArrayList<>();
        if (slots.isEmpty()) return ranges;

        LocalTime start = slots.get(0);
        LocalTime prev = slots.get(0);

        for (int i = 1; i < slots.size(); i++) {
            LocalTime curr = slots.get(i);
            if (!curr.equals(prev.plusMinutes(30))) {
                ranges.add(new TimeRangeResponse(date, start, prev.plusMinutes(30)));
                start = curr;
            }
            prev = curr;
        }
        ranges.add(new TimeRangeResponse(date, start, prev.plusMinutes(30)));
        return ranges;
    }
}
