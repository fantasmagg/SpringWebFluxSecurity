package com.spring.boot.webflux.security.app.service.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private String token;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.principal = null;
        this.token = token;
        setAuthenticated(false); // Este constructor se usa antes de la autenticación, por lo que no está autenticado
    }

    public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities, String token) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true); // Este constructor se usa después de la autenticación, por lo que está autenticado
    }

    @Override
    public Object getCredentials() {
        return token; // El token JWT actúa como credencial
    }

    @Override
    public Object getPrincipal() {
        return principal; // Principal es el nombre de usuario o ID del usuario autenticado
    }
}