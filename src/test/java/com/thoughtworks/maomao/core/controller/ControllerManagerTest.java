package com.thoughtworks.maomao.core.controller;

import com.thoughtworks.maomao.annotation.Controller;
import com.thoughtworks.maomao.annotation.Service;
import com.thoughtworks.maomao.container.WheelContainer;
import com.thoughtworks.maomao.example.controller.BookController;
import org.junit.Before;
import org.junit.Test;

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
    public void testDispatchController() throws Exception {
        Object bookController = controllerManager.dispatchController("/book");
        assertEquals(BookController.class, bookController.getClass());
    }
}
