package com.pegasus.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder=true)
public class RunDto {

    private Long id;
    private String title;
    private String description;
    private Long durationInSeconds;
    private Double caloriesBurnt;
    private Double paceMinInKm;
    private Double distanceInKm;
    private String locationCity;
    private String encodedPolyLine;
    private Date startDateTime;
    private Date endDateTime;

}
