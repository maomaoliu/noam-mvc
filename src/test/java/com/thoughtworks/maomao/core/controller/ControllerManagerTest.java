package com.thoughtworks.maomao.core.controller;

import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.annotation.Service;
import com.thoughtworks.maomao.container.WheelContainer;
import com.thoughtworks.maomao.example.controller.BookController;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;

public class ControllerManagerTest {

    private ControllerManager controllerManager;

    @Before
    public void setUp() throws Exception {
        WheelContainer wheelContainer = new WheelContainer("com.thoughtworks.maomao.example",
                new Class[] {Controller.class, Service.class});
        controllerManager = new ControllerManager(wheelContainer);
    }

    @Test
    public void should_get_book_controller() throws Exception {
        Object bookController = controllerManager.dispatchController("/book");
        assertEquals(BookController.class, bookController.getClass());
    }

    @Test
    public void should_get_index_method() throws NoSuchMethodException {
        Method method = controllerManager.getMethod(BookController.class, "index");
        assertEquals(BookController.class.getMethod("index"), method);
    }
}
