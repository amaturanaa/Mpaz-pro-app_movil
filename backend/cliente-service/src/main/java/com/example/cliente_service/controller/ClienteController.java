package com.example.cliente_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.cliente_service.dto.ClienteDTO;
import com.example.cliente_service.model.Cliente;
import com.example.cliente_service.service.ClienteService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody ClienteDTO clienteDto) {
        Cliente nuevoCliente = clienteService.guardar(clienteDto.toModel());
        return ResponseEntity.ok(ClienteDTO.fromModel(nuevoCliente));
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<Cliente> clientes = clienteService.listar();
        List<ClienteDTO> dtos = clientes.stream().map(ClienteDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeCliente(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.existePorId(id));
    }
}