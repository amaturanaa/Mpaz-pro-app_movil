package com.example.producto_service.dto;

import com.example.producto_service.model.Producto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;

    public Producto toModel() {
        return new Producto(id, nombre, precio, stock);
    }

    public static ProductoDTO fromModel(Producto p) {
        if (p == null) return null;
        return new ProductoDTO(p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
    }
}
