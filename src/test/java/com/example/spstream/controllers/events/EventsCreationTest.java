package com.example.spstream.controllers.events;

import com.example.spstream.beans.rest.Event;
import com.example.spstream.util.Mapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class EventsCreationTest {
    private static final String MISSING_TITLE_ERROR_MESSAGE = "title is missing";
    private static final String MISSING_ACTIVITY_ERROR_MESSAGE = "activity is missing";
    private static final String MISSING_LOCALISATION_ERROR_MESSAGE = "localisation is missing";
    private static final String INVALID_ORGANISER_ID_ERROR_MESSAGE = "organiser id is invalid";
    private static final String MISSING_ORGANISER_ID_ERROR_MESSAGE = "organiser id is missing";
    private static final String ERROR_OCCURED_ERROR_MESSAGE = "error occured";
    @Autowired
    private  MockMvc mockMvc;
    private final WireMockServer wireMockServer = new WireMockServer();

    private static final String DATE_IN_PAST_ERROR_MESSAGE = "date is in the past";

    @BeforeEach
    public void init() throws Exception{
        wireMockServer.resetAll();
        wireMockServer.stubFor(get("/users/123456").willReturn(aResponse().withStatus(200).withBody(Mapper.readJsonFromFile("controllers/users/default/default_user.json"))));
    }

    //prevents hardcoded events from failing tests due to date in the past
    public void setEventDateToTomorrow(Event event){
        event.setDateTime(LocalDateTime.now().plusDays(1));
    }

    public void setEventDateToYesterday(Event event){
        event.setDateTime(LocalDateTime.now().minusDays(1));
    }

    public void testCorrectEventCreationWithEvent(Event event) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/events/create")
                        .content(Mapper.writeObjectToJson(event))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.event_id").exists())
                .andExpect(jsonPath("$.event_id").isNumber());
    }

    public void testIncorrectEventCreationWithEvent(Event event, String errorMessage) throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/events/create")
                        .content(Mapper.writeObjectToJson(event))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString(errorMessage)));
    }

    /** correct data **/
    @Test
    public void testMinimalCorrectEvent() throws Exception {
        Event minimalEvent = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"),Event.class);
        setEventDateToTomorrow(minimalEvent);
        testCorrectEventCreationWithEvent(minimalEvent);
    }

    @Test
    public void testMaximalCorrectEvent() throws Exception {
        Event maximalEvent = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/maximal_event.json"),Event.class);
        setEventDateToTomorrow(maximalEvent);
        testCorrectEventCreationWithEvent(maximalEvent);
    }

    /** missing data **/
    @Test
    public void testIncorrectEventTitleMissing() throws Exception{
        Event eventTitleMissing = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"),Event.class);
        eventTitleMissing.setTitle(null);
        testIncorrectEventCreationWithEvent(eventTitleMissing, MISSING_TITLE_ERROR_MESSAGE);
    }

    @Test
    public void testIncorrectEventActivityMissing() throws Exception{
        Event eventActivityMissing = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"),Event.class);
        eventActivityMissing.setActivity(null);
        testIncorrectEventCreationWithEvent(eventActivityMissing, MISSING_ACTIVITY_ERROR_MESSAGE);
    }

    @Test
    public void testIncorrectEventLocalisationMissing() throws Exception{
        Event eventLocalisationMissing = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"),Event.class);
        eventLocalisationMissing.setLocalisation(null);
        testIncorrectEventCreationWithEvent(eventLocalisationMissing, MISSING_LOCALISATION_ERROR_MESSAGE);
    }

    @Test
    public void testIncorrectEventMissingUserId() throws Exception {
        Event eventOrganiserIdMissing = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/incorrect/missing_user_id.json"),Event.class);
        testIncorrectEventCreationWithEvent(eventOrganiserIdMissing, MISSING_ORGANISER_ID_ERROR_MESSAGE);
    }
/** invalid data**/
    @Test
    public void testIncorrectEventInvalidOrganiserId() throws Exception {
        wireMockServer.stubFor(get("/users/123456").willReturn(aResponse().withStatus(404)));
        Event eventInvalidOrganiserId = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"),Event.class);
        testIncorrectEventCreationWithEvent(eventInvalidOrganiserId, INVALID_ORGANISER_ID_ERROR_MESSAGE);
    }

    @Test
    public void testIncorrectEventDateInThePast() throws Exception {
        Event eventInPast = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"),Event.class);
        setEventDateToYesterday(eventInPast);
        testIncorrectEventCreationWithEvent(eventInPast, DATE_IN_PAST_ERROR_MESSAGE);
    }
    /** external api issue **/
    @Test
    public void testCorrectEventServerError() throws Exception {
        wireMockServer.stubFor(get("/users/123456").willReturn(aResponse().withStatus(500)));
        Event eventInvalidOrganiserId = (Event) Mapper.readObjectFromJson(Mapper.readJsonFromFile("controllers/events/create/correct/minimal_event.json"),Event.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/events/create")
                        .content(Mapper.writeObjectToJson(eventInvalidOrganiserId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(status().reason(containsString(ERROR_OCCURED_ERROR_MESSAGE)));
    }




}
