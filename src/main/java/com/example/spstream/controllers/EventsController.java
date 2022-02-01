package com.example.spstream.controllers;

import com.example.spstream.dto.EventDTO;
import com.example.spstream.entities.Event;
import com.example.spstream.services.EventService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/events")
public class EventsController {
    @Autowired
    private EventService eventService;

    ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody Event event) {
        EventDTO eventDTO = modelMapper.map(eventService.createEvent(event), EventDTO.class);
        return new ResponseEntity<>(eventDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents(){
        List<EventDTO> allEventsDTO = eventService.findAllEvents().stream().map(event -> modelMapper.map(event, EventDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<>(allEventsDTO, HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable("id") String id){
        EventDTO eventDTO = modelMapper.map(eventService.findEvent(id), EventDTO.class);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

}
