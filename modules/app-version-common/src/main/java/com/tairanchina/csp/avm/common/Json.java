package com.tairanchina.csp.avm.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author me 2022-11-28 22:57
 */
public class Json {


    public static ObjectMapper M = new ObjectMapper();

    public static String toJsonString(Object instance) {
        try {
            return M.writeValueAsString(instance);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return M.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static ObjectMapper getMapper() {
        return M;
    }
}
