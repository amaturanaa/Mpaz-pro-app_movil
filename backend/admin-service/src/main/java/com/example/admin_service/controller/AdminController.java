package com.example.admin_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.admin_service.dto.SolicitudCambioEstado;
import com.example.admin_service.model.HistorialAprobacion;
import com.example.admin_service.service.AprobacionService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private final AprobacionService service;

	public AdminController(AprobacionService service) {
		this.service = service;
	}

	@PostMapping("/aprobaciones")
	@ResponseStatus(HttpStatus.CREATED)
	public HistorialAprobacion cambiarEstado(
			@RequestBody SolicitudCambioEstado solicitud,
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		return service.cambiarEstado(solicitud, authorization);
	}

	@GetMapping("/historial")
	public List<HistorialAprobacion> historial(
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false) Long referenciaId) {
		return service.historial(tipo, referenciaId);
	}
}