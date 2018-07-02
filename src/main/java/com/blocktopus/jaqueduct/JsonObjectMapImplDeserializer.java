package com.blocktopus.jaqueduct;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.blocktopus.jaqueduct.exceptions.JAqueductException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonObjectMapImplDeserializer extends JsonDeserializer<JsonObjectMapImpl> {

    private JAqueductFactory factory;

    public JsonObjectMapImplDeserializer(JAqueductFactory factory) {
        super();
        this.factory = factory;
    }

    @Override
    public JsonObjectMapImpl deserialize(JsonParser jsonParser, DeserializationContext dsc) throws IOException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode jn = oc.readTree(jsonParser);
        if (jn.isArray()) {
            throw new JAqueductException("Cannot convert a json array to JsonObject");
        }
        if (jn.isValueNode()) {
            throw new JAqueductException("Cannot convert from a value to JsonObject");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> m = factory.getObjectMapper().convertValue(jn, LinkedHashMap.class);
        return new JsonObjectMapImpl(m, factory);
    }

}
