package com.pegasus.application.mocks.model;

import com.pegasus.application.models.Run;

import java.util.Date;

public class RunMock {
    public static Run createMockRun(Long id) {
        return Run.builder()
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
