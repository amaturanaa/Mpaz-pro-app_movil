package com.example.actividades_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.actividades_service.dto.PreguntaRequest;
import com.example.actividades_service.dto.PreguntaResponse;
import com.example.actividades_service.service.PreguntaService;

@RestController
@RequestMapping("/preguntas")
public class PreguntaController {

    private final PreguntaService service;

    public PreguntaController(PreguntaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PreguntaResponse>> listar(@RequestParam(required = false) Long desafioId,
                                                         @RequestParam(required = false) Long pruebaId) {
        return ResponseEntity.ok(service.listar(desafioId, pruebaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreguntaResponse> obtener(@PathVariable Long id) {
        return service.buscar(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PreguntaResponse> crear(@RequestBody PreguntaRequest body) {
        return ResponseEntity.status(201).body(service.guardar(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreguntaResponse> actualizar(@PathVariable Long id, @RequestBody PreguntaRequest body) {
        return service.actualizar(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}