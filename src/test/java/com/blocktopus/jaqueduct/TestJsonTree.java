package com.blocktopus.jaqueduct;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void findJsonPathToJsonObject(){
        List<JsonObject> finalChildren = createTree(5,root);
        String path = testClass.findJsonPathToJsonObject(finalChildren.get(23));

        System.out.println(path);

    }
    @Test
    public void findPathToJsonObject(){
        List<JsonObject> finalChildren = createTree(5,root);
        List<String> path = testClass.findPathToJsonObject(finalChildren.get(23));

        System.out.println(path);

    }
    @Test
    public void findFromJsonPath(){
        List<JsonObject> finalChildren = createTree(5,root);
        finalChildren.get(1).writeProperty("marker", "ThisOne");
        List<String> path = testClass.findPathToJsonObject(finalChildren.get(1));
        System.out.println(path);
        JsonObject found = testClass.getObjectByJsonPath("$.left.left.left.left.right", JsonObject.class);
        System.out.println(found);

    }

    @Test
    public void findFromJsonPathArrays(){
        List<JsonObject> finalChildren = createTree(5,root);
        finalChildren.get(finalChildren.size()-168).writeProperty("marker", "ThisOne");
        String path = testClass.findJsonPathToJsonObject(finalChildren.get(finalChildren.size()-168));
        System.out.println(path);
        JsonObject found = testClass.getObjectByJsonPath("$.many[2].many[1].many[1].right.many[0]", JsonObject.class);
        System.out.println(found);

    }

    @Test
    public void findFromJsonPathArraysArray(){
        List<JsonObject> finalChildren = createTree(5,root);
        finalChildren.get(finalChildren.size()-168).writeProperty("marker", "ThisOne");
        String path = testClass.findJsonPathToJsonObject(finalChildren.get(finalChildren.size()-168));
        System.out.println(path);
        List<JsonObject> found = testClass.getListByJsonPath("$.many[2].many[1].many[1].right.many[*]", JsonObject.class);
        System.out.println(found);

    }

    @Test
    public void findFromJsonPathStarred(){
        List<JsonObject> finalChildren = createTree(5,root);
        finalChildren.get(finalChildren.size()-168).writeProperty("marker", "ThisOne");
        String path = testClass.findJsonPathToJsonObject(finalChildren.get(finalChildren.size()-168));
        System.out.println(path);
        List<JsonObject> found = testClass.getListByJsonPath("$.many[2].many[1].many[*].right.many[0]", JsonObject.class);
        System.out.println(found);

    }

    @Test
    public void findFromJsonPathMultiIndex(){
        List<JsonObject> finalChildren = createTree(5,root);
        finalChildren.get(finalChildren.size()-168).writeProperty("marker", "ThisOne");
        String path = testClass.findJsonPathToJsonObject(finalChildren.get(finalChildren.size()-168));
        System.out.println(path);
        List<JsonObject> found = testClass.getListByJsonPath("$.many[2].many[1].many[1,2].right.many[0,1]", JsonObject.class);
        System.out.println(found);

    }

    @Test
    public void findFromJsonPathSlice(){
        List<JsonObject> finalChildren = createTree(5,root);
        finalChildren.get(finalChildren.size()-168).writeProperty("marker", "ThisOne");
        String path = testClass.findJsonPathToJsonObject(finalChildren.get(finalChildren.size()-168));
        System.out.println(path);
        List<JsonObject> found = testClass.getListByJsonPath("$.many[2].many[1].many[1].right.many[0:2]", JsonObject.class);
        System.out.println(found);

    }

    @Test
    public void findPathToJsonObjectWithLists(){
        List<JsonObject> finalChildren = createTree(5,root);
        finalChildren.get(100).writeProperty("marker", "ThisOne");
        List<String> path = testClass.findPathToJsonObject(finalChildren.get(100));

        System.out.println(path);
    }
    private List<JsonObject> createTree(int depth,JsonObject root){
        List<JsonObject> parents = Arrays.asList(root);
        List<JsonObject> children = new ArrayList<>();

        for(int i=0;i<depth;i++){
            for (JsonObject parent : parents) {
                children.addAll(createTree(parent));
            }
            parents = children;
            children = new ArrayList<>();
        }
        return parents;
    }

    private List<JsonObject> createTree(JsonObject jo){
        JsonObject jo1 = factory.createJsonObject();
        JsonObject jo2 = factory.createJsonObject();
        JsonObject jo3 = factory.createJsonObject();
        JsonObject jo4 = factory.createJsonObject();
        JsonObject jo5 = factory.createJsonObject();
        jo.writeProperty("left",jo1);
        jo.writeProperty("right",jo2);
        jo.writeProperty("many",Arrays.asList(jo3,jo4,jo5));
        return Arrays.asList(jo1,jo2,jo3,jo4,jo5);
    }


}
