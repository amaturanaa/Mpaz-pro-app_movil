package com.example.actividades_service.util;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Lee el claim "role" del token sin verificar la firma (el gateway ya la verificó). */
public final class RolToken {

    private static final Pattern ROLE = Pattern.compile("\"role\"\\s*:\\s*\"([^\"]+)\"");

    private RolToken() {
    }

    public static String desdeAuthorization(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        try {
            String[] partes = authorization.substring(7).split("\\.");
            if (partes.length < 2) {
                return null;
            }
            String payload = new String(Base64.getUrlDecoder().decode(partes[1]));
            Matcher m = ROLE.matcher(payload);
            return m.find() ? m.group(1) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}