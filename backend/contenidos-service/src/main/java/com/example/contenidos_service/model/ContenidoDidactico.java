package com.example.contenidos_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contenido_didactico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoDidactico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long objetivoId;
	private String titulo;
	private String explicacionEscrita;
	private String archivoAudio;
	private String imagenes;
	private String ejemplosGuiados;
	private String instrucciones;
	private Integer orden;
	private String responsableOrigen;
}