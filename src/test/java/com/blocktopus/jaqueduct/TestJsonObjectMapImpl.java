package com.blocktopus.jaqueduct;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.blocktopus.jaqueduct.exceptions.JAqueductException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestJsonObjectMapImpl {

    JAqueductFactory factory;
    JsonObjectMapImpl testClass;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        factory = JAqueductFactory.getDefaultFactory();
        testClass = new JsonObjectMapImpl(factory);
    }

    private void createData() {
        testClass.writeProperty("id", 1);
        testClass.writeProperty("stringid", "1");
        testClass.writeProperty("name", "bob");
        testClass.writeProperty("foo", Arrays.asList("a", "b", "c"));
        JsonObject m = factory.createJsonObject();
        m.writeProperty("thing", "something");
        m.writeProperty("otherthing", "somethingelse");
        testClass.writeProperty("obj", m);

        JsonObject o1 = factory.createJsonObject();
        JsonObject o2 = factory.createJsonObject();
        o1.writeProperty("stuff", "hey");
        o2.writeProperty("stuff", "woo");
        testClass.writeProperty("bar", Arrays.asList(o1, o2));


        testClass.writeProperty("listoflist", Arrays.asList(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e", "f")));


    }

    @Test
    public void writeProperty() {
        testClass.writeProperty("foo", "bar");
        assertTrue(testClass.containsKey("foo"));
    }

    @Test
    public void hasProperty() {
        testClass.put("foo", "bar");
        assertTrue(testClass.hasProperty("foo"));
    }

    @Test
    public void getProperty1() throws Exception {
        createData();
        String obj = testClass.getProperty("id", String.class);
        assertEquals("1", obj);
    }

    @Test
    public void getProperty() throws Exception {
        createData();
        int obj = testClass.getProperty("id", Integer.class);
        assertEquals(1, obj);
    }


    @Test
    public void getListDefaultMapper() throws Exception {
        createData();
        List<String> foo = testClass.getList("foo", String.class);
        assertEquals(3, foo.size());
        assertEquals("a", foo.get(0));
        assertEquals("b", foo.get(1));
        assertEquals("c", foo.get(2));
    }


    @Test
    public void getJsonObjectProperty() throws Exception {
        createData();
        JsonObject obj = testClass.getJsonObject("obj");
        assertEquals(2, obj.getNumberOfProperties());
    }

    @Test
    public void incorrectgetJsonObjectPropertyFromList() throws Exception {
        createData();
        thrown.expect(JAqueductException.class);
        JsonObject foo = testClass.getJsonObject("foo");

    }

    @Test
    public void incorrectgetJsonObjectPropertyFromString() throws Exception {
        createData();
        thrown.expect(JAqueductException.class);
        JsonObject foo = testClass.getJsonObject("name");

    }

    @Test
    public void incorrectgetJsonObjectPropertyFromInt() throws Exception {
        createData();
        thrown.expect(JAqueductException.class);
        JsonObject foo = testClass.getJsonObject("id");

    }

    @Test
    public void getString() throws Exception {
        createData();
        String name = testClass.getString("name");
        assertEquals("bob", name);
    }

    @Test
    public void getInteger() throws Exception {
        createData();
        Integer id = testClass.getInteger("id");
        assertEquals(1, id.intValue());
    }

    @Test
    public void getIntegerFromString() throws Exception {
        createData();
        Integer id = testClass.getInteger("stringid");
        assertEquals(1, id.intValue());
    }

    @Test
    public void getListOfJsonObjectDefaultMapper() throws Exception {
        createData();
        List<JsonObject> bar = testClass.getList("bar", JsonObject.class);
        assertEquals(2, bar.size());
    }

    @Test
    public void getListOfList() throws Exception {
        createData();

        Function<Object, List<List<String>>> fn
                = TypeConverter.toListOf(TypeConverter.toListOf(TypeConverter.toClass(String.class, factory.getObjectMapper())));

        List<List<String>> listy = testClass.getProperty("listoflist", fn);
        assertEquals(2, listy.size());
        assertEquals(3, listy.get(0).size());
        assertEquals(3, listy.get(1).size());
        assertEquals("a", listy.get(0).get(0));
        assertEquals("b", listy.get(0).get(1));
        assertEquals("c", listy.get(0).get(2));
        assertEquals("d", listy.get(1).get(0));
        assertEquals("e", listy.get(1).get(1));
        assertEquals("f", listy.get(1).get(2));
    }
}
