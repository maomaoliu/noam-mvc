package com.thoughtworks.maomao.core.view;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class STViewTemplate extends ViewTemplate {

    private final String suffix;

    public STViewTemplate(){
        suffix = ".stg";
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public String render(String view, Map<String, Object> map) {
        String outputString = "";
        String viewName = view + suffix;
        try {
            ST st = new ST(getFileString(viewName), '$', '$');
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                st.add(entry.getKey(), entry.getValue());
            }
            outputString = st.render();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputString;
    }

    private String getFileString(String viewName) throws IOException {
        return Files.toString(new File(getClass().getResource(viewName).getFile()), Charsets.UTF_8);
    }
}
