package com.thoughtworks.maomao.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

@HandlesTypes(WebApplicationInitializer.class)
public class NoamServletContainerInitializer implements ServletContainerInitializer{
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
