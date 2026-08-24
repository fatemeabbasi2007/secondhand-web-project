package org.example.secondhandweb.util;


//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonFileStorage {
    private final ObjectMapper objectMapper;

    private static final String STORAGE_DIRECTORY = "data/";
    public JsonFileStorage() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        File directory = new File(STORAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

    }
    public <T> void saveToFile(String fileName , List<T> data ){
        try{
            objectMapper.writeValue(new File(STORAGE_DIRECTORY  + fileName) , data);


        }catch (IOException e){
            throw new RuntimeException("Failed to write data to file : " + fileName);
        }
    }
    public <T> List<T> readFromFile(String fileName, Class<T> valueType) {
        File file = new File(STORAGE_DIRECTORY + fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
        } catch (IOException e) {
            // 👈 حتماً e را به عنوان cause پاس دهید تا خطای دقیق جکسون را در ترمینال ببینید
            throw new RuntimeException("Failed to read data from file: " + fileName + " | Reason: " + e.getMessage(), e);
        }
    }
}
