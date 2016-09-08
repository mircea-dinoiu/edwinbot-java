package common.repository;

import java.util.HashMap;
import java.util.Map;

abstract public class Repository {
    final private static Map<Object, RepositoryItem> items = new HashMap<>();

    public static RepositoryItem get(Object key) {
        if (items.containsKey(key)) {
            return items.get(key);
        } else {
            return null; // TODO
        }
    }
}
