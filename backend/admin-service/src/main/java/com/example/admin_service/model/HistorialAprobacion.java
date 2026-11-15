package com.example.admin_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historial_aprobacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAprobacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// UNIDAD u OBJETIVO
	private String tipo;
	private Long referenciaId;
	private String estadoAnterior;
	private String estadoNuevo;
	// Correo de la UTP que hizo el cambio (sale del token)
	private String responsableUtp;
	private String observacion;
	private LocalDateTime fechaHora;

	@PrePersist
	void antesDeGuardar() {
		this.fechaHora = LocalDateTime.now();
	}
}