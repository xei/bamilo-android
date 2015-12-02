package com.mobile.newFramework.utils;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.SparseArray;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class used to help with collections.<br>
 * Copy from import org.apache.commons.collections4.ListUtils;
 */
public class CollectionUtils {


    public static boolean containsAll(Collection<?> coll1, Collection<?> coll2) {
        if (coll2.isEmpty()) {
            return true;
        } else {
            Iterator it = coll1.iterator();
            HashSet elementsAlreadySeen = new HashSet();
            Iterator i$ = coll2.iterator();
            boolean foundCurrentElement;
            label43:
            do {
                Object nextElement;
                do {
                    if (!i$.hasNext()) {
                        return true;
                    }
                    nextElement = i$.next();
                } while (elementsAlreadySeen.contains(nextElement));

                foundCurrentElement = false;

                while (true) {
                    if (!it.hasNext()) {
                        continue label43;
                    }

                    Object p = it.next();
                    elementsAlreadySeen.add(p);
                    if (nextElement == null) {
                        if (p == null) {
                            break;
                        }
                    } else if (nextElement.equals(p)) {
                        break;
                    }
                }

                foundCurrentElement = true;
            } while (foundCurrentElement);

            return false;
        }
    }

    public static boolean containsAny(Collection<?> coll1, Collection<?> coll2) {
        Iterator i$;
        Object aColl2;
        if (coll1.size() < coll2.size()) {
            i$ = coll1.iterator();

            while (i$.hasNext()) {
                aColl2 = i$.next();
                if (coll2.contains(aColl2)) {
                    return true;
                }
            }
        } else {
            i$ = coll2.iterator();

            while (i$.hasNext()) {
                aColl2 = i$.next();
                if (coll1.contains(aColl2)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static <O> Map<O, Integer> getCardinalityMap(Iterable<? extends O> coll) {
        HashMap count = new HashMap();
        Iterator i$ = coll.iterator();

        while (i$.hasNext()) {
            Object obj = i$.next();
            Integer c = (Integer) count.get(obj);
            if (c == null) {
                count.put(obj, Integer.valueOf(1));
            } else {
                count.put(obj, Integer.valueOf(c.intValue() + 1));
            }
        }

        return count;
    }

    public static <T> boolean addIgnoreNull(Collection<T> collection, T object) {
        if (collection == null) {
            throw new NullPointerException("The collection must not be null");
        } else {
            return object != null && collection.add(object);
        }
    }

    public static <C> boolean addAll(Collection<C> collection, Iterable<? extends C> iterable) {
        return iterable instanceof Collection ? collection.addAll((Collection) iterable) : addAll(collection, iterable.iterator());
    }

    public static <C> boolean addAll(Collection<C> collection, Iterator<? extends C> iterator) {
        boolean changed;
        for (changed = false; iterator.hasNext(); changed |= collection.add(iterator.next())) {
        }

        return changed;
    }

    public static <C> boolean addAll(Collection<C> collection, Enumeration<? extends C> enumeration) {
        boolean changed;
        for (changed = false; enumeration.hasMoreElements(); changed |= collection.add(enumeration.nextElement())) {
        }

        return changed;
    }

    public static <T> T get(Iterator<T> iterator, int index) {
        int i = index;
        checkIndexBounds(index);

        while (iterator.hasNext()) {
            --i;
            if (i == -1) {
                return iterator.next();
            }

            iterator.next();
        }

        throw new IndexOutOfBoundsException("Entry does not exist: " + i);
    }

    private static void checkIndexBounds(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(ContentValues coll) {
        return coll == null || coll.size() == 0;
    }

    public static boolean isEmpty(SparseArray coll){
        return coll == null || coll.size() == 0;
    }

    public static boolean isNotEmpty(ContentValues coll) {
        return !isEmpty(coll);
    }

    public static boolean isNotEmpty(SparseArray coll){
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Bundle bundle) {
        return bundle == null || bundle.size() == 0;
    }

    public static boolean isNotEmpty(Bundle bundle) {
        return !isEmpty(bundle);
    }

    public static boolean isEmpty(JSONArray jsonArray) {
        return jsonArray == null || jsonArray.length() == 0;
    }

    public static boolean isNotEmpty(JSONArray jsonArray) {
        return !isEmpty(jsonArray);
    }

    public static boolean containsKey(Bundle bundle, String key) {
        return !isEmpty(bundle) && bundle.containsKey(key);
    }

    public static void reverseArray(Object[] array) {
        int i = 0;
        for (int j = array.length - 1; j > i; ++i) {
            Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
        }
    }

    public static <E> E extractSingleton(Collection<E> collection) {
        if (collection != null && collection.size() == 1) {
            return collection.iterator().next();
        } else {
            throw new IllegalArgumentException("Can extract singleton only when collection size == 1");
        }
    }


    public static <T> List<T> defaultIfNull(List<T> list, List<T> defaultList) {
        return list == null ? defaultList : list;
    }

    public static <E> List<E> intersection(List<? extends E> list1, List<? extends E> list2) {
        ArrayList result = new ArrayList();
        List smaller = list1;
        List larger = list2;
        if (list1.size() > list2.size()) {
            smaller = list2;
            larger = list1;
        }

        HashSet hashSet = new HashSet(smaller);
        Iterator i$ = larger.iterator();

        while (i$.hasNext()) {
            Object e = i$.next();
            if (hashSet.contains(e)) {
                result.add(e);
                hashSet.remove(e);
            }
        }

        return result;
    }


    public static <E> List<E> union(List<? extends E> list1, List<? extends E> list2) {
        ArrayList result = new ArrayList(list1);
        result.addAll(list2);
        return result;
    }

    public static boolean isEqualList(Collection<?> list1, Collection<?> list2) {
        if (list1 == list2) {
            return true;
        } else if (list1 != null && list2 != null && list1.size() == list2.size()) {
            Iterator it1 = list1.iterator();
            Iterator it2 = list2.iterator();
            Object obj1 = null;
            Object obj2 = null;

            while (true) {
                if (it1.hasNext() && it2.hasNext()) {
                    obj1 = it1.next();
                    obj2 = it2.next();
                    if (obj1 == null) {
                        if (obj2 == null) {
                            continue;
                        }
                    } else if (obj1.equals(obj2)) {
                        continue;
                    }

                    return false;
                }

                return !it1.hasNext() && !it2.hasNext();
            }
        } else {
            return false;
        }
    }

    public static int hashCodeForList(Collection<?> list) {
        if (list == null) {
            return 0;
        } else {
            int hashCode = 1;

            Object obj;
            for (Iterator it = list.iterator(); it.hasNext(); hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode())) {
                obj = it.next();
            }

            return hashCode;
        }
    }

    public static <E> List<E> retainAll(Collection<E> collection, Collection<?> retain) {
        ArrayList list = new ArrayList(Math.min(collection.size(), retain.size()));
        Iterator i$ = collection.iterator();

        while (i$.hasNext()) {
            Object obj = i$.next();
            if (retain.contains(obj)) {
                list.add(obj);
            }
        }

        return list;
    }

    public static <E> List<E> removeAll(Collection<E> collection, Collection<?> remove) {
        ArrayList list = new ArrayList();
        Iterator i$ = collection.iterator();

        while (i$.hasNext()) {
            Object obj = i$.next();
            if (!remove.contains(obj)) {
                list.add(obj);
            }
        }

        return list;
    }

    public static <E> List<E> synchronizedList(List<E> list) {
        return Collections.synchronizedList(list);
    }

    public static Map<String, String> convertContentValuesToMap(ContentValues contentValues) {
        Map<String, String> data = new HashMap<>();
        for (Map.Entry entrySet: contentValues.valueSet()) {
            data.put(entrySet.getKey().toString(), entrySet.getValue() != null ? entrySet.getValue().toString() : null);
        }
        return data;
    }


    public static Map<String, String> convertContentValuesToSortedMap(ContentValues contentValues) {
        Map<String, String> data = new TreeMap<>();
        for (Map.Entry entrySet: contentValues.valueSet()) {
            data.put(entrySet.getKey().toString(), entrySet.getValue() != null ? entrySet.getValue().toString() : null);
        }
        return data;
    }

}