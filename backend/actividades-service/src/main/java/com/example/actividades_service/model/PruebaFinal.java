package com.example.actividades_service.model;

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
@Table(name = "prueba_final")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PruebaFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long objetivoId;
    private String titulo;
    private Integer limiteIntentos;
    private LocalDate fechaHabilitacion;
    private LocalDate fechaRendicionHasta;
    private String criteriosPuntuacion;
    private String solucionario;
    private String estado;
}