package com.example.spstream.controllers.events;

import com.example.spstream.entities.Event;
import com.example.spstream.entities.User;
import com.example.spstream.repositories.EventRepository;
import com.example.spstream.repositories.UserRepository;
import com.example.spstream.services.UserService;
import com.example.spstream.util.Mapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = EventsCreationTest.EventsCreationTestConfig.class)
public class EventsCreationTest {
    private static final String MISSING_TITLE_ERROR_MESSAGE = "title is missing";
    private static final String MISSING_ACTIVITY_ERROR_MESSAGE = "activity is missing";
    private static final String MISSING_LOCALISATION_ERROR_MESSAGE = "localisation is missing";
    private static final String INVALID_ORGANISER_ID_ERROR_MESSAGE = "user %s does not exist";
    private static final String MISSING_ORGANISER_ID_ERROR_MESSAGE = "organiser id is missing";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @SpyBean
    private EventRepository eventRepository;

    private static final String DATE_IN_PAST_ERROR_MESSAGE = "date is in the past";

    @BeforeEach
    public void init() {
        Mockito.reset(userRepository);
    }

    //prevents hardcoded events from failing tests due to date in the past
    public void setEventDateToTomorrow(Event event) {
        event.setDateTime(LocalDateTime.now().plusDays(1));
    }

    public void setEventDateToYesterday(Event event) {
        event.setDateTime(LocalDateTime.now().minusDays(1));
    }

    public void testCorrectEventCreationWithEvent(Event event) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .content(Mapper.writeObjectToJson(event))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString());
    }

    public void testIncorrectEventCreationWithEvent(Event event, String errorMessagePattern) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .content(Mapper.writeObjectToJson(event))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(String.format(errorMessagePattern, event.getOrganiserId()))));
    }

    /**
     * correct data
     **/
    @Test
    public void testMinimalCorrectEvent() throws Exception {
        Event minimalEvent = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"), Event.class);
        setEventDateToTomorrow(minimalEvent);
        testCorrectEventCreationWithEvent(minimalEvent);
    }

    @Test
    public void testMaximalCorrectEvent() throws Exception {
        Event maximalEvent = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/maximal_event.json"), Event.class);
        setEventDateToTomorrow(maximalEvent);
        testCorrectEventCreationWithEvent(maximalEvent);
    }

    /**
     * missing data
     **/
    @Test
    public void testIncorrectEventTitleMissing() throws Exception {
        Event eventTitleMissing = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"), Event.class);
        setEventDateToTomorrow(eventTitleMissing);
        eventTitleMissing.setTitle(null);
        testIncorrectEventCreationWithEvent(eventTitleMissing, MISSING_TITLE_ERROR_MESSAGE);
    }

    @Test
    public void testIncorrectEventActivityMissing() throws Exception {
        Event eventActivityMissing = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"), Event.class);
        eventActivityMissing.setActivity(null);
        setEventDateToTomorrow(eventActivityMissing);
        testIncorrectEventCreationWithEvent(eventActivityMissing, MISSING_ACTIVITY_ERROR_MESSAGE);
    }

    @Test
    public void testIncorrectEventLocalisationMissing() throws Exception {
        Event eventLocalisationMissing = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"), Event.class);
        eventLocalisationMissing.setLocalisation(null);
        setEventDateToTomorrow(eventLocalisationMissing);
        testIncorrectEventCreationWithEvent(eventLocalisationMissing, MISSING_LOCALISATION_ERROR_MESSAGE);
    }

    @Test
    public void testIncorrectEventMissingUserId() throws Exception {
        Event eventOrganiserIdMissing = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/incorrect/missing_user_id.json"), Event.class);
        setEventDateToTomorrow(eventOrganiserIdMissing);
        testIncorrectEventCreationWithEvent(eventOrganiserIdMissing, MISSING_ORGANISER_ID_ERROR_MESSAGE);
    }

    /**
     * invalid data
     **/
    @Test
    public void testIncorrectEventInvalidOrganiserId() throws Exception {
        Mockito.when(userRepository.existsById(Mockito.any())).thenReturn(false);
        Event eventInvalidOrganiserId = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"), Event.class);
        setEventDateToTomorrow(eventInvalidOrganiserId);
        testIncorrectEventCreationWithEvent(eventInvalidOrganiserId, INVALID_ORGANISER_ID_ERROR_MESSAGE);
    }

    @Test
    public void testIncorrectEventDateInThePast() throws Exception {
        Event eventInPast = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"), Event.class);
        setEventDateToYesterday(eventInPast);
        testIncorrectEventCreationWithEvent(eventInPast, DATE_IN_PAST_ERROR_MESSAGE);
    }

    /**
     * internal database issue
     **/
    @Test
    public void testCorrectEventServerError() throws Exception {

        Event eventInvalidOrganiserId = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"), Event.class);
        setEventDateToTomorrow(eventInvalidOrganiserId);
        Mockito.when(eventRepository.save(eventInvalidOrganiserId)).thenThrow(Mockito.mock(DataAccessException.class));
        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .content(Mapper.writeObjectToJson(eventInvalidOrganiserId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        System.out.println("whatever");
    }

    @TestConfiguration
    public static class EventsCreationTestConfig {
        //necessary because a user repository @mockbean instantiated in @beforeEach method would have been overriden by spring context initialisation
        @Bean
        @Primary
        public static UserRepository mockUserRepository() {
            UserRepository userRepository = Mockito.mock(UserRepository.class);
            Mockito.when(userRepository.existsById("123456")).thenReturn(true);
            return userRepository;
        }
    }


}
