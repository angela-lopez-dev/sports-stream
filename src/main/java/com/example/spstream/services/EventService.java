package com.example.spstream.services;

import com.example.spstream.entities.Event;
import com.example.spstream.repositories.EventRepository;
import com.example.spstream.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
}
