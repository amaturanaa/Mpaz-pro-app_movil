package com.example.actividades_service.dto;

import java.util.List;

import com.example.actividades_service.model.Alternativa;

public record PreguntaRequest(
        Long desafioId,
        Long pruebaId,
        String enunciado,
        Integer orden,
        List<Alternativa> alternativas) {
}