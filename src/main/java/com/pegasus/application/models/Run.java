package com.pegasus.application.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder=true)
@Table(name = "run")
public class Run {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Long durationInSeconds;

    @NotNull
    private Double caloriesBurnt;

    @NotNull
    private Double paceMinInKm;

    @NotNull
    private Double distanceInKm;

    @NotBlank
    private String locationCity;

    @NotNull
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String encodedPolyLine;

    @NotNull
    private Date startDateTime;

    @NotNull
    private Date endDateTime;
}
