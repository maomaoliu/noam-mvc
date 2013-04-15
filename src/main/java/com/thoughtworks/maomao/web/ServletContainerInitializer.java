package com.thoughtworks.maomao.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public interface ServletContainerInitializer {
    public void onStartup(Set<Class<?>> c, ServletContext ctx)
            throws ServletException;

}
