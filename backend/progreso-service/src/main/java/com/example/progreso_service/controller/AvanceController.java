package com.example.progreso_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.progreso_service.dto.AvanceItem;
import com.example.progreso_service.service.AvanceService;

@RestController
@RequestMapping("/progreso")
public class AvanceController {

    private final AvanceService service;

    public AvanceController(AvanceService service) {
        this.service = service;
    }

    @GetMapping("/estudiantes/{codigo}/avance")
    public ResponseEntity<List<AvanceItem>> avance(@PathVariable String codigo) {
        return ResponseEntity.ok(service.avanceDeEstudiante(codigo));
    }
}