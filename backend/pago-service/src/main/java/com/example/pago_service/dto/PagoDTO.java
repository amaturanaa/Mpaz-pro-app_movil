package com.example.pago_service.dto;

import com.example.pago_service.model.Pago;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {

    private Long id;

    @NotNull(message = "El ID de la compra es obligatorio")
    private Long idCompra;

    @NotNull(message = "El valor neto es obligatorio")
    @Min(value = 100, message = "El valor mínimo es 100")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    private Integer valorNeto;

    @NotNull(message = "El % de descuento es obligatorio")
    @Min(value = 0, message = "El valor mínimo es 0")
    @Max(value = 100, message = "El valor máximo es 100")
    private Integer descuento;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer iva;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer totalPagar;

    @NotBlank(message = "El medio de pago es obligatorio")
    private String medioPago;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date fecha;

    public Pago toModel() {
        return new Pago(
                id,
                idCompra,
                valorNeto,
                iva != null ? iva : 0,
                descuento,
                totalPagar != null ? totalPagar : 0,
                medioPago,
                fecha
        );
    }

    public static PagoDTO fromModel(Pago pago) {
        return new PagoDTO(
                pago.getId(),
                pago.getIdCompra(),
                pago.getValorNeto(),
                pago.getDescuento(),
                pago.getIva(),
                pago.getTotalPagar(),
                pago.getMedioPago(),
                pago.getFecha()
        );
    }
}