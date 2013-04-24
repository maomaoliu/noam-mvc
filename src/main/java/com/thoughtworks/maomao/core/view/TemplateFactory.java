package com.thoughtworks.maomao.core.view;

public class TemplateFactory {
    public static ViewTemplate getViewTemplate(String template) {
        if(template.equals("ST")){
            return new STViewTemplate();
        } else {
            throw new UnsupportedOperationException("Only support StringTemplate now.");
        }
    }
}
