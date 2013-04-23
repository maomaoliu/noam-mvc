package com.thoughtworks.maomao.core;

import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.annotation.Service;
import com.thoughtworks.maomao.container.WheelContainer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class NoamServletContextListener implements ServletContextListener {

    public static final String WHEEL_CONTAINER = "wheel_container";
    public static final String WHEEL_SERVLET = "wheel_servlet";
    public static final String APP_PATH = "appPath";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String appFolder = sce.getServletContext().getInitParameter(APP_PATH);
        WheelContainer wheelContainer = new WheelContainer(appFolder, new Class[]{Controller.class, Service.class});
        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute(WHEEL_CONTAINER, wheelContainer);
        servletContext.addServlet(WHEEL_SERVLET, DispatcherServlet.class).addMapping("/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce = null;
    }
}
