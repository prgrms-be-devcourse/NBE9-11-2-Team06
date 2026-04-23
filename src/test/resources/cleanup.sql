-- FK 무시 (테스트용 핵심🔥)
SET REFERENTIAL_INTEGRITY FALSE;

-- 타임테이블 관련 (가장 하위)
DELETE FROM adjust_result;
DELETE FROM time_info;
DELETE FROM date_info;
DELETE FROM time_table;

-- 시간 블록 관련
DELETE FROM available_time;
DELETE FROM available_date_time;
DELETE FROM time_block;

-- 모임 관련 (참조 먼저 제거)
DELETE FROM meetings_date;   -- ❗ 이거 반드시 포함해야 함
DELETE FROM participant;

-- 부모 테이블
DELETE FROM meeting;
DELETE FROM member;

-- FK 다시 활성화
SET REFERENTIAL_INTEGRITY TRUE;