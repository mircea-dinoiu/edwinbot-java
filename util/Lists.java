package util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Lists {
    final private static boolean DEFAULT_REMOVE_DUPLICATES_KEEP_ORDER = false;

    public static <T> void removeDuplicates(List<T> list, boolean keepOrder) {
        Set<T> items = keepOrder ? new LinkedHashSet<>(list) : new HashSet<>(list);

        list.clear();
        list.addAll(items);
    }

    public static <T> void removeDuplicates(List<T> list) {
        removeDuplicates(list, DEFAULT_REMOVE_DUPLICATES_KEEP_ORDER);
    }
}
