package com.thoughtworks.maomao.core;

public class NameResolver {
    private String singular;
    private String plural;
    private final String basePath;

    public NameResolver(Class klazz) {
        String simpleName = klazz.getSimpleName();
        basePath = klazz.getPackage().getName().replace("controller", "view").replace(".", "/");
        singular = simpleName.replace("Controller", "").toLowerCase();
        if (singular.endsWith("s")) {
            plural = singular + "es";
        } else {
            plural = singular + "s";
        }
    }

    public String getSingularPath() {
        return "/" + singular;
    }

    public String getPluralPath() {
        return "/" + plural;
    }

    public String getIndexView() {
        return String.format("/%s/%s/index.stg", basePath, singular);
    }

    public String getShowView() {
        return String.format("/%s/%s/show.stg", basePath, singular);
    }

    public String getSingular() {
        return singular;
    }

    public String getPlural() {
        return plural;
    }

}
