package com.spring.boot.webflux.security.app.persistence.documents;

import com.spring.boot.webflux.security.app.persistence.util.Role;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Document(collection = "usuarios")
public class Users implements UserDetails {
    /*
       NOTA
       test> db.usuarios.createIndex({ "name": 1 }, { unique: true })
       name_1
       test> db.usuarios.createIndex({ "username": 1 }, { unique: true })
       username_1
       test>
       esa es la forma manueal de hacer que ciertos campos sean del tipo unique

    */
    @Id
    private String id;

    @NotEmpty
    @Indexed(unique = true)
    private String name;
    @Indexed(unique = true)
    private String username;
    private String password;
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if(role==null) return null;
        if(role.getPermissions()==null)return null;

        return role.getPermissions().stream()
                .map(each-> each.name())
                .map(ex -> new SimpleGrantedAuthority(ex))
//                .map(x->{
//                    String each = x.name();
//                    return new SimpleGrantedAuthority(each);
//                })
                .collect(Collectors.toList());


    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotEmpty String getName() {
        return name;
    }

    public void setName(@NotEmpty String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
