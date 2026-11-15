package com.example.admin_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolicitudCambioEstado {

	// "UNIDAD" u "OBJETIVO"
	private String tipo;
	// id de la unidad o del objetivo
	private Long referenciaId;
	// estado al que se quiere pasar (ej: "En revisión")
	private String estado;
	private String observacion;
}