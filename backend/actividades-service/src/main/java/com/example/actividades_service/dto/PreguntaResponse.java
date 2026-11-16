package com.example.actividades_service.dto;

import java.util.List;

import com.example.actividades_service.model.Alternativa;
import com.example.actividades_service.model.Pregunta;

public record PreguntaResponse(Pregunta pregunta, List<Alternativa> alternativas) {
}