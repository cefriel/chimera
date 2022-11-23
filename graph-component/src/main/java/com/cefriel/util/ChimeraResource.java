package com.cefriel.util;

public class ChimeraResource {
    private String url;
    private String serializationFormat;
    private AuthBean authConfig;

    public ChimeraResource() {}

    public ChimeraResource(String url, String serializationFormat, AuthBean authConfig) {
        this.url = url;
        this.serializationFormat = serializationFormat;
        this.authConfig = authConfig;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSerializationFormat() {
        return serializationFormat;
    }

    public void setSerializationFormat(String serializationFormat) {
        this.serializationFormat = serializationFormat;
    }

    public AuthBean getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(AuthBean authConfig) {
        this.authConfig = authConfig;
    }
}
