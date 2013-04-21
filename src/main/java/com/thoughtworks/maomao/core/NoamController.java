package com.thoughtworks.maomao.core;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.stringtemplate.v4.ST;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
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
            if (methodParameter != null && "delete".equals(methodParameter.toLowerCase())) {
                _delete(Integer.parseInt(req.getParameter("id")), req, resp, params);
            } else {
                if (req.getPathInfo().endsWith(nameResolver.getSingularPath())) {
                    _show(Integer.parseInt(req.getParameter("id")), req, resp, params);
                } else {
                    _index(req, resp, params);
                }
            }
        } else if (method.equals(METHOD_POST)) {
            doPost(req, resp);

        } else if (method.equals(METHOD_PUT)) {
            doPut(req, resp);

        } else if (method.equals(METHOD_DELETE)) {


        } else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "http.method_not_implemented");
        }
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

    private void _create(Object instance) {

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

    public void create(Object instance) {

    }
}
