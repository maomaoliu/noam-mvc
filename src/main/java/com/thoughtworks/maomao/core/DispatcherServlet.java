package com.thoughtworks.maomao.core;

import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.container.WheelContainer;
import com.thoughtworks.maomao.core.controller.ControllerManager;
import com.thoughtworks.maomao.core.util.NameResolver;
import com.thoughtworks.maomao.core.view.TemplateFactory;
import com.thoughtworks.maomao.core.view.ViewTemplate;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private ControllerManager controllerManager;
    private NameResolver nameResolver;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WheelContainer wheelContainer = (WheelContainer) config.getServletContext().getAttribute(NoamServletContextListener.WHEEL_CONTAINER);
        controllerManager = new ControllerManager(wheelContainer);
        String appPath = config.getServletContext().getInitParameter(NoamServletContextListener.APP_PATH);
        nameResolver = new NameResolver(appPath);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Object controller = controllerManager.dispatchController(pathInfo);
        doService(req, res, controller);
    }

    private void doService(HttpServletRequest req, HttpServletResponse res, Object controller) throws IOException {
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
        try {
            String methodName = req.getParameter("method");
            if (methodCode == POST) {
                methodName = nameResolver.toPostName(methodName);
                Class modelClass = getModelClass(controller);
                Object model = assembleModel(req.getParameterMap(), modelClass);
                params.put(nameResolver.getInstanceName(modelClass), model);
            }
            Method method = controller.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class, Map.class);
            Object object = method.invoke(controller, req, res, params);
            if (object != null && object.getClass() == String.class) {
                String redirectURL = (String) object;
                res.sendRedirect(redirectURL);
            } else {
                String view = nameResolver.getView(controller, methodName);
                render(view, res, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <T> T assembleModel(Map<String, String[]> parameterMap, Class<T> model) {
        T t = null;
        try {
            t = model.newInstance();
            Set<Method> setterMethods = getSetterMethods(model);
            String prefix = nameResolver.getInstanceName(model);
            Set<String> keys = parameterMap.keySet();

            List<String> subModels = new ArrayList<String>();
            HashMap<String, String[]> subParameterMap = new HashMap<String, String[]>();

            for (String key : keys) {
                String[] parts = key.split("\\.");
                if (parts.length == 2 && parts[0].equals(prefix)) {
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
                } else if (parts.length > 2 && parts[0].equals(prefix)) {
                    String newKey = key.replaceFirst(prefix + ".", "");
                    if (!subModels.contains(parts[1])) {
                        subModels.add(parts[1]);
                    }
                    subParameterMap.put(newKey, parameterMap.get(key));
                }

            }

            for (String key : subModels) {
                String expectedSetterName = "set" + LOWER_CAMEL.to(UPPER_CAMEL, key);
                for (Method method : setterMethods) {
                    if (method.getName().equals(expectedSetterName)) {
                        Class<?> subModel = method.getParameterTypes()[0];
                        Object subModelInstance = assembleModel(subParameterMap, subModel);
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

    private void render(String view, HttpServletResponse response, Map<String, Object> map) throws IOException {
        String template = getServletContext().getInitParameter("templateName");
        ViewTemplate viewTemplate = TemplateFactory.getViewTemplate(template);
        response.getOutputStream().write(viewTemplate.render(view, map));
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

    private <T> T parsePrimaryType(String[] valueString, Class<T> valueType) {
        try {
            if (valueType.equals(Arrays.class)) {
                return (T) valueString;
            } else if (valueType.equals(Character.class)) {
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
