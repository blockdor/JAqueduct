package com.blocktopus.jaqueduct;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JsonTree {

    public JsonTree(JsonObject root) {
        this.root = root;
    }

    private JsonObject root;

    public List<JsonObject> flattenTree() {
        List<JsonObject> flatList = new ArrayList<>();
        forEachJsonObjectInTree(flatList::add);
        return flatList;
    }

    public <T> T getObjectByJsonPath(String jsonPath, Class<T> clazz) {
        List<T> stuff = getListByJsonPath(jsonPath, clazz);
        if (stuff.size() == 1) {
            return stuff.get(0);
        } else {
            throw new RuntimeException("Too Many Values Returned");
        }
    }

    public <T> List<T> getListByJsonPath(String jsonPath, Class<T> clazz) {
        List<String> paths = new ArrayList<>();
        paths.addAll(Arrays.asList(jsonPath.split("[.]")));
        paths.remove(0);
        return getNextLevel(root, paths, clazz);
    }

    private <T> List<T> getNextLevel(JsonObject here, List<String> jsonPath, Class<T> clazz) {

        String nextName = jsonPath.get(0);
        List<String> newPath = new ArrayList<>();
        newPath.addAll(jsonPath);
        newPath.remove(0);
        if (newPath.isEmpty()) {
            //last level!
            return doWork(here, newPath, clazz, nextName);
        } else {
            List<JsonObject> toProcess = doWork(here, newPath, JsonObject.class, nextName);
            return toProcess.stream().map(jo -> getNextLevel(jo, newPath, clazz)).flatMap(List::stream).collect(Collectors.toList());
        }
    }

    private <T> List<T> doWork(JsonObject here, List<String> jsonPath, Class<T> clazz, String nextName) {
        if (nextName.contains("[")) {
            List<T> list = here.getList(nextName.substring(0, nextName.indexOf("[")), clazz);
            String index = nextName.substring(nextName.indexOf("[") + 1, nextName.indexOf("]"));
            if ("*".equals(index)) {
                return list;
            }
            if (index.contains(",")) {
                String[] numbers = index.split(",");
                List<T> newList = new ArrayList<>();

                for (String number : numbers) {
                    int i = Integer.parseInt(number);
                    if (i < 0) {
                        i = list.size() - i;
                    }
                    newList.add(list.get(i));
                }
                return newList;
            }
            if (index.contains(":")) {
                String[] numbers = index.split(":");
                int start = Integer.parseInt(numbers[0]);
                int end = Integer.parseInt(numbers[1]);
                return list.subList(start, end + 1);
            }
            if (index.contains("?")) {

            }

            int i = Integer.parseInt(index);
            if (i < 0) {
                i = list.size() - i;
            }
            return Collections.singletonList(list.get(i));
        } else {
            return Collections.singletonList(here.getProperty(nextName, clazz));
        }
    }


    public String findJsonPathToJsonObject(JsonObject toFind) {
        List<String> path = findPathToJsonObject(toFind);
        String jsonPath = "$." + String.join(".", path);
        return jsonPath;
    }

    public List<String> findPathToJsonObject(JsonObject toFind) {
        List<String> path = findPathToJsonObject(root, toFind);
        Collections.reverse(path);
        return path;
    }

    private List<String> findPathToJsonObject(JsonObject current, JsonObject toFind) {

        Set<String> names = current.getPropertyNameList();
        for (String name : names) {
            if (current.isList(name, JsonObject.class)) {
                List<JsonObject> joList = current.getListOfJsonObject(name);
                for (int i = 0; i < joList.size(); i++) {
                    if (joList.get(i) == toFind) {
                        List<String> found = new ArrayList<>();
                        found.add(name + "[" + i + "]");
                        return found;
                    } else {
                        List<String> found = findPathToJsonObject(joList.get(i), toFind);
                        if (!found.isEmpty()) {
                            found.add(name + "[" + i + "]");
                            return found;
                        }
                    }
                }
            }
            if (current.isJsonObject(name)) {
                if (current.getJsonObject(name) == toFind) {
                    List<String> found = new ArrayList<>();
                    found.add(name);
                    return found;
                } else {
                    List<String> found = findPathToJsonObject(current.getJsonObject(name), toFind);
                    if (!found.isEmpty()) {
                        found.add(name);
                        return found;
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * finds by object ref, not equals
     * returns empty list if no path exists
     * @param toFind
     * @return
     */
    private List<JsonObject> findTreeToJsonObject(JsonObject toFind) {
        return findTreeToJsonObject(root, toFind);
    }

    private List<JsonObject> findTreeToJsonObject(JsonObject current, JsonObject toFind) {
        if (current == toFind) {
            List<JsonObject> found = new ArrayList<>();
            found.add(current);
            return found;
        }

        Set<String> names = current.getPropertyNameList();
        for (String name : names) {
            if (current.isList(name, JsonObject.class)) {
                List<JsonObject> joList = current.getListOfJsonObject(name);
                for (JsonObject jsonObject : joList) {
                    List<JsonObject> found = findTreeToJsonObject(jsonObject, toFind);
                    if (!found.isEmpty()) {
                        found.add(current);
                        return found;
                    }
                }
            }
            if (current.isJsonObject(name)) {
                List<JsonObject> found = findTreeToJsonObject(current, toFind);
                if (!found.isEmpty()) {
                    found.add(current);
                    return found;
                }
            }
        }
        return Collections.emptyList();
    }

    private void forEachJsonObjectInTree(Consumer<JsonObject> consumer) {
        forEachJsonObjectInTree(consumer, root);
    }

    private void forEachJsonObjectInTree(Consumer<JsonObject> consumer, JsonObject current) {
        Set<String> names = current.getPropertyNameList();
        for (String name : names) {
            if (current.isList(name, JsonObject.class)) {
                List<JsonObject> joList = current.getListOfJsonObject(name);
                joList.forEach(jo -> forEachJsonObjectInTree(consumer, jo));
            }
            if (current.isJsonObject(name)) {
                consumer.accept(current.getJsonObject(name));
                forEachJsonObjectInTree(consumer, current.getJsonObject(name));
            }
        }
    }
}
