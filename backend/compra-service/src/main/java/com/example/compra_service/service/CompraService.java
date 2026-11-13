package com.example.compra_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.compra_service.exception.BadRequestException;
import com.example.compra_service.exception.ResourceNotFoundException;
import com.example.compra_service.model.Compra;
import com.example.compra_service.repository.CompraRepository;

import org.springframework.beans.factory.annotation.Value;

@Service
public class CompraService {
    private final CompraRepository compraRepository;
    private final WebClient webClient;

    @Value("${api.cliente.exists}")
    private String clientePath;
    @Value("${api.producto.exists}")
    private String productoPath;

    public CompraService(CompraRepository compraRepository, WebClient webClient) {
        this.compraRepository = compraRepository;
        this.webClient = webClient;
    }

    public Compra guardar(Compra compra) {
        Boolean existeCliente = webClient.get()
                .uri(String.format(clientePath, compra.getIdCliente()))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        Boolean existeProducto = webClient.get()
                .uri(String.format(productoPath, compra.getIdProducto()))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (existeCliente == null) {
            throw new BadRequestException("No se pudo validar la existencia del cliente");
        }
        if (Boolean.FALSE.equals(existeCliente)) {
            throw new ResourceNotFoundException("Cliente no existe");
        }
        if (existeProducto == null) {
            throw new BadRequestException("No se pudo validar la existencia del producto");
        }
        if (Boolean.FALSE.equals(existeProducto)) {
            throw new ResourceNotFoundException("Producto no existe");
        }
        return compraRepository.save(compra);
    }

    public List<Compra> listar() {
        return compraRepository.findAll();
    }

    public boolean existePorId(Long id) {
        return compraRepository.existsById(id);
    }
}
