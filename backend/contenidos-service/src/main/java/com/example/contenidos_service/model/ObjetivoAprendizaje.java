package com.example.contenidos_service.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "objetivo_aprendizaje")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjetivoAprendizaje {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long unidadId;
	private String codigo;
	private String descripcion;
	private String ejeCurricular;
	private String textoReferencia;
	private String estado;
	private String responsableUtp;
	private LocalDate fechaActualizacion;

	@PrePersist
	@PreUpdate
	void antesDeGuardar() {
		this.fechaActualizacion = LocalDate.now();
	}
}