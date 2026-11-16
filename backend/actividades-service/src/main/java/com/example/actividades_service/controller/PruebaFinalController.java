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

import com.example.actividades_service.model.PruebaFinal;
import com.example.actividades_service.service.PruebaFinalService;

@RestController
@RequestMapping("/pruebas")
public class PruebaFinalController {

    private final PruebaFinalService service;

    public PruebaFinalController(PruebaFinalService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PruebaFinal>> listar(@RequestParam(required = false) Long objetivoId) {
        return ResponseEntity.ok(service.listar(objetivoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PruebaFinal> obtener(@PathVariable Long id) {
        return service.buscar(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PruebaFinal> crear(@RequestBody PruebaFinal body) {
        return ResponseEntity.status(201).body(service.guardar(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PruebaFinal> actualizar(@PathVariable Long id, @RequestBody PruebaFinal body) {
        return service.actualizar(id, body).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}