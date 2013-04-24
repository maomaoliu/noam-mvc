package com.thoughtworks.maomao.core.util;


import com.thoughtworks.maomao.core.util.NameResolver;
import com.thoughtworks.maomao.example.controller.BookController;
import com.thoughtworks.maomao.example.model.Book;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NameResolverTest {

    private NameResolver nameResolver;
    private String appPath;

    @Before
    public void setUp() {
        appPath = "com.thoughtworks.maomao.example";
        nameResolver = new NameResolver(appPath);
    }

    @Test
    public void should_get_instance_name_from_class() {
        String instanceName = NameResolver.getInstanceName(Book.class);
        assertEquals("book", instanceName);
    }

    @Test
    public void should_get_instance_name_when_class_is_a_Controller() {
        String instanceName = NameResolver.getInstanceName(BookController.class);
        assertEquals("bookController", instanceName);
    }

    @Test
    public void should_get_instance_name_from_a_class_name() {
        String instanceName = NameResolver.getInstanceName("BookController");
        assertEquals("bookController", instanceName);
    }

    @Test
    public void should_get_post_name() {
        String instanceName = NameResolver.toPostName("create");
        assertEquals("createPost", instanceName);
    }

    @Test
    public void should_get_singular_path() {
        assertEquals("/book", NameResolver.getSingularPath(BookController.class));
    }

    @Test
    public void should_get_view() {
        String indexView = nameResolver.getView(new BookController(), "index");
        assertEquals("/com/thoughtworks/maomao/example/view/book/index", indexView);
    }
}

