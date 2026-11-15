package com.example.contenidos_service.model;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Estados posibles de una unidad u objetivo de aprendizaje
 * (los define el caso MPAZ PRO 2.º) y el flujo permitido entre ellos.
 */
public final class Estados {

    public static final String BORRADOR = "Borrador";
    public static final List<String> VALIDOS = List.of(
            "Borrador", "En revisión", "Autorizado", "Publicado", "Deshabilitado");

    // Desde cada estado, a cuáles se puede pasar
    private static final Map<String, List<String>> TRANSICIONES = Map.of(
            "Borrador", List.of("En revisión"),
            "En revisión", List.of("Autorizado", "Borrador"),
            "Autorizado", List.of("Publicado", "En revisión"),
            "Publicado", List.of("Deshabilitado"),
            "Deshabilitado", List.of("Borrador"));

    private Estados() {
    }

    /** Si no viene estado usa "Borrador"; si viene uno inválido responde 400. */
    public static String normalizar(String estado) {
        if (estado == null || estado.isBlank()) {
            return BORRADOR;
        }
        if (!VALIDOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado inválido. Valores permitidos: " + VALIDOS);
        }
        return estado;
    }

    /** Revisa que el cambio de estado esté permitido; si no, responde 400 o 409. */
    public static void validarTransicion(String actual, String nuevo) {
        if (nuevo == null || !VALIDOS.contains(nuevo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado inválido. Valores permitidos: " + VALIDOS);
        }
        List<String> permitidos = TRANSICIONES.getOrDefault(actual, List.of());
        if (!permitidos.contains(nuevo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede pasar de '" + actual + "' a '" + nuevo
                            + "'. Desde '" + actual + "' se puede pasar a: " + permitidos);
        }
    }
}