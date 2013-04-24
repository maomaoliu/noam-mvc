package com.thoughtworks.maomao.core.view;

import java.util.Map;

public abstract class ViewTemplate {

    public abstract String getSuffix();

    public abstract byte[] render(String view, Map<String, Object> map);

}
