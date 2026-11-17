package com.example.progreso_service.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asignacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pruebaId;
    private Long objetivoId;
    private String curso;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer maxIntentos;
    private Boolean permiteRevisar;
    private Boolean avisarCurso;
}