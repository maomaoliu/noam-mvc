package com.thoughtworks.maomao.core.util;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class NameResolver {
    private String appPath;

    public NameResolver(String appPath) {
        this.appPath = appPath;
    }

    public String getView(Object controller, String methodName) {
        String controllerName = getPathName(controller);
        String path = appPath.replace('.', '/');
        return String.format("/%s/view/%s/%s", path, controllerName, methodName);
    }

    public static String getInstanceName(Class klazz) {
        return UPPER_CAMEL.to(LOWER_CAMEL, klazz.getSimpleName());
    }

    public static String getInstanceName(String className) {
        return UPPER_CAMEL.to(LOWER_CAMEL, className);
    }

    public static String toPostName(String methodName) {
        methodName += "Post";
        return methodName;
    }

    public static String getSingularPath(Class controllerClass) {
        return "/" + getPathName(controllerClass);
    }

    private static String getPathName(Object controller) {
        return getInstanceName(controller.getClass()).replaceFirst("Controller", "");
    }

    private static String getPathName(Class controllerClass) {
        return getInstanceName(controllerClass.getSimpleName()).replaceFirst("Controller", "");
    }

}
