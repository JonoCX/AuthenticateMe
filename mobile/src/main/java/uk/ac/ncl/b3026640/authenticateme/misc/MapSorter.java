package uk.ac.ncl.b3026640.authenticateme.misc;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Handy class to sort a generic map by its values or keys
 * either ascending or descending
 *
 * @author Jonathan Carlton
 */

public class MapSorter {

    /**
     * Generic method to sort a map by its values - ascending
     * @param map   to be sorted
     * @param <K>   generic key type
     * @param <V>   generic value type
     * @return      sorted map, by value
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> valueAscending(Map<K, V> map) {
        if (map.isEmpty())
            return map;

        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> kvEntry, Map.Entry<K, V> t1) {
                return (kvEntry.getValue()).compareTo(t1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> m : list)
            result.put(m.getKey(), m.getValue());
        return result;
    }

    /**
     * Generic method to sort a map by its values - descending
     * @param map   to be sorted
     * @param <K>   generic key type
     * @param <V>   generic value type
     * @return      sorted map, by value
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> valueDescending(Map<K, V> map) {
        if (map.isEmpty())
            return map;

        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> kvEntry, Map.Entry<K, V> t1) {
                return (t1.getValue()).compareTo(kvEntry.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> m : list)
            result.put(m.getKey(), m.getValue());
        return result;
    }

    /**
     * Generic method to sort a map by its keys - descending
     * @param map   to be sorted
     * @param <K>   generic key type
     * @param <V>   generic value type
     * @return      sorted map, by value
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> keyDescending(Map<K, V> map) {
        if (map.isEmpty())
            return map;

        Map<K, V> result = new TreeMap<>(new Comparator<K>() {
            @Override
            public int compare(K k, K t1) {
                return (t1.toString()).compareTo(k.toString());
            }
        });
        result.putAll(map);
        return result;
    }

    /**
     * Generic method to sort a map by its keys - ascending
     * @param map   to be sorted
     * @param <K>   generic key type
     * @param <V>   generic value type
     * @return      sorted map, by value
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> keyAscending(Map<K, V> map) {
        if (map.isEmpty())
            return map;
        return new TreeMap<K, V>(map);
    }

}
