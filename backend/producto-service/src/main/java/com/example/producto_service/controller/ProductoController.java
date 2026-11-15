package com.example.producto_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.producto_service.dto.ProductoDTO;
import com.example.producto_service.model.Producto;
import com.example.producto_service.service.ProductoService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@PostMapping
	public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO productoDto) {
		Producto nuevo = productoService.guardar(productoDto.toModel());
		return ResponseEntity.ok(ProductoDTO.fromModel(nuevo));
	}

	@GetMapping
	public ResponseEntity<List<ProductoDTO>> listarProductos() {
		List<Producto> productos = productoService.listar();
		List<ProductoDTO> dtos = productos.stream().map(ProductoDTO::fromModel).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/{id}/exists")
	public ResponseEntity<Boolean> existeProducto(@PathVariable Long id) {
		return ResponseEntity.ok(productoService.existePorId(id));
	}

}
