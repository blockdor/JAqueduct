package com.blocktopus.jaqueduct;

import java.util.Map;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;


public class JAqueductFactory {

    private ObjectMapper objectMapper;

    private SerializationUtils serializationUtils = new SerializationUtils(this);

    public SerializationUtils getSerializationUtils() {
        return serializationUtils;
    }

    private JAqueductFactory() {
        super();
    }

    public static JAqueductFactory getDefaultFactory() {
        ObjectMapper objectMapper = new ObjectMapper();
        JAqueductFactory factory = new JAqueductFactory();
        configureObjectMapper(objectMapper, factory);
        factory.setObjectMapper(objectMapper);
        return factory;
    }

    public static JAqueductFactory getFactory(ObjectMapper objectMapper) {
        JAqueductFactory factory = new JAqueductFactory();
        configureObjectMapper(objectMapper, factory);
        factory.setObjectMapper(objectMapper);
        return factory;
    }

    public JsonObject createJsonObject() {
        return new JsonObjectMapImpl(this);
    }

    public JsonObject createJsonObject(Map<String, Object> m) {
        return new JsonObjectMapImpl(m, this);
    }

    public JsonObject createJsonObject(Object o) {
        return TypeConverter.toClass(JsonObject.class, getObjectMapper()).apply(o);
    }

    private static void configureObjectMapper(ObjectMapper objectMapper, JAqueductFactory factory) {
        SimpleModule stockModule = new SimpleModule("stockMapping", new Version(1, 0, 0, null, null, null))
                .addAbstractTypeMapping(Map.class, JsonObjectMapImpl.class)
                .addAbstractTypeMapping(JsonObject.class, JsonObjectMapImpl.class)
                .addDeserializer(JsonObjectMapImpl.class, new JsonObjectMapImplDeserializer(factory));

        objectMapper.registerModule(stockModule);

        // allow the mapper to parse JSON with comments in it
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public <T> T convertObject(Object o, Class<T> clazz) {
        return TypeConverter.toClass(clazz, getObjectMapper()).apply(o);
    }
}
