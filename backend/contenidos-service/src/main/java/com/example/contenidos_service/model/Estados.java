package com.example.contenidos_service.model;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Estados posibles de una unidad u objetivo de aprendizaje
 */
public final class Estados {

    public static final String BORRADOR = "Borrador";
    public static final List<String> VALIDOS = List.of(
            "Borrador", "En revisión", "Autorizado", "Publicado", "Deshabilitado");

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
}