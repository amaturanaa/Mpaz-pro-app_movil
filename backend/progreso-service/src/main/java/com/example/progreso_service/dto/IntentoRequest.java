package com.example.progreso_service.dto;

import java.util.List;

public record IntentoRequest(String estudianteCodigo, List<RespuestaRequest> respuestas) {
}