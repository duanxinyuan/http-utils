package com.dxy.library.util.common;

import com.google.common.base.Defaults;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 反射工具类
 * @author duanxinyuan
 * 2015-01-16 20:43
 */
@Slf4j
public class ReflectUtils {

    //类和属性的缓存
    private static final Map<Class<?>, List<Field>> DECLARED_FIELDS_CACHE = new ConcurrentHashMap<>(256);

    /**
     * 设置属性可见
     */
    public static void setAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers())
                || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 设置方法可见
     */
    public static void setAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 设置构造方法可见
     */
    public static void setAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers())
                || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    /**
     * 合并一个对象的属性到另一个对象（不为空就合并，集合类型会自动合并）
     */
    public static <P, T> T union(P source, T target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        List<String> fieldNames = getFieldNamesIntersection(source, target);
        fieldNames.forEach(name -> {
            Object fieldValue = getFieldValue(source, name);
            if (fieldValue != null) {
                setFieldValue(target, name, fieldValue);
            }
        });
        return target;
    }

    /**
     * 将某个数组里面的对象转换为另一个对象的list返回
     */
    public static <P, T> List<T> copy(P[] ps, Class<T> cls) {
        Objects.requireNonNull(ps);
        return copy(Lists.newArrayList(ps), cls);
    }

    /**
     * 将某个List里面的对象转换为另一个对象的list返回
     */
    public static <P, T> List<T> copy(List<P> ps, Class<T> cls) {
        Objects.requireNonNull(ps);
        List<T> ts = new ArrayList<>();
        List<String> fieldNames = getFieldNamesIntersection(ps.get(0), ClassUtils.instantiateClass(cls));
        for (P p : ps) {
            T t = ClassUtils.instantiateClass(cls);
            fieldNames.forEach(name -> setFieldValue(t, name, getFieldValue(p, name)));
            ts.add(t);
        }
        return ts;
    }

    /**
     * 将某个对象转换为另外一个类型的对象
     * @param source 要转换的对象
     * @param cls 转换成为的类型
     */
    public static <P, T> T copy(P source, Class<T> cls) {
        Objects.requireNonNull(source);
        //创建一个对象
        T t = ClassUtils.instantiateClass(cls);
        return copy(source, t);
    }

    /**
     * 复制一个对象到另一个对象（所有属性覆盖）
     */
    public static <P, T> T copy(P source, T target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        List<String> fieldNames = getFieldNamesIntersection(source, target);
        fieldNames.forEach(name -> setFieldValue(target, name, getFieldValue(source, name)));
        return target;
    }

    /**
     * 获取两个对象的fieldName的交集
     */
    public static <P, T> List<String> getFieldNamesIntersection(P source, T target) {
        if (ClassUtils.isAssignable(source.getClass(), Map.class)) {
            return getFieldNames(target);
        }
        if (ClassUtils.isAssignable(target.getClass(), Map.class)) {
            return getFieldNames(source);
        }
        List<String> sourceFieldNames = getFieldNames(source);
        List<String> targetFieldNames = getFieldNames(target);
        Sets.SetView<String> intersection = Sets.intersection(Sets.newHashSet(sourceFieldNames), Sets.newHashSet(targetFieldNames));
        return Lists.newArrayList(intersection.iterator());
    }

    /**
     * 获取对象的所有fieldName
     */
    public static <P> List<String> getFieldNames(P source) {
        if (ClassUtils.isAssignable(source.getClass(), Map.class)) {
            Map<Object, Object> map = (Map) source;
            return map.keySet().stream()
                    .filter(item -> item instanceof String)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } else {
            //排除 static、transient 字段
            return getFields(source.getClass())
                    .stream()
                    .filter(field -> {
                        int modifier = field.getModifiers();
                        return !Modifier.isStatic(modifier) && !Modifier.isTransient(modifier);
                    })
                    .map(Field::getName)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取元素值
     */
    public static <P, T> T getFieldValue(P source, String name, Class<T> type) {
        Object fieldValue = getFieldValue(source, name);
        return transformValue(type, fieldValue);
    }

    /**
     * 获取元素值
     */
    public static <P> Object getFieldValue(P source, String name) {
        Objects.requireNonNull(source);
        try {
            if (ClassUtils.isAssignable(source.getClass(), Map.class)) {
                Map map = (Map) source;
                return map.get(name);
            }
            Field field = getField(source.getClass(), name);
            if (field == null) {
                return invokeGet(source, name);
            } else {
                return field.get(source);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据deep路径获取参数值
     * 1. {"a": "b"}, "a"
     * 2. {"person": {"name": "JK"}} ==> person.name
     * @param obj 对象
     * @param deep field路径
     * @return 相应field值
     */
    public static Object deepField(Object obj, String deep) {
        if (StringUtils.isEmpty(deep)) {
            return obj;
        }
        Object res = obj;
        String[] items = deep.split("\\.");
        for (String item : items) {
            res = getFieldValue(res, item);
            if (res == null) {
                return null;
            }
        }
        return res;
    }

    /**
     * 设置某个对象的某个值
     * @param target 被设置的对象
     * @param name 对象对应的属性名称
     * @param value 设置对应的值
     */
    public static <P> void setFieldValue(P target, String name, Object value) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(name);
        if (ClassUtils.isAssignable(target.getClass(), Map.class)) {
            Map map = (Map) target;
            map.put(name, value);
            return;
        }
        Field field = getField(target.getClass(), name);
        if (null != field) {
            try {
                field.set(target, transformValue(field.getType(), value));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            invokeSet(target, name, value);
        }
    }

    private static <T> T transformValue(Class<T> type, Object value) {
        if (null == value) {
            if (type.isPrimitive()) {
                return Defaults.defaultValue(type);
            } else {
                return null;
            }
        } else {
            if (type == Short.class && !(value instanceof Short)) {
                value = Short.parseShort(String.valueOf(value));
            } else if ((type == Integer.class || type == int.class) && !(value instanceof Integer)) {
                value = Integer.parseInt(String.valueOf(value));
            } else if ((type == Long.class || type == long.class) && !(value instanceof Long)) {
                value = Long.parseLong(String.valueOf(value));
            } else if ((type == Float.class || type == float.class) && !(value instanceof Float)) {
                value = Float.parseFloat(String.valueOf(value));
            } else if ((type == Double.class || type == double.class) && !(value instanceof Double)) {
                value = Double.parseDouble(String.valueOf(value));
            } else if ((type == Boolean.class || type == boolean.class) && !(value instanceof Boolean)) {
                value = BooleanUtils.toBoolean(String.valueOf(value));
            } else if (type == BigDecimal.class && !(value instanceof BigDecimal)) {
                value = new BigDecimal(String.valueOf(value));
            } else if (type == String.class && !(value instanceof String)) {
                value = String.valueOf(value);
            }
        }
        return (T) value;
    }

    /**
     * 获取元素值
     */
    private static Field getField(Class<?> type, String name) {
        if (type == null) {
            return null;
        }
        try {
            Field field = type.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return getField(type.getSuperclass(), name);
        }
    }

    /**
     * 获取类的全部属性（包括所有父类的）
     */
    public static List<Field> getFields(Class<?> type) {
        List<Field> cacheFields = DECLARED_FIELDS_CACHE.get(type);
        if (cacheFields != null) {
            return cacheFields;
        } else {
            List<Field> fields = Lists.newArrayList();
            Collections.addAll(fields, type.getDeclaredFields());
            if (null != type.getSuperclass()) {
                fields.addAll(getFields(type.getSuperclass()));
            }
            List<Field> result = fields.stream().filter(field -> !"serialVersionUID".equals(field.getName())).collect(Collectors.toList());
            DECLARED_FIELDS_CACHE.put(type, result);
            return result;
        }
    }

    /**
     * 执行set方法
     * @param obj 执行对象
     * @param fieldName 属性
     * @param value 值
     */
    public static <P> void invokeSet(P obj, String fieldName, Object value) {
        try {
            Class<?>[] parameterTypes = new Class<?>[1];
            parameterTypes[0] = obj.getClass().getDeclaredField(fieldName).getType();
            String sb = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = obj.getClass().getMethod(sb, parameterTypes);
            method.invoke(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行get方法
     * @param obj 执行对象
     * @param fieldName 属性
     */
    public static <P> Object invokeGet(P obj, String fieldName) {
        try {
            String sb = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = obj.getClass().getMethod(sb);
            return method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }

    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }

    public static Method findDeclaredMethodWithMinimalParameters(Class<?> clazz, String methodName)
            throws IllegalArgumentException {

        Method targetMethod = findMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
        if (targetMethod == null && clazz.getSuperclass() != null) {
            targetMethod = findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
        }
        return targetMethod;
    }

    public static Method findMethodWithMinimalParameters(Method[] methods, String methodName)
            throws IllegalArgumentException {

        Method targetMethod = null;
        int numMethodsFoundWithCurrentMinimumArgs = 0;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                int numParams = method.getParameterCount();
                if (targetMethod == null || numParams < targetMethod.getParameterCount()) {
                    targetMethod = method;
                    numMethodsFoundWithCurrentMinimumArgs = 1;
                } else if (!method.isBridge() && targetMethod.getParameterCount() == numParams) {
                    if (targetMethod.isBridge()) {
                        // Prefer regular method over bridge...
                        targetMethod = method;
                    } else {
                        // Additional candidate with same length
                        numMethodsFoundWithCurrentMinimumArgs++;
                    }
                }
            }
        }
        if (numMethodsFoundWithCurrentMinimumArgs > 1) {
            throw new IllegalArgumentException("Cannot resolve method '" + methodName +
                    "' to a unique method. Attempted to resolve to overloaded method with " +
                    "the least number of parameters but there were " +
                    numMethodsFoundWithCurrentMinimumArgs + " candidates.");
        }
        return targetMethod;
    }

    /**
     * 解析 getMethodName -> propertyName
     * @param getMethodName 需要解析的
     * @return 返回解析后的字段名称
     */
    public static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith("is")) {
            getMethodName = getMethodName.substring(2);
        }
        // 小写第一个字母
        return StringUtils.firstToLowerCase(getMethodName);
    }

}
