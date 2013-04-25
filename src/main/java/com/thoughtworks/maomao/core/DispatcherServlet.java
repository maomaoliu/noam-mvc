package com.thoughtworks.maomao.core;

import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.container.WheelContainer;
import com.thoughtworks.maomao.core.controller.ControllerManager;
import com.thoughtworks.maomao.core.util.ModelAssembler;
import com.thoughtworks.maomao.core.util.NameResolver;
import com.thoughtworks.maomao.core.view.TemplateFactory;
import com.thoughtworks.maomao.core.view.ViewTemplate;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    public static final String CONTEXT_PATH = "contextPath";

    private static final String GET = "GET";
    private static final String POST = "POST";
    private ControllerManager controllerManager;
    private NameResolver nameResolver;
    private ModelAssembler modelAssembler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WheelContainer wheelContainer = (WheelContainer) config.getServletContext().getAttribute(NoamServletContextListener.WHEEL_CONTAINER);
        controllerManager = new ControllerManager(wheelContainer);
        String appPath = config.getServletContext().getInitParameter(NoamServletContextListener.APP_PATH);
        nameResolver = new NameResolver(appPath);
        modelAssembler = new ModelAssembler();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        Object controller = controllerManager.dispatchController(pathInfo);
        doService(req, res, controller);
    }

    private void doService(HttpServletRequest req, HttpServletResponse res, Object controller) throws IOException {
        String method = req.getMethod();
        if (method.equals(GET) || method.equals(POST)) {
            _proceed(controller, method, req, res);
        } else {
            String errMsg = "Only support GET and REQUEST methods.";
            res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
        }
    }

    private void _proceed(Object controller, String methodCode, HttpServletRequest req, HttpServletResponse res) throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CONTEXT_PATH, req.getServletContext().getContextPath());
        if (methodCode.equals(GET)) {
            doGet(controller, params, req, res);
        } else {
            doPost(controller, params, req, res);
        }
    }

    private void doGet(Object controller, Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws IOException {
        String methodName = req.getParameter("method");
        try {
            Method method = controller.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class, Map.class);
            method.invoke(controller, req, res, params);
            String view = nameResolver.getView(controller, methodName);
            render(view, res, params);
        } catch (Exception e) {
            String errMsg = "Do GET failed.";
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, errMsg);
        }
    }

    private void doPost(Object controller, Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws IOException {
        String methodName = nameResolver.toPostName(req.getParameter("method"));
        try {
            Object model = modelAssembler.assembleModel(req.getParameterMap(), getModelClass(controller));
            params.put(nameResolver.getInstanceName(getModelClass(controller)), model);
            Method method = controller.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class, Map.class);
            Object object = method.invoke(controller, req, res, params);
            if (object != null && object.getClass() == String.class) {
                String redirectURL = (String) object;
                res.sendRedirect(redirectURL);
            }
        } catch (Exception e) {
            String errMsg = "Do POST failed.";
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, errMsg);
        }
    }

    private void render(String view, HttpServletResponse response, Map<String, Object> map) throws IOException {
        String template = getServletContext().getInitParameter("templateName");
        ViewTemplate viewTemplate = TemplateFactory.getViewTemplate(template);
        response.getOutputStream().write(viewTemplate.render(view, map).getBytes());
    }

    private Class getModelClass(Object controller) {
        Controller controllerAnnotation = (Controller) controller.getClass().getAnnotation(Controller.class);
        return controllerAnnotation.model();
    }

}
