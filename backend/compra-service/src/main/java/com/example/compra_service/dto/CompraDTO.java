package com.example.compra_service.dto;

import com.example.compra_service.model.Compra;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraDTO {
    private Long id;
    private Long idCliente;
    private Long idProducto;
    private Integer cantidad;

    public Compra toModel() {
        return new Compra(id, idCliente, idProducto, cantidad);
    }

    public static CompraDTO fromModel(Compra c) {
        if (c == null) return null;
        return new CompraDTO(c.getId(), c.getIdCliente(), c.getIdProducto(), c.getCantidad());
    }
}
