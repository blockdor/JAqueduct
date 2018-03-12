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
		this.jsonFactory=jsonFactory;
	}

	JsonObjectMapImpl(Map<String, Object> m,JAqueductFactory jsonFactory) {
		super(m);
		this.jsonFactory=jsonFactory;
	}

	@Override
	public Object writeProperty(String name, Object value) {
		return this.put(name, value);
	}

	@Override
	public Object removeProperty(String name) {
		return this.remove(name);
	}

	public Optional<JsonObject> getOptionalJsonObject(String name) {
		if (!hasProperty(name)) {
			return Optional.empty();
		}
		return Optional.of(getJsonObject(name));

	}

	@Override
	public JsonObject getJsonObject(String name)  {
		if (!hasProperty(name)) {
			throw new JAqueductException(name+" is not a property on this object");
		}
		JsonObject jo = toClass(JsonObject.class,jsonFactory.getObjectMapper()).apply(getProperty(name));
		if(jo==null) {
			throw new JAqueductException("property is set but null");
		}
		writeProperty(name, jo);
		return jo;

	}


	@Override
	public <T> Optional<T> getOptionalProperty(String name, Class<T> clazz) {
		if (!hasProperty(name)) {
			return Optional.empty();
		} else {
			return Optional.ofNullable(getProperty(name, clazz));
		}
	}

	@Override
	public <T> T getProperty(String name, Class<T> clazz) {
		return toClass(clazz,jsonFactory.getObjectMapper()).apply(getProperty(name));
	}

	@Override
	public boolean hasProperty(String name) {
		return this.containsKey(name);
	}

	@Override
	public Class<?> getPropertyType(String name) {
		return getProperty(name).getClass();
	}

	@Override
	public Object getProperty(String name) {
		if (!hasProperty(name)) {
			throw new JAqueductException(name + " not found");
		}
		return this.get(name);
	}

	@Override
	public String getString(String name) {
		return getProperty(name, String.class);
	}

	@Override
	public Optional<String> getOptionalString(String name) {
		return getOptionalProperty(name,  String.class);
	}

	@Override
	public Integer getInteger(String name) {
		return getProperty(name, Integer.class);
	}

	@Override
	public Optional<Integer> getOptionalInteger(String name) {
		return getOptionalProperty(name,Integer.class);
	}

	@Override
	public int getNumberOfProperties() {
		return this.size();
	}

	@Override
	public Optional<Object> getOptionalProperty(String name) {
		if (!hasProperty(name)) {
			return Optional.empty();
		} else {
			return Optional.of(getProperty(name));
		}
	}

	@Override
	public <T> Optional<List<T>> getOptionalList(String name, Class<T> clazz) {
		if (!hasProperty(name)) {
			return Optional.empty();
		}
		return Optional.of(getList(name, clazz));
	}

	@Override
	public <T> List<T> getList(String name, Class<T> clazz) {
		try {
			Object property = getProperty(name);
			List<T> list = toListOf(toClass(clazz,jsonFactory.getObjectMapper())).apply(property);
			writeProperty(name, list);
			return list;
		} catch (Throwable re) {
			throw new JAqueductException("Error while getting " + name, re);
		}
	}
	
	@Override
	public <T> T getProperty(String name, Function<Object, T> valueConvertingFunction) {
		return valueConvertingFunction.apply(getProperty(name));
	}

	@Override
	public <T> Optional<T> getOptionalProperty(String name, Function<Object, T> valueConvertingFunction) {
		if (!hasProperty(name)) {
			return Optional.empty();
		} else {
			return Optional.of(getProperty(name, valueConvertingFunction));
		}
	}

	public JsonObject deepCopy() {
		SerializationUtils utils = jsonFactory.getSerializationUtils();
		String string = utils.toJsonString(this);
		return  utils.jsonToObject(string,JsonObject.class);
	}

	@Override
	public Set<String> getPropertyNameList() {
		return this.keySet();
	}

	@Override
	public boolean isList(String PropertyName,Class<?> membersClass) {
		try {
			getList(PropertyName,membersClass);
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
	public boolean isClass(String propertyName,Class<?> clazz) {
		try {
			Object o = getProperty(propertyName,clazz);
			if(o==null) {
				throw new JAqueductException("property is set but null");
			}
			return true;
		} catch (Throwable jae) {
			return false;
		}
	}

	@Override
	public List<JsonObject> flattenTree(){
		List<JsonObject> flatList = new ArrayList<>();
		forEachJsonObjectInTree(flatList::add);
		return flatList;
	}

	@Override
	public List<JsonObject> findJsonObjectInTreeByPropertyName(String nameToFind){
		List<JsonObject> found = new ArrayList<>();
		forEachJsonObjectInTree(
				jo-> {
					if (jo.isJsonObject(nameToFind)) {
						found.add(jo.getJsonObject(nameToFind));
					}
				}
		);
		return found;
	}


	@SuppressWarnings("unchecked")
	public void forEachJsonObjectInTree(Consumer<JsonObject> consumer) {
		Set<String> names = getPropertyNameList();
		for(String name :names) {
			if(isList(name, JsonObject.class)) {
				List<JsonObject> joList = getListOfJsonObject(name);
				joList.forEach(jo1->jo1.forEachJsonObjectInTree(consumer));
			}
			if(isJsonObject(name)) {
				consumer.accept(getJsonObject(name));
				getJsonObject(name).forEachJsonObjectInTree(consumer);
			}
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
		if(optional.isPresent()) {
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
		if(optional.isPresent()) {
			joList = optional.get();
		} else {
			joList = new ArrayList<>();
			writeProperty(propertyName, joList);
		}
		return joList;
	}



}
