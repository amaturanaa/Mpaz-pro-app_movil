package com.example.producto_service.service;

import org.springframework.stereotype.Service;
import com.example.producto_service.model.Producto;
import com.example.producto_service.repository.ProductoRepository;
import java.util.List;

@Service
public class ProductoService {

	private final ProductoRepository productoRepository;

	public ProductoService(ProductoRepository productoRepository) {
		this.productoRepository = productoRepository;
	}

	public Producto guardar(Producto producto) {
		return productoRepository.save(producto);
	}

	public List<Producto> listar() {
		return productoRepository.findAll();
	}

	public boolean existePorId(Long id) {
		return productoRepository.existsById(id);
	}

}
