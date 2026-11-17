package com.example.progreso_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record IntentoRevision(Long id, Long asignacionId, Integer numero, BigDecimal nota,
                              LocalDateTime fecha, List<RespuestaRevision> respuestas) {
}