package com.cefriel.util;

import java.security.InvalidParameterException;

public class ChimeraResourceBean {
    private String url;
    private String serializationFormat;

    // token authentication
    private String authToken;

    // basic auth
    private String username;
    private String password;
    private String authMethod;

    public ChimeraResourceBean() {}
    public ChimeraResourceBean(String url, String serializationFormat) {
        this.url = url;
        this.serializationFormat = serializationFormat;

        this.authToken = null;
        this.username = null;
        this.password = null;
        this.authMethod = null;
        }

    public ChimeraResourceBean(String url, String serializationFormat, String authToken) {
        this.url = url;
        this.serializationFormat = serializationFormat;
        this.authToken = authToken;
        this.username = null;
        this.password = null;
        this.authMethod = null;
    }

    public ChimeraResourceBean(String url, String serializationFormat, String username, String password, String authMethod)
    {
        this.url = url;
        this.serializationFormat = serializationFormat;
        this.authToken = null;
        this.username = username;
        this.password = password;
        this.authMethod = authMethod;
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
    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    public ChimeraResource specialize() {

        TypeAuthConfig authConfig;

        if (this.authToken != null) {
            authConfig = new AuthTokenConfigBean(this.authToken);
        }
        else if (this.username != null && this.password != null && this.authMethod != null) {
            authConfig = new AuthConfigBean(this.username, this.password, this.authMethod);
        }
        else {
            authConfig = null;
        }

        if (this.getUrl().startsWith(ChimeraResourceConstants.FILE_PREFIX))
            return new ChimeraResource.FileResource(this.getUrl(), this.getSerializationFormat());
        else if (this.getUrl().startsWith(ChimeraResourceConstants.HTTP_PREFIX) || this.getUrl().startsWith(ChimeraResourceConstants.HTTPS_PREFIX))
            return new ChimeraResource.HttpResource(this.getUrl(), this.getSerializationFormat(), authConfig);
        else if (this.getUrl().startsWith(ChimeraResourceConstants.HEADER_PREFIX))
            return new ChimeraResource.HeaderResource(this.getUrl(), this.getSerializationFormat());
        else if (this.getUrl().startsWith(ChimeraResourceConstants.PROPERTY_PREFIX))
            return new ChimeraResource.PropertyResource(this.getUrl(), this.getSerializationFormat());
        else if (this.getUrl().startsWith(ChimeraResourceConstants.CLASSPATH_PREFIX))
            return new ChimeraResource.ClassPathResource(this.getUrl(), this.getSerializationFormat());
        else
            throw new InvalidParameterException("Resource: " + this + " with url " + this.getUrl() + " is not supported.");
    }
}
