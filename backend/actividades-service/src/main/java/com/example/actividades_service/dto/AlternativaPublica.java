package com.example.actividades_service.dto;

import com.example.actividades_service.model.Alternativa;

/** Alternativa sin el campo "correcta", para el estudiante. */
public record AlternativaPublica(Long id, Long preguntaId, String texto) {

    public static AlternativaPublica desde(Alternativa a) {
        return new AlternativaPublica(a.getId(), a.getPreguntaId(), a.getTexto());
    }
}