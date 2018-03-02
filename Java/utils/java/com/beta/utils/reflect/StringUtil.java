package com.beta.utils.reflect;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public abstract class StringUtil {

    private static final Map<Class<?>, Class<?>> ignoreArrayClassMap = new HashMap<>();

    static {
        ignoreArrayClassMap.put(boolean[].class, boolean[].class);
        ignoreArrayClassMap.put(byte[].class, byte[].class);
        ignoreArrayClassMap.put(char[].class, char[].class);
        ignoreArrayClassMap.put(short[].class, short[].class);
        ignoreArrayClassMap.put(int[].class, int[].class);
        ignoreArrayClassMap.put(long[].class, long[].class);
        ignoreArrayClassMap.put(double[].class, double[].class);
        ignoreArrayClassMap.put(float[].class, float[].class);

        ignoreArrayClassMap.put(Boolean[].class, Boolean[].class);
        ignoreArrayClassMap.put(Byte[].class, Byte[].class);
        ignoreArrayClassMap.put(Character[].class, Character[].class);
        ignoreArrayClassMap.put(Short[].class, Short[].class);
        ignoreArrayClassMap.put(Integer[].class, Integer[].class);
        ignoreArrayClassMap.put(Long[].class, Long[].class);
        ignoreArrayClassMap.put(Double[].class, Double[].class);
        ignoreArrayClassMap.put(Float[].class, Float[].class);

    }

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    /**
     * 递归对 对象图里的所有 String 字段进行操作. TODO:优化性能,缓存每个 Type 以及能做的操作.
     *
     * @param object
     * @return 对String字段处理后的Object.
     */
    public static Object recursionStringAction(Object object, Function<String, String> mapper) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();

        if (!maybeNeedProcess(clazz)) {
            return object;
        } else if (clazz.equals(String.class)) {
            object = mapper.apply((String) object);
            return object;
        } else if (isCustomType(clazz)) { // 自定义类
            try {
                for (PropertyDescriptor pd : Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors()) {
                    // 使用了标准 Introspector API，所以只能作用于标准Bean, setter、getter
                    // 只支持标准格式.

                    // TODO:支持没有setter但有field的
                    Method getter = pd.getReadMethod();
                    Method setter = pd.getWriteMethod();
                    Class<?> returnType = getter.getReturnType();

                    Object value = getter.invoke(object);
                    if (value == null) {
                        continue;
                    }

                    if (returnType.equals(String.class)) { // 免去一次递归调用
                        Object realValue = mapper.apply((String) value);
                        if (setter != null) {
                            setter.invoke(object, realValue);
                        }
                    } else if (maybeNeedProcess(returnType)) {
                        recursionStringAction(value, mapper);
                    } else {
                        continue;
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException("RecursionStringAction fail,because: ", e);
            }
        } else {

            // String[]
            if (object.equals(String[].class)) {
                String[] propertyArray = (String[]) (object);
                if (propertyArray.length > 0) {

                    String[] modifiedArray = new String[propertyArray.length];
                    for (int i = 0; i < propertyArray.length; i++) {
                        if (propertyArray[i] != null) {
                            modifiedArray[i] = mapper.apply(propertyArray[i]);
                        }
                        object = modifiedArray;
                    }
                }
            } else if (Collection.class.isAssignableFrom(clazz)) {
                // Collection
                Collection collectionProperty = (Collection) object;
                if (!collectionProperty.isEmpty()) {
                    for (int index = 0; index < collectionProperty.size(); index++) {
                        if (collectionProperty.toArray()[index] instanceof String) {

                            String element = (String) collectionProperty.toArray()[index];
                            if (element != null) {
                                // Check if List was created with
                                // Arrays.asList (non-resizable Array)
                                if (collectionProperty instanceof List) {
                                    ((List) collectionProperty).set(index, mapper.apply(element));
                                } else {
                                    collectionProperty.remove(element);
                                    collectionProperty.add(mapper.apply(element));
                                }
                            }
                        } else {
                            // Recursively revisit with the current property
                            recursionStringAction(collectionProperty.toArray()[index], mapper);
                        }
                    }
                }

            } else if (Map.class.isAssignableFrom(clazz)) {
                // Map
                Map mapProperty = (Map) (object);
                if (!mapProperty.isEmpty()) {
                    // 还可以替换Keys，暂时不支持吧
                    // Values
                    for (Map.Entry entry : (Set<Map.Entry>) mapProperty.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof String) {
                            entry.setValue(mapper.apply((String) value));
                        } else {
                            recursionStringAction(value, mapper);
                        }
                    }
                }

            } else if (isCustomTypeArray(clazz)) {
                // Object[]
                Object[] propertyArray = (Object[]) (object);
                if (propertyArray.length > 0) {
                    for (Object aPropertyArray : propertyArray) {
                        if (aPropertyArray != null) {
                            recursionStringAction(aPropertyArray, mapper);
                        }
                    }
                }
            }
        }

        return object;
    }

    /**
     * 该种类型字段是否需要处理.
     */
    private static boolean maybeNeedProcess(Class<?> clazz) {
        return !(ClassUtils.isPrimitiveOrWrapper(clazz) || ignoreArrayClassMap.containsKey(clazz));
    }

    private static boolean isCustomType(Class<?> clazz) {
        return !ClassUtils.isPrimitiveOrWrapper(clazz) && !clazz.isArray() && !Map.class.isAssignableFrom(clazz)
                && !Collection.class.isAssignableFrom(clazz);
    }

    private static boolean isCustomTypeArray(Class<?> clazz) {

        if (clazz.isArray()) {
            return true;
        }

        return false;
    }

}
