package com.pegasus.application.mocks.dto;

import com.pegasus.application.dto.RunDto;

import java.util.Date;

public class RunDtoMock {
    public static RunDto createMockRunDto(Long id) {
        return RunDto.builder()
                .id(id)
                .durationInSeconds(3600L)
                .caloriesBurnt(500D)
                .paceMinInKm(5D)
                .distanceInKm(10D)
                .locationCity("New York")
                .encodedPolyLine("abc123xyz")
                .startDateTime(new Date())
                .endDateTime(new Date(System.currentTimeMillis() + 3600000)) // Adding one hour to the current time
                .build();
    }
}
