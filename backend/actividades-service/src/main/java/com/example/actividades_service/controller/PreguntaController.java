package com.example.actividades_service.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.actividades_service.dto.PreguntaEstudianteResponse;
import com.example.actividades_service.dto.PreguntaRequest;
import com.example.actividades_service.dto.PreguntaResponse;
import com.example.actividades_service.service.PreguntaService;
import com.example.actividades_service.util.RolToken;

@RestController
@RequestMapping("/preguntas")
public class PreguntaController {

    private final PreguntaService service;

    public PreguntaController(PreguntaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<?>> listar(@RequestParam(required = false) Long desafioId,
                                          @RequestParam(required = false) Long pruebaId,
                                          @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        List<PreguntaResponse> preguntas = service.listar(desafioId, pruebaId);
        if (esEstudiante(authorization)) {
            return ResponseEntity.ok(preguntas.stream().map(PreguntaEstudianteResponse::desde).toList());
        }
        return ResponseEntity.ok(preguntas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id,
                                     @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return service.buscar(id).map(r -> ResponseEntity.ok(esEstudiante(authorization)
                        ? PreguntaEstudianteResponse.desde(r) : r))
                .orElse(ResponseEntity.notFound().build());
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

    private boolean esEstudiante(String authorization) {
        return "ESTUDIANTE".equals(RolToken.desdeAuthorization(authorization));
    }
}