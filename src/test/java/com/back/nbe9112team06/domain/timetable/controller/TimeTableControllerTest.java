package com.back.nbe9112team06.domain.timetable.controller;

import com.back.nbe9112team06.domain.timeblock.repository.TimeBlockRepository;
import com.back.nbe9112team06.domain.timetable.repository.TimeTableRepository;
import com.back.nbe9112team06.domain.timetable.service.TimeTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class TimeTableControllerTest {

    @Autowired
    TimeTableService timeTableService;

    @Autowired
    TimeBlockRepository timeBlockRepository;

    @Autowired
    TimeTableRepository timeTableRepository;

    @Test
    void aggregate_테스트() {
        // given
        Integer meetingId = 1;



    }
}