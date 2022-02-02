package com.example.spstream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String id;

    @JsonProperty("date_time")
    @NotNull(message = "date is null")
    @Future(message = "date is in the past")
    private LocalDateTime dateTime;
    @NotEmpty(message  = "title is missing")
    private String title;
    @JsonProperty("min_number_participants")
    private int minNumberParticipants;
    @JsonProperty("max_number_participants")
    private int maxNumberParticipants;
    @JsonProperty("cur_number_participants")
    private int curNumberParticipants;
    @NotEmpty(message = "activity is missing")
    private String activity;
    @NotEmpty(message = "localisation is missing")
    private String localisation;
    @JsonProperty("organiser_id")
    @NotEmpty(message = "organiser id is missing")
    private String organiserId;
}
