package com.thoughtworks.maomao.core.view;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TemplateFactoryTest {
    @Test
    public void should_get_ST_template(){
        ViewTemplate st = TemplateFactory.getViewTemplate("ST");
        assertEquals(STViewTemplate.class, st.getClass());
    }

    @Test (expected = UnsupportedOperationException.class)
    public void should_throw_exception(){
        ViewTemplate st = TemplateFactory.getViewTemplate("jsp");
    }
}
