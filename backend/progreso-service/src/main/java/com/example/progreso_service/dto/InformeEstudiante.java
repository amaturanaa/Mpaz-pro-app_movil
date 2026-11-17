package com.example.progreso_service.dto;

import java.math.BigDecimal;

public record InformeEstudiante(String estudianteCodigo, int intentosUsados, BigDecimal mejorNota,
                                BigDecimal promedio) {
}