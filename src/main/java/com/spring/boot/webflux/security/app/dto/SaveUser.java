package com.spring.boot.webflux.security.app.dto;

import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;

public class SaveUser implements Serializable {

    /*
        NOTA
        test> db.usuarios.createIndex({ "name": 1 }, { unique: true })
        name_1
        test> db.usuarios.createIndex({ "username": 1 }, { unique: true })
        username_1
        test>
        esa es la forma manueal de hacer que ciertos campos sean del tipo unique

     */

    @Size(min = 4)
    @Indexed(unique = true)
    private String name;
    @Indexed(unique = true)
    private String username;
    @Size(min = 8)
    private String password;
    @Size(min = 8)
    private String repeatedPassword;

    public @Size(min = 8) String getPassword() {
        return password;
    }

    public void setPassword(@Size(min = 8) String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public @Size(min = 4) String getName() {
        return name;
    }

    public void setName(@Size(min = 4) String name) {
        this.name = name;
    }

    public @Size(min = 8) String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(@Size(min = 8) String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
}
