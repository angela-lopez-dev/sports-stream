package com.example.spstream.controllers.events;

import com.example.spstream.WithDBContainerTest;
import com.example.spstream.entities.Event;
import com.example.spstream.repositories.EventRepository;
import com.example.spstream.util.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;
import java.util.Optional;

import static com.example.spstream.util.Mapper.readJsonFromFile;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class EventsAccessTest extends WithDBContainerTest {

    @MockBean
    EventRepository mockEventRepository;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void reset(){
        Mockito.reset(mockEventRepository);
    }

    @Test
    public void testFindAll() throws Exception{
        List<Event> events = Mapper.readObjectListFromJson(readJsonFromFile("controllers/events/access/all_events.json"), Event.class);
        Mockito.when(mockEventRepository.findAll()).thenReturn(events);
        mockMvc.perform(MockMvcRequestBuilders.get("/events")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Mapper.readJsonFromFile("controllers/events/access/all_events.json")));
    }

    @Test
    public void testFindEventWhichExists() throws Exception{
        Mockito.when(mockEventRepository.findById("123456")).thenReturn(Optional.of(Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/access/final_event.json"),Event.class)));
        mockMvc.perform(MockMvcRequestBuilders.get("/events/123456")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(Mapper.readJsonFromFile("controllers/events/access/final_event.json")));
    }

     @Test
    public void testFindEventWhichDoesntExist() throws Exception {
         Mockito.when(mockEventRepository.findById("7891011")).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/events/7891011")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testFindEventDatabaseError() throws Exception {
        Mockito.when(mockEventRepository.findById("123456")).thenThrow(Mockito.mock(DataAccessException.class));
        mockMvc.perform(MockMvcRequestBuilders.get("/events/123456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

    }

}
