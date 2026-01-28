package com.cefriel.util;

public class JdbcConnectionDetails {
    public final String jdbcUrl;
    public final String username;
    public final String password;

    public JdbcConnectionDetails(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }
}