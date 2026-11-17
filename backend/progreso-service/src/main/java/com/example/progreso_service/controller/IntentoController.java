package com.example.progreso_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.progreso_service.dto.IntentoRequest;
import com.example.progreso_service.model.Intento;
import com.example.progreso_service.service.IntentoService;

@RestController
@RequestMapping("/asignaciones")
public class IntentoController {

    private final IntentoService service;

    public IntentoController(IntentoService service) {
        this.service = service;
    }

    @PostMapping("/{id}/intentos")
    public ResponseEntity<Intento> rendir(@PathVariable Long id, @RequestBody IntentoRequest body) {
        return ResponseEntity.status(201).body(service.registrar(id, body));
    }
}