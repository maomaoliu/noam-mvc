package com.thoughtworks.maomao.core;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.container.WheelContainer;
import org.stringtemplate.v4.ST;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

public class DispatcherServlet extends HttpServlet {
    public static final String CONTEXT_PATH = "contextPath";

    private static final String GET = "GET";
    private static final String POST = "POST";
    private WheelContainer wheelContainer;
    private Map<String, Class> servletMap = new HashMap<String, Class>();
    private Map<String, Object> controllerInstanceMap = new HashMap<String, Object>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        wheelContainer = (WheelContainer) config.getServletContext().getAttribute(NoamServletContextListener.WHEEL_CONTAINER);
        Set<Class> controllers = wheelContainer.getAllClassWithAnnotation(Controller.class);
        for (Class controller : controllers) {
            Controller controllerAnnotation = (Controller) controller.getAnnotation(Controller.class);
            Class model = controllerAnnotation.model();
            NameResolver nameResolver = new NameResolver(model);
            servletMap.put(nameResolver.getSingularPath(), controller);
            servletMap.put(nameResolver.getPluralPath(), controller);
        }
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Object controller = dispatchController(pathInfo);

        String method = req.getMethod();
        if (method.equals(GET)) {
            _proceed(controller, GET, req, res);
        } else if (method.equals(POST)) {
            _proceed(controller, POST, req, res);
        } else {
            String errMsg = "Only support GET and REQUEST methods.";
            res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
        }
    }

    private void _proceed(Object controller, String methodCode, HttpServletRequest req, HttpServletResponse res) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CONTEXT_PATH, req.getServletContext().getContextPath());
        String methodName = req.getParameter("method");
        try {
            NameResolver nameResolver = new NameResolver(controller.getClass());
            if (methodCode == POST) {
                methodName += "Post";
                Class model = getModelClass(controller);
                Object instance = createModelInstance(req.getParameterMap(), model);
                params.put(UPPER_CAMEL.to(LOWER_CAMEL, model.getSimpleName()), instance);
            }
            Method method = controller.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class, Map.class);
            Object object = method.invoke(controller, req, res, params);
            if (object != null && object.getClass() == String.class) {
                String redirectURL = (String) object;
                res.sendRedirect(redirectURL);
            } else {
                render(nameResolver.getView(methodName), res, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <T> T createModelInstance(Map<String,String[]> parameterMap, Class<T> model) {
        T t = null;
        try {
            t = model.newInstance();
            Set<Method> setterMethods = getSetterMethods(model);
            String prefix = UPPER_CAMEL.to(LOWER_CAMEL, model.getSimpleName());
            Set<String> keys = parameterMap.keySet();

            List<String> subModels = new ArrayList<>();
            HashMap<String, String[]> subParameterMap = new HashMap<>();

            for(String key: keys){
                String[] parts = key.split("\\.");
                if(parts.length == 2 && parts[0].equals(prefix)){
                    String expectedSetterName = "set" + LOWER_CAMEL.to(UPPER_CAMEL, parts[1]);
                    // setPrimaryType
                    for (Method method : setterMethods) {
                        if (method.getName().equals(expectedSetterName)) {
                            String[] valueStrings = parameterMap.get(key);
                            Object value = parsePrimaryType(valueStrings, method.getParameterTypes()[0]);
                            method.invoke(t, value);
                            break;
                        }
                    }
                } else if(parts.length > 2 && parts[0].equals(prefix)){
                    String newKey = key.replaceFirst(prefix + ".", "");
                    if(!subModels.contains(parts[1])){
                        subModels.add(parts[1]);
                    }
                    subParameterMap.put(newKey, parameterMap.get(key));
                }

            }

            for(String key: subModels){
                String expectedSetterName = "set" + LOWER_CAMEL.to(UPPER_CAMEL, key);
                for (Method method : setterMethods) {
                    if (method.getName().equals(expectedSetterName)) {
                        Class<?> subModel = method.getParameterTypes()[0];
                        Object subModelInstance = createModelInstance(subParameterMap, subModel);
                        method.invoke(t, subModelInstance);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }

    private Class getModelClass(Object controller) {
        Controller controllerAnnotation = (Controller) controller.getClass().getAnnotation(Controller.class);
        return controllerAnnotation.model();
    }

    private Object dispatchController(String pathInfo) {
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

    private void render(String viewTemplate, HttpServletResponse response, Map<String, Object> map) throws IOException {
        ST st = new ST(Files.toString(new File(getClass().getResource(viewTemplate).getFile()), Charsets.UTF_8), '$', '$');
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            st.add(entry.getKey(), entry.getValue());
        }
        response.getOutputStream().write(st.render().getBytes());
    }

    private <T> Set<Method> getSetterMethods(Class<T> model) {
        Set<Method> setterMethods = new HashSet<Method>();
        for (Method method : model.getDeclaredMethods()) {
            if(method.getName().startsWith("set")){
                setterMethods.add(method);
            }
        }
        return setterMethods;
    }

    private <T> T parsePrimaryType(String[] valueString, Class<T> valueType) {
        try {
            if (valueType.equals(Arrays.class)) {
                return (T) valueString;
            } else if(valueType.equals(Character.class)){
                Constructor<T> constructor = valueType.getConstructor(Character.class);
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
