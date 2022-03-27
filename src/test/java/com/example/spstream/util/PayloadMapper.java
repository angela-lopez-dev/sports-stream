package com.example.spstream.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class PayloadMapper {



    public static <T> List<T> deserializePayload(String jsonPayload, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .findAndRegisterModules();
        String payload = mapper.readTree(jsonPayload).get("_embedded").get("events").toString();

        return Mapper.readObjectListFromJson(payload, clazz);
    }
}
