package com.example.progreso_service.dto;

import java.math.BigDecimal;

public record AvanceItem(Long asignacionId, Long pruebaId, Long objetivoId, int intentosUsados,
                         int maxIntentos, BigDecimal mejorNota, BigDecimal promedio) {
}