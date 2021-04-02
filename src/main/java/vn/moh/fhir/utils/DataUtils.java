package vn.moh.fhir.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DataUtils {

    @SafeVarargs
    public static <T> List<T> listOf(T... arr) {
        var lst = new ArrayList<T>();
        for (T obj : arr) {
            if (obj != null)
                lst.add(obj);
        }
        return lst;
    }

    public static <T> T getFirst(List<T> lst) {
        if (lst != null && lst.size() > 0) {
            return lst.get(0);
        }
        return null;
    }

    public static Map<String, String> mapOf(String... obj) {
        var m = new HashMap<String, String>();
        if (obj.length % 2 != 0) {
            throw new RuntimeException("Number of map items must be even");
        }
        int n = obj.length / 2;
        for (int i = 0; i < n; i++) {
            String key = obj[2 * i];
            String value = obj[2 * i + 1];
            if (key != null) {
                m.put(key, value);
            }
        }
        return m;
    }

    public static Map<String, ?> emptyMap() {
        return new HashMap<>();
    }

    public static Map<String, Object> mapOf(Object... obj) {
        var m = new HashMap<String, Object>();
        if (obj.length % 2 != 0) {
            throw new RuntimeException("Number of map items must be even");
        }
        int n = obj.length / 2;
        for (int i = 0; i < n; i++) {
            Object key = obj[2 * i];
            Object value = obj[2 * i + 1];
            if (key != null) {
                m.put(key.toString(), value);
            }
        }
        return m;
    }

    public static  Map<String, Object> mapOf3(String keyLevel1, String keyLevel2, Object value) {
        if (value != null) {
            return Map.of(keyLevel1, Map.of(keyLevel2, value));
        }
        return new HashMap<>();
    }

    public static <T, U> List<U> transform(List<T> lst, Function<T, U> func) {
        if (lst != null) {
            return lst.stream().filter(x -> x != null).map(x -> func.apply(x)).collect(Collectors.toList());
        }
        return null;
    }

    public static String joinString(List<String> lst, String delimiter) {
        if (lst == null)
            return null;

        return lst.stream().collect(Collectors.joining(delimiter));
    }
    
    public static Double parseDouble(Object obj) {
        try {
            return Double.valueOf(String.valueOf(obj));
        }catch(NumberFormatException e) {
            return null;
        }        
    }
    
    public static Integer parseInt(Object obj) {
        try {
            return Integer.valueOf(String.valueOf(obj));
        }catch(NumberFormatException e) {
            return null;
        }        
    }
    
    public static Boolean parseBool(Object obj) {
        try {
            return Boolean.valueOf(String.valueOf(obj));
        }catch(NumberFormatException e) {
            return null;
        }        
    }
    
    public static <T> List<T> filter(List<T> lst, Predicate<T> pred) {
        if(lst != null) {
            return lst.stream().filter(pred).collect(Collectors.toList());
        }
        return null;
    }
    
    public static <T> boolean anyMatch(List<T> lst, Predicate<T> pred) {
        if(lst != null) {
            return lst.stream().anyMatch(pred);
        }
        return false;
    }
    
    public static <T> boolean allMatch(List<T> lst, Predicate<T> pred) {
        if(lst != null) {
            return lst.stream().allMatch(pred);
        }
        return false;
    }
    
    public static <T> T findFirst(List<T> lst, Predicate<T> pred) {
        if(lst != null) {
            return lst.stream().filter(pred).findFirst().orElse(null);
        }
        return null;
    }
}