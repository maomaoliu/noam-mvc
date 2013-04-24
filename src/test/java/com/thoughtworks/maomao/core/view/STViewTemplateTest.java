package com.thoughtworks.maomao.core.view;

import com.google.common.base.Charsets;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class STViewTemplateTest {

    private STViewTemplate stViewTemplate;

    @Before
    public void setup(){
        stViewTemplate = new STViewTemplate();
    }

    @Test
    public void should_get_suffix_with_stg(){
        assertEquals(".stg", stViewTemplate.getSuffix());
    }

    @Test
    public void should_render_template(){
        String stString = "My book's name is 'Who am I?'.";
        System.out.println(stString);
        String view = "/com/thoughtworks/maomao/example/view/book/my";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("book_name", "Who am I?");
        String renderString = new String(stViewTemplate.render(view, map), Charsets.UTF_8);
        assertEquals(stString, renderString.trim());
    }
}
