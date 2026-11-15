package com.example.contenidos_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.contenidos_service.model.Unidad;
import com.example.contenidos_service.service.UnidadService;

@RestController
@RequestMapping("/unidades")
public class UnidadController {

	private final UnidadService service;

	public UnidadController(UnidadService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<List<Unidad>> listar(@RequestParam(required = false) Long asignaturaId) {
		return ResponseEntity.ok(service.listar(asignaturaId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Unidad> obtener(@PathVariable Long id) {
		return service.buscar(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Unidad> crear(@RequestBody Unidad body) {
		return ResponseEntity.status(201).body(service.guardar(body));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Unidad> actualizar(@PathVariable Long id, @RequestBody Unidad body) {
		return service.actualizar(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Cambia solo el estado. Body: {"estado": "En revisión"}
	@PatchMapping("/{id}/estado")
	public ResponseEntity<Unidad> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
		return service.cambiarEstado(id, body.get("estado")).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}