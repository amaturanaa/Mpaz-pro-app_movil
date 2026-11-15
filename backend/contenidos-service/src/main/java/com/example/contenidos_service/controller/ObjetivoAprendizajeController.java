package com.example.contenidos_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.contenidos_service.model.ObjetivoAprendizaje;
import com.example.contenidos_service.service.ObjetivoAprendizajeService;

@RestController
@RequestMapping("/objetivos")
public class ObjetivoAprendizajeController {

	private final ObjetivoAprendizajeService service;

	public ObjetivoAprendizajeController(ObjetivoAprendizajeService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<List<ObjetivoAprendizaje>> listar(@RequestParam(required = false) Long unidadId) {
		return ResponseEntity.ok(service.listar(unidadId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ObjetivoAprendizaje> obtener(@PathVariable Long id) {
		return service.buscar(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<ObjetivoAprendizaje> crear(@RequestBody ObjetivoAprendizaje body) {
		return ResponseEntity.status(201).body(service.guardar(body));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ObjetivoAprendizaje> actualizar(@PathVariable Long id, @RequestBody ObjetivoAprendizaje body) {
		return service.actualizar(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// Cambia solo el estado. Body: {"estado": "En revisión"}
	@PatchMapping("/{id}/estado")
	public ResponseEntity<ObjetivoAprendizaje> cambiarEstado(@PathVariable Long id,
			@RequestBody Map<String, String> body) {
		return service.cambiarEstado(id, body.get("estado")).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/exists")
	public ResponseEntity<Boolean> existe(@PathVariable Long id) {
		return ResponseEntity.ok(service.existe(id));
	}
}