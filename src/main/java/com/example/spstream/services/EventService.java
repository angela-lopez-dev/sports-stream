package com.example.spstream.services;

import com.example.spstream.entities.Event;
import com.example.spstream.repositories.EventRepository;
import com.example.spstream.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public Event createEvent(Event event) throws ResponseStatusException {
        if (userRepository.existsById(event.getOrganiserId()))
            return eventRepository.save(event);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("user %s does not exist", event.getOrganiserId()));
    }

    public List<Event> findAllEvents(){
       return StreamSupport.stream(eventRepository.findAll().spliterator(),false).collect(Collectors.toList());
    }

    public Event findEvent(String id) throws ResponseStatusException {
        return eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("event %s does not exist", id)));
    }

}
