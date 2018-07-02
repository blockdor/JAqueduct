package com.blocktopus.jaqueduct;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public interface JsonObject {

    boolean hasProperty(String propertyName);

    Class<? extends Object> getPropertyType(String propertyName);

    Object writeProperty(String propertyName, Object value);

    Object removeProperty(String propertyName);

    int getNumberOfProperties();

    Set<String> getPropertyNameList();

    boolean isList(String propertyName, Class<?> membersClass);

    boolean isJsonObject(String propertyName);

    boolean isClass(String propertyName, Class<?> clazz);

    Optional<Object> getOptionalProperty(String propertyName);

    Object getProperty(String propertyName);

    <T> Optional<T> getOptionalProperty(String propertyName, Class<T> clazz);

    <T> T getProperty(String propertyName, Class<T> clazz);

    Optional<JsonObject> getOptionalJsonObject(String propertyName);

    JsonObject getJsonObject(String propertyName);

    JsonObject getOrCreateJsonObject(String propertyName);

    List<JsonObject> getOrCreateListOfJsonObjects(String propertyName);

    Optional<String> getOptionalString(String propertyName);

    String getString(String propertyName);

    Optional<Integer> getOptionalInteger(String propertyName);

    Integer getInteger(String propertyName);

    Optional<Double> getOptionalDouble(String propertyName);

    Double getDouble(String propertyName);

    <T> Optional<List<T>> getOptionalList(String propertyName, Class<T> membersClazz);

    <T> List<T> getList(String propertyName, Class<T> membersClazz);

    Optional<List<JsonObject>> getOptionalListOfJsonObject(String propertyName);

    List<JsonObject> getListOfJsonObject(String propertyName);

    <T> Optional<T> getOptionalProperty(String propertyName, Function<Object, T> convertingFunction);

    <T> T getProperty(String propertyName, Function<Object, T> convertingFunction);

    JsonObject deepCopy();

    <T> T getGenericProperty(String name);

}
