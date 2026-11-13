package com.example.pago_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de la compra es obligatorio")
    @Column(nullable = false)
    private Long idCompra;

    @NotNull(message = "El valor neto es obligatorio")
    @Min(value = 100, message = "El valor mínimo es 100")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    @Column(nullable = false)
    private int valorNeto;

    @NotNull(message = "El valor del IVA es obligatorio")
    @Min(value = 0, message = "El valor mínimo es 0")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    @Column(nullable = false)
    private int iva;

    @NotNull(message = "El % de descuento es obligatorio")
    @Min(value = 0, message = "El valor mínimo es 0")
    @Max(value = 100, message = "El valor máximo es 100")
    private int descuento;

    @NotNull(message = "El total a pagar es obligatorio")
    @Min(value = 100, message = "El valor mínimo es 100")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    @Column(nullable = false)
    private int totalPagar;

    @NotBlank(message = "El medio de pago es obligatorio")
    @Column(nullable = false, length = 50)
    private String medioPago;

   @Column(nullable = false)
    private Date fecha;
}
