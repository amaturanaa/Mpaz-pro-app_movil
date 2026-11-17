package com.example.progreso_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.progreso_service.dto.InformeEstudiante;
import com.example.progreso_service.service.AvanceService;

@RestController
@RequestMapping("/informes")
public class InformeController {

    private final AvanceService service;

    public InformeController(AvanceService service) {
        this.service = service;
    }

    @GetMapping("/asignaciones/{id}")
    public ResponseEntity<List<InformeEstudiante>> informe(@PathVariable Long id) {
        return ResponseEntity.ok(service.informeDeAsignacion(id));
    }
}