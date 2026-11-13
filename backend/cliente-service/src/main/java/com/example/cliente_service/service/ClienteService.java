package com.example.cliente_service.service;

import org.springframework.stereotype.Service;

import com.example.cliente_service.model.Cliente;
import com.example.cliente_service.repository.ClienteRepository;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public boolean existePorId(Long id) {
        return clienteRepository.existsById(id);
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }
}