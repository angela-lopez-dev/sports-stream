package com.example.spstream.beans.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Event {
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
    private int organiserId;
}
