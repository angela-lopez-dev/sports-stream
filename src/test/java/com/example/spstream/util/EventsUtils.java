package com.example.spstream.util;

import com.example.spstream.entities.Event;

import java.time.LocalDateTime;

public class EventsUtils {

    public static void setEventDateToTomorrow(Event event) {
        event.setDateTime(LocalDateTime.now().plusDays(1));
    }

}
