package com.example.spstream.controllers.events;

import com.example.spstream.entities.Event;
import com.example.spstream.entities.User;
import com.example.spstream.repositories.EventRepository;
import com.example.spstream.repositories.UserRepository;
import com.example.spstream.util.EventsUtils;
import com.example.spstream.util.Mapper;
import com.example.spstream.util.PayloadMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;
import java.util.Optional;

import static com.example.spstream.util.Mapper.readJsonFromFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
@Slf4j
public class EventsAccessTest {
    //TODO : init data

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void reset() {
        eventRepository.deleteAll();
    }



    @Test
    public void testFindAll() throws Exception {
        User user = User.builder()
                .firstName("a")
                .lastName("b")
                .age(23)
                .build();
        userRepository.save(user);
        List<Event> events = Mapper.readObjectListFromJson(readJsonFromFile("controllers/events/access/all_events.json"), Event.class);
        events.forEach(event -> {
            EventsUtils.setEventDateToTomorrow(event);
            event.setOrganiser(user);
        });

        eventRepository.saveAll(events);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/events")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
         List<Event> returnedEvents = PayloadMapper.deserializePayload( result.getResponse().getContentAsString(), Event.class);
        log.info("jkjdskd");
    }

    @Test
    public void testFindEventWhichExists() throws Exception {
        Mockito.when(eventRepository.findById("123456")).thenReturn(Optional.of(Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/access/final_event.json"), Event.class)));
        mockMvc.perform(MockMvcRequestBuilders.get("/events/123456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Mapper.readJsonFromFile("controllers/events/access/final_event.json")));
    }

    @Test
    public void testFindEventWhichDoesntExist() throws Exception {
        Mockito.when(eventRepository.findById("7891011")).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/events/7891011")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testFindEventDatabaseError() throws Exception {
        Mockito.when(eventRepository.findById("123456")).thenThrow(Mockito.mock(DataAccessException.class));
        mockMvc.perform(MockMvcRequestBuilders.get("/events/123456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

    }

}
