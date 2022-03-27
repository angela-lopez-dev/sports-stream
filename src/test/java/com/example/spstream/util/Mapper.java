package com.example.spstream.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.testcontainers.shaded.org.apache.commons.lang.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Mapper {

    public static String  writeObjectToJson(Object object) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().findAndRegisterModules().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

    public static String readJsonFromFile(String filePath) throws IOException, NullPointerException {
        Path path = new File(Mapper.class.getClassLoader().getResource(filePath).getFile()).toPath();
        return Files.readString(Path.of(path.toUri()));

    }

    public static <T> T readObjectFromJson(String jsonString, Class<T> clazz ) throws JsonProcessingException {
        return new ObjectMapper().findAndRegisterModules().readValue(jsonString, clazz);
    }

    public static <T> List<T> readObjectListFromJson(String jsonString, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        //allows custom object collection unmarshalling
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);

        return mapper.readValue(jsonString, listType);
    }

}
