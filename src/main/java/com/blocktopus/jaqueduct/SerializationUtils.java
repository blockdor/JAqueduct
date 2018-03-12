package com.blocktopus.jaqueduct;

import java.util.List;
import java.util.function.Function;

import com.blocktopus.jaqueduct.exceptions.JAqueductException;
import com.fasterxml.jackson.core.JsonProcessingException;
import static com.blocktopus.jaqueduct.TypeConverter.*;

public class SerializationUtils {
	
	public SerializationUtils(JAqueductFactory factory) {
		super();
		this.factory = factory;
	}

	private JAqueductFactory factory;
	
	public Object jsonToObject(String json) {
		try {
			return factory.getObjectMapper().readValue(json, Object.class);
		} catch (Throwable e) {
			throw new JAqueductException(e);
		}
	}

	public String toJsonString(Object obj) {
		try {
			return factory.getObjectMapper().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new JAqueductException(e);
		}
	}

	public String toPrettyJsonString(Object obj) {
		try {
			return factory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new JAqueductException(e);
		}
	}

	public <T> T jsonToObject(String json, Class<T> clazz) {
		try {
			return factory.getObjectMapper().readValue(json, clazz);
		} catch (Throwable e) {
			throw new JAqueductException(e);
		}		
	}
	
	public JsonObject jsonToJsonObject(String json) {
		return jsonToObject(json,JsonObject.class);
	}

	public <T> List<T> jsonToList(String json, Class<T> membersClazz) {
		Object rootList = jsonToObject(json);
		return toListOf(toClass(membersClazz,factory.getObjectMapper())).apply(rootList);
	}
	public <T> T jsonToObjectByFunction(String json, Function<Object,T> convertingFunction) {
		Object rootList = jsonToObject(json);
		return convertingFunction.apply(rootList);
	}
}
