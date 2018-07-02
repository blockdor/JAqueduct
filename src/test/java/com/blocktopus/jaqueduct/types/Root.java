package com.blocktopus.jaqueduct.types;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.blocktopus.jaqueduct.JsonObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Root {

    private String id;
    private String name;
    private List<String> foo;
    private JsonObject obj;
    private List<JsonObject> bar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFoo() {
        return foo;
    }

    public void setFoo(List<String> foo) {
        this.foo = foo;
    }

    public JsonObject getObj() {
        return obj;
    }

    public void setObj(JsonObject obj) {
        this.obj = obj;
    }

    public List<JsonObject> getBar() {
        return bar;
    }

    public void setBar(List<JsonObject> bar) {
        this.bar = bar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
