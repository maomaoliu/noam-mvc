package com.thoughtworks.maomao.core;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.thoughtworks.maomao.annotation.Controller;
import org.stringtemplate.v4.ST;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class NoamController extends HttpServlet {

    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    public static final String CONTEXT_PATH = "contextPath";
    private NameResolver nameResolver = new NameResolver(getClass());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CONTEXT_PATH, req.getServletContext().getContextPath());
        if (method.equals(METHOD_GET)) {
            String methodParameter = req.getParameter("method");
            if (methodParameter == null) {
                _index(req, resp, params);
            } else if ("delete".equals(methodParameter.toLowerCase())) {
                _delete(Integer.parseInt(req.getParameter("id")), req, resp, params);
            } else if ("create".equals(methodParameter.toLowerCase())) {
                _create(req, resp, params);
            } else if (methodParameter == null && req.getPathInfo().endsWith(nameResolver.getSingularPath())) {
                _show(Integer.parseInt(req.getParameter("id")), req, resp, params);
            } else {
                _proceed(req, resp, methodParameter, params);
            }
        } else if (method.equals(METHOD_POST)) {
            String methodParameter = req.getParameter("method");
            if ("create".equals(methodParameter.toLowerCase())) {
                String redirectURL = _doSave(req, resp, params);
                resp.sendRedirect(redirectURL);
            } else {
                _proceed(req, resp, methodParameter, params);
            }
        } else if (method.equals(METHOD_PUT)){
            doPut(req, resp);
        } else if (method.equals(METHOD_DELETE)){

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "http.method_not_implemented");
        }

    }

    private void _proceed(HttpServletRequest req, HttpServletResponse resp, String methodName, Map<String, Object> params) {
        try {
            Class c = this.getClass();
            Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class, Map.class);
            method.invoke(this, req, resp, params);
            render(nameResolver.getView(methodName), resp, params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private <T> T createModelInstance(HttpServletRequest request, Class<T> model) {
        T t = null;
        try {
            t = model.newInstance();
            Set<Method> setterMethods = getSetterMethods(model);
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                String expectedSetterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                for (Method method : setterMethods) {
                    if (method.getName().equals(expectedSetterName)) {
                        String value = request.getParameter(name);
                        method.invoke(t, value);
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return t;  //To change body of created methods use File | Settings | File Templates.
    }

    private <T> Set<Method> getSetterMethods(Class<T> model) {
        Set<Method> setterMethods = new HashSet<Method>();
        for (Method method : model.getDeclaredMethods()) {
            setterMethods.add(method);
        }
        return setterMethods;
    }

    private String _doSave(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        Controller controllerAnnotation = (Controller) this.getClass().getAnnotation(Controller.class);
        Class model = controllerAnnotation.model();
        Object object = createModelInstance(req, model);
        return doSave(object);
    }

    public String doSave(Object object) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private void render(String viewTemplate, HttpServletResponse response, Map<String, Object> map) throws IOException {
        ST st = new ST(Files.toString(new File(getClass().getResource(viewTemplate).getFile()), Charsets.UTF_8), '$', '$');
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            st.add(entry.getKey(), entry.getValue());
        }
        response.getOutputStream().write(st.render().getBytes());
    }

    private void _index(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) throws IOException {
        List objects = index(req, resp, params);
        params.put(nameResolver.getPlural(), objects);
        render(nameResolver.getIndexView(), resp, params);
    }

    private void _show(Integer id, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) throws IOException {
        Object object = show(id, req, resp, params);
        params.put(nameResolver.getSingular(), object);
        render(nameResolver.getShowView(), resp, params);
    }

    private void _update(Object instance) {

    }

    private void _delete(Integer id, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) throws IOException {
        delete(id, req, resp, params);
        resp.sendRedirect(params.get(CONTEXT_PATH) + nameResolver.getPluralPath());
    }

    private void _create(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) throws IOException {
        Object object = create(req, resp, params);
        params.put(nameResolver.getSingular(), object);
        render(nameResolver.getCreateView(), resp, params);
    }

    public List index(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        return new ArrayList();
    }

    public Object show(Integer id, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        return null;
    }

    public void update(Object instance) {

    }

    public void delete(Integer id, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {

    }

    public Object create(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> params) {
        return null;
    }

}
