package com.blocktopus.jaqueduct;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestJsonTree {

    JAqueductFactory factory;
    JsonTree testClass;
    JsonObjectMapImpl root;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        factory = JAqueductFactory.getDefaultFactory();
        root = new JsonObjectMapImpl(factory);
        testClass = root.getTree();
    }

    @Test
    public void findJsonPathToJsonObject() {
        List<JsonObject> finalChildren = createTree(5, root);
        String path = testClass.findJsonPathToJsonObject(finalChildren.get(23));
        assertEquals("$.left.left.left.many[2].many[1]",path);
    }

    @Test
    public void findPathToJsonObject() {
        List<JsonObject> finalChildren = createTree(5, root);
        List<String> path = testClass.findPathToJsonObject(finalChildren.get(23));
        assertEquals("left",path.get(0));
        assertEquals("left",path.get(1));
        assertEquals("left",path.get(2));
        assertEquals("many[2]",path.get(3));
        assertEquals("many[1]",path.get(4));

    }

    @Test
    public void findFromJsonPath() {
        List<JsonObject> finalChildren = createTree(5, root);
        finalChildren.get(1).writeProperty("marker", "ThisOne");
        JsonObject found = testClass.getObjectByJsonPath("$.left.left.left.left.right", JsonObject.class);
        assertEquals("ThisOne",found.getString("marker"));

    }

    @Test
    public void findFromJsonPathArrays() {
        List<JsonObject> finalChildren = createTree(5, root);
        finalChildren.get(finalChildren.size() - 168).writeProperty("marker", "ThisOne");
        JsonObject found = testClass.getObjectByJsonPath("$.many[2].many[1].many[1].right.many[0]", JsonObject.class);
        assertEquals("ThisOne",found.getString("marker"));

    }

    @Test
    public void findFromJsonPathArraysArray() {
        List<JsonObject> finalChildren = createTree(5, root);
        finalChildren.get(finalChildren.size() - 168).writeProperty("marker", "ThisOne");
        List<JsonObject> found = testClass.getListByJsonPath("$.many[2].many[1].many[1].right.many[*]", JsonObject.class);
        assertEquals(3,found.size());
        assertEquals("ThisOne",found.get(0).getString("marker"));


    }

    @Test
    public void findFromJsonPathStarred() {
        List<JsonObject> finalChildren = createTree(5, root);
        finalChildren.get(finalChildren.size() - 168).writeProperty("marker", "ThisOne");
        List<JsonObject> found = testClass.getListByJsonPath("$.many[2].many[1].many[*].right.many[0]", JsonObject.class);
        assertEquals(3,found.size());
        assertEquals("ThisOne",found.get(1).getString("marker"));

    }

    @Test
    public void findFromJsonPathMultiIndex() {
        List<JsonObject> finalChildren = createTree(5, root);
        finalChildren.get(finalChildren.size() - 168).writeProperty("marker", "ThisOne");
        List<JsonObject> found = testClass.getListByJsonPath("$.many[2].many[1].many[1,2].right.many[0,1]", JsonObject.class);
        assertEquals(4,found.size());
        assertEquals("ThisOne",found.get(0).getString("marker"));

    }

    @Test
    public void findFromJsonPathSlice() {
        List<JsonObject> finalChildren = createTree(5, root);
        finalChildren.get(finalChildren.size() - 168).writeProperty("marker", "ThisOne");
        List<JsonObject> found = testClass.getListByJsonPath("$.many[2].many[1].many[1].right.many[0:2]", JsonObject.class);
        assertEquals(3,found.size());
        assertEquals("ThisOne",found.get(0).getString("marker"));

    }

    private List<JsonObject> createTree(int depth, JsonObject root) {
        List<JsonObject> parents = Arrays.asList(root);
        List<JsonObject> children = new ArrayList<>();

        for (int i = 0; i < depth; i++) {
            for (JsonObject parent : parents) {
                children.addAll(createTree(parent));
            }
            parents = children;
            children = new ArrayList<>();
        }
        return parents;
    }

    private List<JsonObject> createTree(JsonObject jo) {
        JsonObject jo1 = factory.createJsonObject();
        JsonObject jo2 = factory.createJsonObject();
        JsonObject jo3 = factory.createJsonObject();
        JsonObject jo4 = factory.createJsonObject();
        JsonObject jo5 = factory.createJsonObject();
        jo.writeProperty("left", jo1);
        jo.writeProperty("right", jo2);
        jo.writeProperty("many", Arrays.asList(jo3, jo4, jo5));
        return Arrays.asList(jo1, jo2, jo3, jo4, jo5);
    }


}
