package com.example.progreso_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.progreso_service.dto.IntentoRevision;
import com.example.progreso_service.service.RevisionService;

@RestController
@RequestMapping("/intentos")
public class RevisionController {

    private final RevisionService service;

    public RevisionController(RevisionService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntentoRevision> revisar(@PathVariable Long id,
                                                   @RequestParam String estudianteCodigo) {
        return ResponseEntity.ok(service.revisar(id, estudianteCodigo));
    }
}