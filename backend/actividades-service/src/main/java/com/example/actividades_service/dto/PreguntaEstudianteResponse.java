package com.example.actividades_service.dto;

import java.util.List;

import com.example.actividades_service.model.Pregunta;

public record PreguntaEstudianteResponse(Pregunta pregunta, List<AlternativaPublica> alternativas) {

    public static PreguntaEstudianteResponse desde(PreguntaResponse r) {
        return new PreguntaEstudianteResponse(r.pregunta(),
                r.alternativas().stream().map(AlternativaPublica::desde).toList());
    }
}