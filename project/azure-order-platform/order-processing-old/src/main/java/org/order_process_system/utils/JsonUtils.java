package org.order_process_system.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {


    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String parseObToJson(Object ob){
        try {
           return objectMapper.writeValueAsString(ob);
        } catch (JsonProcessingException e) {
            return "{error: \"Error While parseJson\"}";
        }
    }
}
