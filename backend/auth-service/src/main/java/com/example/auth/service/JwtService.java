package com.example.auth.service;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final Key key = Keys.hmacShaKeyFor("esta_es_mi_compleja_clave_secreta".getBytes());

    // El token ahora lleva el rol del usuario (UTP, DOCENTE o ESTUDIANTE)
    public String generateToken(String email, String role) {
        Date ahora = new Date();
        Date expiration = new Date(ahora.getTime() + 1000 * 60 * 60);
        return Jwts.builder()
        .subject(email)
        .claim("role", role)
        .issuedAt(ahora)
        .expiration(expiration)
        .signWith(key)
        .compact();
    }

    public String getEmailFromToken(String token) {
        if (token == null || token.isBlank()) return null;
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
        return Jwts.parser()
            .verifyWith((SecretKey) key)
            .build()
            .parseSignedClaims(jwt)
            .getPayload()
            .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isValid(String token) {
        if (token == null || token.isBlank()) return false;
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
            Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(jwt);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}