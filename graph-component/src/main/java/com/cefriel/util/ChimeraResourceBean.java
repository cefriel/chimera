package com.cefriel.util;

sealed interface TypeAuthConfig
        permits AuthTokenConfigBean, AuthConfigBean {}
// todo check if this can also correctly handle sparql queries used in the constructPhase
public class ChimeraResourceBean {
    private String url;
    private String serializationFormat;
    private TypeAuthConfig authConfig;

    public ChimeraResourceBean() {}

    public ChimeraResourceBean(String url, String serializationFormat, TypeAuthConfig authConfig) {
        this.url = url;
        this.serializationFormat = serializationFormat;
        this.authConfig = authConfig;
    }
    public ChimeraResourceBean(String url, String serializationFormat){
        this.url = url;
        this.serializationFormat = serializationFormat;
        this.authConfig = null;
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

    public TypeAuthConfig getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(TypeAuthConfig authConfig) {
        this.authConfig = authConfig;
    }
}
