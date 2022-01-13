package com.example.spstream.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Entity
public class Event {
    @Id @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
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
