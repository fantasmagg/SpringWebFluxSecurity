package com.spring.boot.webflux.security.app.persistence.documents;

import com.spring.boot.webflux.security.app.persistence.util.Role;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

public class RegisteredUser implements Serializable {
    private String id;
    private String username;
    private String name;
    private String role;
    private String jwt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
