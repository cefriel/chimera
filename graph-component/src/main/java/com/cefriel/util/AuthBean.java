package com.cefriel.util;

public class AuthBean {
    private String userName;
    private String password;
    private String authMethod;

    // todo also allow just having a bearer token

    public AuthBean() {}

    public AuthBean(String userName, String password, String authMethod) {
        this.userName = userName;
        this.password = password;
        this.authMethod = authMethod;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public String toString() {
        return "authMethod=" + this.authMethod + "&authUsername=" + this.userName + "&authPassword=" + this.password + "&authenticationPreemptive=true";
    }
}
