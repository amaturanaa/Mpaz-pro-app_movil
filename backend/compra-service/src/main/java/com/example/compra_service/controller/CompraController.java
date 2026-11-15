package com.example.compra_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.compra_service.dto.CompraDTO;
import com.example.compra_service.model.Compra;
import com.example.compra_service.service.CompraService;

@RestController
@RequestMapping("/compras")
public class CompraController {
    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<CompraDTO> crearCompra(@RequestBody CompraDTO compraDto) {
        Compra nuevaCompra = compraService.guardar(compraDto.toModel());
        return ResponseEntity.ok(CompraDTO.fromModel(nuevaCompra));
    }

    @GetMapping
    public ResponseEntity<List<CompraDTO>> listarCompras() {
        List<Compra> compras = compraService.listar();
        List<CompraDTO> dtos = compras.stream().map(CompraDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeCompra(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.existePorId(id));
    }    
}
