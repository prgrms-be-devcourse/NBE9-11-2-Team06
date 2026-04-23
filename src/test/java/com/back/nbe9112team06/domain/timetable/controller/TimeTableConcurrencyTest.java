package com.back.nbe9112team06.domain.timetable.controller;

import com.back.nbe9112team06.domain.timetable.dto.TimeTableResponse;
import com.back.nbe9112team06.domain.timetable.repository.TimeTableRepository;
import com.back.nbe9112team06.domain.timetable.service.TimeTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Sql(value = "/cleanup.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(value = "/timetable-test-data.sql", executionPhase = BEFORE_TEST_METHOD)
class TimeTableConcurrencyTest {

    @Autowired
    TimeTableService timeTableService;

    @Autowired
    TimeTableRepository timeTableRepository;

    @Test
    void aggregate_동시성_실전형_검증() throws InterruptedException {

        int repeat = 30;

        for (int r = 0; r < repeat; r++) {

            // 🔥 초기 상태 세팅
            timeTableRepository.deleteAll();
            timeTableService.aggregate(1);
            TimeTableResponse expected = timeTableService.getTimeTable(1);

            int threadCount = 50;

            ExecutorService executor = Executors.newFixedThreadPool(20);

            // 🔥 동시에 시작시키기
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startLatch.await(); // 🔥 여기서 대기 → 동시에 출발

                        // 💡 현실적인 사용자 딜레이 (0~100ms 랜덤)
                        Thread.sleep((long) (Math.random() * 100));

                        timeTableService.aggregate(1);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        endLatch.countDown();
                    }
                });
            }

            // 🔥 전체 동시에 시작
            startLatch.countDown();

            endLatch.await();

            // 🔍 결과 확인
            TimeTableResponse actual = timeTableService.getTimeTable(1);

            var tables = timeTableRepository.findByMeeting_Id(1);
            if (tables.size() != 1) {
                System.err.println("❗❗❗ [구조 깨짐] iteration=" + r + ", tableCount=" + tables.size());
                throw new AssertionError("TimeTable 개수 이상");
            }

            if (!actual.equals(expected)) {
                System.err.println("❗❗❗ [정합성 깨짐] iteration=" + r);

                int expectedCount = extractTotalCount(expected);
                int actualCount = extractTotalCount(actual);

                System.err.println("expected=" + expectedCount);
                System.err.println("actual=" + actualCount);

                throw new AssertionError("동시성 문제 발생");
            }
        }
    }
    private int extractTotalCount(TimeTableResponse response) {
        return response.availableDateTimes().stream()
                .flatMap(d -> d.availableTimeInfos().stream())
                .mapToInt(t -> t.participants().size())
                .sum();
    }
}
