package com.example.spstream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String id;

    @JsonProperty("date_time")
    private LocalDateTime dateTime;

    private String title;

    @JsonProperty("min_number_participants")
    private int minNumberParticipants;

    @JsonProperty("max_number_participants")
    private int maxNumberParticipants;

    @JsonProperty("cur_number_participants")
    private int curNumberParticipants;

    private String activity;

    private String localisation;

    @JsonProperty("organiser_id")
    private String organiserId;
}
