package com.example.pago_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.model.Pago;
import com.example.pago_service.service.PagoService;

@RestController
@RequestMapping("/pagos")
public class PagoController {
    private final PagoService pagoService;
    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoDTO> crearPago(@Valid @RequestBody PagoDTO pagoDto) {
        try {
            logger.info("POST /pagos - Creando pago: idCompra={}, totalPagar={}", 
                pagoDto.getIdCompra(), pagoDto.getTotalPagar());
            Pago nuevoPago = pagoService.guardar(pagoDto.toModel());
            logger.info("Pago creado exitosamente id={}", nuevoPago.getId());
            return ResponseEntity.ok(PagoDTO.fromModel(nuevoPago));
        } catch (Exception e) {
            logger.error("Error al crear pago: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<PagoDTO>> listarPagos() {
        logger.info("GET /pagos - Listando pagos");
        List<Pago> pagos = pagoService.listar();
        List<PagoDTO> dtos = pagos.stream().map(PagoDTO::fromModel).collect(Collectors.toList());
        logger.info("Total pagos listados: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtenerPago(@PathVariable Long id) {
        logger.info("GET /pagos/{} - Obteniendo pago", id);
        try {
            Pago pago = pagoService.obtenerPorId(id);
            logger.info("Pago obtenido id={}", id);
            return ResponseEntity.ok(PagoDTO.fromModel(pago));
        } catch (Exception e) {
            logger.error("Error al obtener pago id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoDTO> actualizarPago(@PathVariable Long id, @Valid @RequestBody PagoDTO pagoDto) {
        logger.info("PUT /pagos/{} - Actualizando pago", id);
        try {
            Pago actualizado = pagoService.actualizar(id, pagoDto.toModel());
            logger.info("Pago actualizado exitosamente id={}", id);
            return ResponseEntity.ok(PagoDTO.fromModel(actualizado));
        } catch (Exception e) {
            logger.error("Error al actualizar pago id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPago(@PathVariable Long id) {
        logger.info("DELETE /pagos/{} - Eliminando pago", id);
        try {
            pagoService.eliminar(id);
            logger.info("Pago eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Pago Eliminado Exitosamente");
        } catch (Exception e) {
            logger.error("Error al eliminar pago id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}
