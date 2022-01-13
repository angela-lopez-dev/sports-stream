package com.example.spstream.controllers;

import com.example.spstream.entities.Event;
import com.example.spstream.services.EventService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/events")
public class EventsController {
    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.CREATED);
    }
}
