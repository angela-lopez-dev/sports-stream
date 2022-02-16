package com.example.spstream.controllers.events;

import com.example.spstream.WithDBContainerTest;
import com.example.spstream.entities.Event;
import com.example.spstream.repositories.EventRepository;
import com.example.spstream.util.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class EventsDeletionTest extends WithDBContainerTest {

    @SpyBean
    private EventRepository eventRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void clean(){
        eventRepository.deleteAll();
    }

    @Test
    public void deleteEventWhichExists() throws Exception {
        String savedEventID = storeDefaultEventAndReturnId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/events/"+savedEventID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));

        assertFalse(eventRepository.existsById(savedEventID));
    }

    @Test
    public void deleteEventWhichDoesntExists() throws Exception {
        String savedEventID = storeDefaultEventAndReturnId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/events/notSavedEventId")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
        assertTrue(eventRepository.existsById(savedEventID));
    }

    public String storeDefaultEventAndReturnId() throws IOException {
        Event eventToDelete = Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"), Event.class);
        eventToDelete.setDateTime(LocalDateTime.now().plusDays(1));
        return eventRepository.save(eventToDelete).getId();
    }


}
