package dev.neylz.uniformsreader.util;

import java.lang.reflect.Field;
import java.util.Arrays;

public class AttributesGetter {

    public static Field[] getFields(Class<?> clazz) {
        return clazz.getDeclaredFields();
    }

    public static Field[] filterNames(Field[] fields, String[] names) {
        return Arrays.stream(fields).filter(field -> Arrays.stream(names).anyMatch(name -> field.getName().contains(name))).toArray(Field[]::new);
    }
}
