package com.example.spstream.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Mapper {

    public static String  writeObjectToJson(Object object) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().findAndRegisterModules().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

    public static String readJsonFromFile(String filePath) throws IOException {
        Path path = new File(Mapper.class.getClassLoader().getResource(filePath).getFile()).toPath();
        return Files.readString(Path.of(path.toUri()));

    }

    public static <T> Object readObjectFromJson(String jsonString, Class<T> clazz ) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, clazz);
    }
}
