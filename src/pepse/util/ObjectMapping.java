package pepse.util;

import danogl.GameObject;

import java.util.*;

public class ObjectMapping {

    private static final Map<Integer, ArrayList<GameObject>> objectMapping = new HashMap<>();

    public static void addObj(int k, GameObject gameObject){
        if (!objectMapping.containsKey(k)){
            objectMapping.put(k, new ArrayList<>());
        }
        objectMapping.get(k).add(gameObject);
    }

    public static void removeObj(int k, GameObject gameObject){
        if (objectMapping.containsKey(k)){
            objectMapping.get(k).remove(gameObject);
        }
    }


}
