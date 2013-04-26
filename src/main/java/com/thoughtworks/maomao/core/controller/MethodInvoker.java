package com.thoughtworks.maomao.core.controller;

import com.thoughtworks.maomao.annotation.Param;
import com.thoughtworks.maomao.core.util.ModelAssembler;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodInvoker {
    private ControllerManager controllerManager;
    private ModelAssembler modelAssembler;

    public MethodInvoker(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
        this.modelAssembler = new ModelAssembler();
    }


    public Object invokeMethod(Object controller, String methodName, HttpServletRequest req) throws InvocationTargetException, IllegalAccessException {
        Method method = controllerManager.getMethod(controller.getClass(), methodName);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();

        List<Object> paramValues = new ArrayList<Object>();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            String string = getParameterValue(parameterAnnotations[i], req);
            Object parameterValue;
            if (modelAssembler.isPrimitiveType(parameterTypes[i])) {
                parameterValue = modelAssembler.parsePrimitiveType(new String[]{string}, parameterTypes[i]);
            } else {
                parameterValue = modelAssembler.assembleModel(req.getParameterMap(), parameterTypes[i]);
            }

            paramValues.add(parameterValue);
        }
        return invokeMethod(controller, method, paramValues);
    }

    private Object invokeMethod(Object controller, Method method, List<Object> paramValues) throws InvocationTargetException, IllegalAccessException {
        int paramsNumber = method.getParameterTypes().length;
        return method.invoke(controller, paramValues.subList(0, paramsNumber).toArray());
    }

    private String getParameterValue(Annotation[] annotations, HttpServletRequest req) {
        if (annotations.length == 1 && annotations[0].annotationType().equals(Param.class)) {
            return req.getParameter(((Param) annotations[0]).value());
        } else throw new RuntimeException("Wrong parameter error.");
    }
}
