package com.example.progreso_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.progreso_service.model.Observacion;
import com.example.progreso_service.service.ObservacionService;

@RestController
@RequestMapping("/observaciones")
public class ObservacionController {

    private final ObservacionService service;

    public ObservacionController(ObservacionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Observacion>> listar(@RequestParam String estudianteCodigo) {
        return ResponseEntity.ok(service.listar(estudianteCodigo));
    }

    @PostMapping
    public ResponseEntity<Observacion> crear(@RequestBody Observacion body) {
        return ResponseEntity.status(201).body(service.guardar(body));
    }
}