package com.cefriel.util;

public final class AuthTokenConfigBean implements TypeAuthConfig {
    private String authToken;

    public AuthTokenConfigBean() {}

    public AuthTokenConfigBean(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
