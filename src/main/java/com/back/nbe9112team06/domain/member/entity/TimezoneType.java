package com.back.nbe9112team06.domain.member.entity;

public enum TimezoneType {
    ASIA_SEOUL("Asia/Seoul"),
    UTC("UTC"),
    AMERICA_NEW_YORK("America/New_York"),
    AMERICA_LOS_ANGELES("America/Los_Angeles");

    private final String value;

    TimezoneType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}