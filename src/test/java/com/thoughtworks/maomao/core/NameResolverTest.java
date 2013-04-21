package com.thoughtworks.maomao.core;


import com.thoughtworks.maomao.example.controller.BookController;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NameResolverTest {

    private NameResolver nameResolver;

    @Before
    public void setUp() {
        nameResolver = new NameResolver(BookController.class);
    }

    @Test
    public void should_get_singular() {
        assertEquals("book", nameResolver.getSingular());
    }

    @Test
    public void should_get_plural() {
        assertEquals("books", nameResolver.getPlural());
    }

    @Test
    public void should_get_singular_path() {
        assertEquals("/book", nameResolver.getSingularPath());
    }

    @Test
    public void should_get_plural_path() {
        assertEquals("/books", nameResolver.getPluralPath());
    }

    @Test
    public void should_get_index_view() {
        assertEquals("/com/thoughtworks/maomao/example/view/book/index.stg", nameResolver.getIndexView());
    }

    @Test
    public void should_get_show_view() {
        assertEquals("/com/thoughtworks/maomao/example/view/book/show.stg", nameResolver.getShowView());
    }
}

