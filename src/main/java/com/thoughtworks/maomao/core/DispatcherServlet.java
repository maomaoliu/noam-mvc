package com.thoughtworks.maomao.core;

import com.thoughtworks.maomao.container.WheelContainer;
import com.thoughtworks.maomao.core.controller.ControllerManager;
import com.thoughtworks.maomao.core.controller.MethodInvoker;
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
import java.util.HashMap;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {
    public static final String CONTEXT_PATH = "contextPath";

    private static final String GET = "GET";
    private static final String POST = "POST";
    private ControllerManager controllerManager;
    private MethodInvoker methodInvoker;
    private NameResolver nameResolver;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WheelContainer wheelContainer = (WheelContainer) config.getServletContext().getAttribute(NoamServletContextListener.WHEEL_CONTAINER);
        controllerManager = new ControllerManager(wheelContainer);
        String appPath = config.getServletContext().getInitParameter(NoamServletContextListener.APP_PATH);
        nameResolver = new NameResolver(appPath);
        methodInvoker = new MethodInvoker(controllerManager);
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
            Object methodResult = methodInvoker.invokeMethod(controller, methodName, req);
            if (isMap(methodResult)) {
                params.putAll((Map<? extends String, ?>) methodResult);
                String view = nameResolver.getView(controller, methodName);
                render(view, res, params);
            } else if (isString(methodResult)) {
                String redirectURL = (String) methodResult;
                res.sendRedirect(redirectURL);
            }
        } catch (Exception e) {
            String errMsg = "Do GET failed.";
            res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, errMsg);
        }
    }

    private void doPost(Object controller, Map<String, Object> params, HttpServletRequest req, HttpServletResponse res) throws IOException {
        String methodName = NameResolver.toPostName(req.getParameter("method"));
        try {
            Object methodResult = methodInvoker.invokeMethod(controller, methodName, req);

            if (isString(methodResult)) {
                String redirectURL = (String) methodResult;
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


    private boolean isMap(Object methodResult) {
        if (methodResult == null) return false;
        return Map.class.isAssignableFrom(methodResult.getClass());
    }

    private boolean isString(Object methodResult) {
        if (methodResult == null) return false;
        return methodResult.getClass().equals(String.class);
    }

}
