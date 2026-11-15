package com.example.contenidos_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.contenidos_service.model.Asignatura;
import com.example.contenidos_service.service.AsignaturaService;

@RestController
@RequestMapping("/asignaturas")
public class AsignaturaController {

	private final AsignaturaService service;

	public AsignaturaController(AsignaturaService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<List<Asignatura>> listar() {
		return ResponseEntity.ok(service.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Asignatura> obtener(@PathVariable Long id) {
		return service.buscar(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Asignatura> crear(@RequestBody Asignatura body) {
		return ResponseEntity.status(201).body(service.guardar(body));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Asignatura> actualizar(@PathVariable Long id, @RequestBody Asignatura body) {
		return service.actualizar(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}