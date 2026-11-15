package com.example.contenidos_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.contenidos_service.model.ContenidoDidactico;
import com.example.contenidos_service.service.ContenidoDidacticoService;

@RestController
@RequestMapping("/contenidos")
public class ContenidoDidacticoController {

	private final ContenidoDidacticoService service;

	public ContenidoDidacticoController(ContenidoDidacticoService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<List<ContenidoDidactico>> listar(@RequestParam(required = false) Long objetivoId) {
		return ResponseEntity.ok(service.listar(objetivoId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ContenidoDidactico> obtener(@PathVariable Long id) {
		return service.buscar(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<ContenidoDidactico> crear(@RequestBody ContenidoDidactico body) {
		return ResponseEntity.status(201).body(service.guardar(body));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ContenidoDidactico> actualizar(@PathVariable Long id, @RequestBody ContenidoDidactico body) {
		return service.actualizar(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}