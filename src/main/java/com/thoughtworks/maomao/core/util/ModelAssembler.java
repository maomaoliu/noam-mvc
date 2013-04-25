package com.thoughtworks.maomao.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class ModelAssembler {

    /**
     *     Only support type like Integer instead of primitive type int.
     */
    public <T> T assembleModel(Map<String, String[]> parameterMap, Class<T> modelClass) {
        T t = null;

        try {
            t = modelClass.newInstance();
            Set<Method> setterMethods = getSetterMethods(modelClass);
            String prefix = NameResolver.getInstanceName(modelClass);
            Set<String> keys = parameterMap.keySet();

            List<String> subModels = new ArrayList<String>();
            HashMap<String, String[]> subParameterMap = new HashMap<String, String[]>();

            for (String key : keys) {
                String[] parts = key.split("\\.");
                if (isPrimitiveType(prefix, parts)) {
                    String expectedSetterName = getSetterByPropertyName(parts[1]);
                    String[] valueStrings = parameterMap.get(key);
                    invokeMethodIfPossible(t, expectedSetterName, valueStrings, setterMethods);
                } else if (isComplexType(prefix, parts)) {
                    if (!subModels.contains(parts[1])) {
                        subModels.add(parts[1]);
                    }
                    String newKey = getSubPrefix(prefix, key);
                    subParameterMap.put(newKey, parameterMap.get(key));
                }
            }

            for (String key : subModels) {
                String expectedSetterName = getSetterByPropertyName(key);
                invokeMethodIfPossible(t, expectedSetterName, subParameterMap, setterMethods);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }

    private <T> void invokeMethodIfPossible(T t, String expectedSetterName, String[] valueStrings, Set<Method> setterMethods) throws IllegalAccessException, InvocationTargetException {
        for (Method method : setterMethods) {
            if (method.getName().equals(expectedSetterName)) {
                Object value = parsePrimitiveType(valueStrings, method.getParameterTypes()[0]);
                method.invoke(t, value);
                break;
            }
        }
    }

    private <T> void invokeMethodIfPossible(T t, String expectedSetterName, HashMap<String, String[]> subParameterMap, Set<Method> setterMethods) throws IllegalAccessException, InvocationTargetException {
        for (Method method : setterMethods) {
            if (method.getName().equals(expectedSetterName)) {
                Class<?> subModel = method.getParameterTypes()[0];
                Object subModelInstance = assembleModel(subParameterMap, subModel);
                method.invoke(t, subModelInstance);
                break;
            }
        }
    }

    private String getSubPrefix(String prefix, String key) {
        return key.replaceFirst(prefix + ".", "");
    }

    private boolean isComplexType(String prefix, String[] parts) {
        return parts.length > 2 && parts[0].equals(prefix);
    }

    private String getSetterByPropertyName(String part) {
        return "set" + LOWER_CAMEL.to(UPPER_CAMEL, part);
    }

    private boolean isPrimitiveType(String prefix, String[] parts) {
        return parts.length == 2 && parts[0].equals(prefix);
    }

    private <T> Set<Method> getSetterMethods(Class<T> model) {
        Set<Method> setterMethods = new HashSet<Method>();
        for (Method method : model.getDeclaredMethods()) {
            if (method.getName().startsWith("set")) {
                setterMethods.add(method);
            }
        }
        return setterMethods;
    }

    private <T> T parsePrimitiveType(String[] valueString, Class<T> valueType) {
        try {
            if (valueType.isArray()) {
                return (T) valueString;
            } else if (valueType.equals(Character.class)) {
                Constructor<T> constructor = valueType.getConstructor(Character.TYPE);
                return constructor.newInstance(valueString[0].charAt(0));
            } else {
                Constructor<T> constructor = valueType.getConstructor(String.class);
                return constructor.newInstance(valueString[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
