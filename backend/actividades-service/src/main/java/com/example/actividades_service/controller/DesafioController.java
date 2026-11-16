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

import com.example.actividades_service.model.Desafio;
import com.example.actividades_service.service.DesafioService;

@RestController
@RequestMapping("/desafios")
public class DesafioController {

    private final DesafioService service;

    public DesafioController(DesafioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Desafio>> listar(@RequestParam(required = false) Long objetivoId) {
        return ResponseEntity.ok(service.listar(objetivoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Desafio> obtener(@PathVariable Long id) {
        return service.buscar(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Desafio> crear(@RequestBody Desafio body) {
        return ResponseEntity.status(201).body(service.guardar(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Desafio> actualizar(@PathVariable Long id, @RequestBody Desafio body) {
        return service.actualizar(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}