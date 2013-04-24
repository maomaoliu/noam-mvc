package com.thoughtworks.maomao.core.controller;

import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.container.WheelContainer;
import com.thoughtworks.maomao.core.util.NameResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ControllerManager {
    private Map<String, Class> servletMap = new HashMap<String, Class>();
    private WheelContainer wheelContainer;
    private Map<String, Object> controllerInstanceMap = new HashMap<String, Object>();

    public ControllerManager(WheelContainer wheelContainer) {
        this.wheelContainer = wheelContainer;
        init(wheelContainer);
    }

    public void init(WheelContainer wheelContainer) {
        Set<Class> controllers = wheelContainer.getAllClassWithAnnotation(Controller.class);

        for (Class controller : controllers) {
            servletMap.put(NameResolver.getSingularPath(controller), controller);
        }
    }

    public Object dispatchController(String pathInfo) {
        Object controller = controllerInstanceMap.get(pathInfo);
        if (controller == null) {
            Class aClass = servletMap.get(pathInfo);
            if (aClass != null) {
                controller = wheelContainer.getWheelInstance(aClass);
                controllerInstanceMap.put(pathInfo, controller);
            }
        }
        return controller;
    }
}
