package com.blocktopus.jaqueduct;

import com.blocktopus.jaqueduct.exceptions.JAqueductException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.blocktopus.jaqueduct.TypeConverter.toClass;
import static com.blocktopus.jaqueduct.TypeConverter.toListOf;

public class JsonObjectMapImpl extends LinkedHashMap<String, Object> implements JsonObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JAqueductFactory jsonFactory;

    JsonObjectMapImpl(JAqueductFactory jsonFactory) {
        super();
        this.jsonFactory = jsonFactory;
    }

    JsonObjectMapImpl(Map<String, Object> m, JAqueductFactory jsonFactory) {
        super(m);
        this.jsonFactory = jsonFactory;
    }

    @Override
    public Object writeProperty(String name, Object value) {
        return this.put(name, value);
    }

    @Override
    public Object removeProperty(String propertyName) {
        return this.remove(propertyName);
    }

    public Optional<JsonObject> getOptionalJsonObject(String propertyName) {
        if (!hasProperty(propertyName)) {
            return Optional.empty();
        }
        return Optional.of(getJsonObject(propertyName));
    }

    @Override
    public JsonObject getJsonObject(String propertyName) {
        if (!hasProperty(propertyName)) {
            throw new JAqueductException(propertyName + " is not a property on this object", this);
        }
        JsonObject jo = toClass(JsonObject.class, jsonFactory.getObjectMapper()).apply(getProperty(propertyName));
        if (jo == null) {
            throw new JAqueductException(propertyName + " is set but null", this);
        }
        writeProperty(propertyName, jo);
        return jo;

    }


    @Override
    public <T> Optional<T> getOptionalProperty(String propertyName, Class<T> clazz) {
        if (!hasProperty(propertyName)) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(getProperty(propertyName, clazz));
        }
    }

    @Override
    public <T> T getProperty(String propertyName, Class<T> clazz) {
        return toClass(clazz, jsonFactory.getObjectMapper()).apply(getProperty(propertyName));
    }

    @Override
    public boolean hasProperty(String propertyName) {
        return this.containsKey(propertyName);
    }

    @Override
    public Class<?> getPropertyType(String propertyName) {
        return getProperty(propertyName).getClass();
    }

    @Override
    public Object getProperty(String propertyName) {
        if (!hasProperty(propertyName)) {
            throw new JAqueductException(propertyName + " is not a property on this object", this);
        }
        return this.get(propertyName);
    }

    @Override
    public String getString(String propertyName) {
        return getProperty(propertyName, String.class);
    }

    @Override
    public Optional<String> getOptionalString(String propertyName) {
        return getOptionalProperty(propertyName, String.class);
    }

    @Override
    public Integer getInteger(String propertyName) {
        return getProperty(propertyName, Integer.class);
    }

    @Override
    public Optional<Double> getOptionalDouble(String propertyName) {
        return getOptionalProperty(propertyName, Double.class);
    }

    @Override
    public Double getDouble(String propertyName) {
        return getProperty(propertyName, Double.class);
    }

    @Override
    public Optional<Integer> getOptionalInteger(String propertyName) {
        return getOptionalProperty(propertyName, Integer.class);
    }

    @Override
    public int getNumberOfProperties() {
        return this.size();
    }

    @Override
    public Optional<Object> getOptionalProperty(String propertyName) {
        if (!hasProperty(propertyName)) {
            return Optional.empty();
        } else {
            return Optional.of(getProperty(propertyName));
        }
    }

    @Override
    public <T> Optional<List<T>> getOptionalList(String propertyName, Class<T> clazz) {
        if (!hasProperty(propertyName)) {
            return Optional.empty();
        }
        return Optional.of(getList(propertyName, clazz));
    }

    @Override
    public <T> List<T> getList(String propertyName, Class<T> clazz) {
        try {
            Object property = getProperty(propertyName);
            List<T> list = toListOf(toClass(clazz, jsonFactory.getObjectMapper())).apply(property);
            writeProperty(propertyName, list);
            return list;
        } catch (Throwable re) {
            throw new JAqueductException("Error while getting " + propertyName, re, this);
        }
    }

    public <T> T getGenericProperty(String name) {
        return (T) get(name);
    }


    @Override
    public <T> T getProperty(String name, Function<Object, T> valueConvertingFunction) {
        return valueConvertingFunction.apply(getProperty(name));
    }

    @Override
    public <T> Optional<T> getOptionalProperty(String propertyName, Function<Object, T> valueConvertingFunction) {
        if (!hasProperty(propertyName)) {
            return Optional.empty();
        } else {
            return Optional.of(getProperty(propertyName, valueConvertingFunction));
        }
    }

    public JsonObject deepCopy() {
        SerializationUtils utils = jsonFactory.getSerializationUtils();
        String string = utils.toJsonString(this);
        return utils.jsonToObject(string, JsonObject.class);
    }

    @Override
    public Set<String> getPropertyNameList() {
        return this.keySet();
    }

    @Override
    public boolean isList(String PropertyName, Class<?> membersClass) {
        try {
            getList(PropertyName, membersClass);
            return true;
        } catch (Throwable jae) {
            return false;
        }
    }

    @Override
    public boolean isJsonObject(String propertyName) {
        try {
            getJsonObject(propertyName);
            return true;
        } catch (Throwable jae) {
            return false;
        }
    }

    @Override
    public boolean isClass(String propertyName, Class<?> clazz) {
        try {
            Object o = getProperty(propertyName, clazz);
            if (o == null) {
                throw new JAqueductException("property is set but null");
            }
            return true;
        } catch (Throwable jae) {
            return false;
        }
    }

    @Override
    public Optional<List<JsonObject>> getOptionalListOfJsonObject(String propertyName) {
        return getOptionalList(propertyName, JsonObject.class);
    }

    @Override
    public List<JsonObject> getListOfJsonObject(String propertyName) {
        return getList(propertyName, JsonObject.class);
    }

    @Override
    public JsonObject getOrCreateJsonObject(String propertyName) {
        Optional<JsonObject> optional = getOptionalJsonObject(propertyName);
        JsonObject jo;
        if (optional.isPresent()) {
            jo = optional.get();
        } else {
            jo = jsonFactory.createJsonObject();
            writeProperty(propertyName, jo);
        }
        return jo;
    }

    @Override
    public List<JsonObject> getOrCreateListOfJsonObjects(String propertyName) {
        Optional<List<JsonObject>> optional = getOptionalListOfJsonObject(propertyName);
        List<JsonObject> joList;
        if (optional.isPresent()) {
            joList = optional.get();
        } else {
            joList = new ArrayList<>();
            writeProperty(propertyName, joList);
        }
        return joList;
    }

    public JsonTree getTree() {
        return new JsonTree(this);
    }
}
