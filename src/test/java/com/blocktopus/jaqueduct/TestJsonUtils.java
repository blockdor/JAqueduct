package com.blocktopus.jaqueduct;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.blocktopus.jaqueduct.exceptions.JAqueductException;
import com.blocktopus.jaqueduct.types.Root;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.blocktopus.jaqueduct.TypeConverter.*;

public class TestJsonUtils {

    JAqueductFactory factory;
    SerializationUtils testClass;

    @Before
    public void setup() {
        factory = JAqueductFactory.getDefaultFactory();
        testClass = new SerializationUtils(factory);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void deserialiseToJsonObject() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("test.json");
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);
        JsonObject root = testClass.jsonToJsonObject(json);
        assertEquals(6, root.getNumberOfProperties());

    }

    @Test
    public void incorrectDeserialiseToJsonObject() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("test2.json");
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);
        thrown.expect(JAqueductException.class);
        JsonObject root = testClass.jsonToJsonObject(json);
        //assertEquals(4,root.size());

    }

    @Test
    public void serialise() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("test.json");
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);
        InputStream in2 = this.getClass().getClassLoader()
                .getResourceAsStream("output.json");
        String outputJson = IOUtils.toString(in2, StandardCharsets.UTF_8);
        JsonObject root = testClass.jsonToJsonObject(json);
        root.removeProperty("stringid");
        String generated = testClass.toJsonString(root);

        assertEquals(outputJson, generated);
    }

    //modify and serialise
    //serialise
    @Test
    public void modify() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("test.json");
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);
        InputStream in2 = this.getClass().getClassLoader()
                .getResourceAsStream("modified.json");
        String prettyJson = IOUtils.toString(in2, StandardCharsets.UTF_8);
        JsonObject root = testClass.jsonToJsonObject(json);
        root.writeProperty("addedString", "aString");
        root.writeProperty("addedInteger", 12);
        JsonObject newObject = factory.createJsonObject();
        newObject.writeProperty("newName", "bill");
        root.writeProperty("addedObject", newObject);

        root.removeProperty("stringid");
        String prettyGenerated = testClass.toPrettyJsonString(root);
        assertEquals(prettyJson, prettyGenerated);
    }

    @Test
    public void deserialiseToJsonList() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("test2.json");
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);
        List<String> root = testClass.jsonToList(json, String.class);
        assertEquals(4, root.size());

    }

    @Test
    public void deserialiseToJsonListOfList() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("test3.json");
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);

        ObjectMapper om = factory.getObjectMapper();
        Function<Object, List<List<String>>> listListFunction
                = toListOf(toListOf(toClass(String.class, om)));

        List<List<String>> root =
                testClass.jsonToObjectByFunction(json, listListFunction);
        assertEquals(2, root.size());
        assertEquals(4, root.get(0).size());
        assertEquals(4, root.get(1).size());
        assertEquals("a", root.get(0).get(0));
        assertEquals("b", root.get(0).get(1));
        assertEquals("c", root.get(0).get(2));
        assertEquals("d", root.get(0).get(3));
        assertEquals("e", root.get(1).get(0));
        assertEquals("f", root.get(1).get(1));
        assertEquals("g", root.get(1).get(2));
        assertEquals("h", root.get(1).get(3));
    }

    @Test
    public void convertTest() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("test.json");
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);
        Object root = testClass.jsonToObject(json, Object.class);
        Map m = testClass.jsonToObject(json, Map.class);
        JsonObject ooo = testClass.jsonToJsonObject(json);

    }

    @Test
    public void pojoRoot() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("test.json");
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);
        Root root = testClass.jsonToObject(json, Root.class);
        List<String> list = root.getBar().stream().map(jo -> jo.getString("stuff")).collect(Collectors.toList());
        assertEquals(2, list.size());

    }

    //serialise
    @Test
    public void serialisePretty() throws Exception {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream("test.json");
        String json = IOUtils.toString(in, StandardCharsets.UTF_8);
        InputStream in2 = this.getClass().getClassLoader()
                .getResourceAsStream("prettyOutput.json");
        String prettyJson = IOUtils.toString(in2, StandardCharsets.UTF_8);
        JsonObject root = testClass.jsonToJsonObject(json);
        root.removeProperty("stringid");
        String prettyGenerated = testClass.toPrettyJsonString(root);
        assertEquals(prettyJson, prettyGenerated);
    }

}
