package com.example.pago_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.pago_service.exception.BadRequestException;
import com.example.pago_service.exception.ResourceNotFoundException;
import com.example.pago_service.model.Pago;
import com.example.pago_service.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Value;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);

    @Value("${api.compra.exists}")
    private String compraPath;

    public PagoService(PagoRepository pagoRepository,WebClient webClient) {
        this.pagoRepository = pagoRepository;
        this.webClient = webClient;
    }

    public Pago guardar(Pago pago) {
        logger.info("Iniciando guardar pago con idCompra={}, Neto={}, Dcto{}, MedioPago={}", 
            pago.getIdCompra(), pago.getValorNeto(), pago.getDescuento(),pago.getMedioPago());
            pago.setIva(calcularIVA(calcularSubtotal(pago.getValorNeto(),pago.getDescuento())));
            pago.setTotalPagar(calcularSubtotal(pago.getValorNeto(),pago.getDescuento())+pago.getIva());
        try {
            if (pago.getTotalPagar() <=0) throw new IllegalArgumentException("totalPagar requerido");
            if (pago.getMedioPago() == null || pago.getMedioPago().isBlank()) throw new IllegalArgumentException("medioPago requerido");
            if (pago.getFecha() == null) pago.setFecha(new Date());

            logger.info("ID COMPRA = {}", pago.getIdCompra());

            String uri = String.format(compraPath, pago.getIdCompra());
            logger.info("URI = {}", uri);

            logger.info("Realizando petición a api-gateway: {}", uri);
            Boolean existeCompra = webClient.get()
                    .uri(String.format(compraPath, pago.getIdCompra()))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            logger.info("Respuesta de api-gateway: existeCompra={}", existeCompra);

            if (existeCompra == null) {
                logger.error("No se pudo validar la existencia de la compra");
                throw new BadRequestException("No se pudo validar la existencia de la compra");
            }
            if (Boolean.FALSE.equals(existeCompra)) {
                logger.warn("Compra no existe con id={}", pago.getIdCompra());
                throw new ResourceNotFoundException("Compra no existe");
            }
            
            Pago pagoGuardado = pagoRepository.save(pago);
            logger.info("Pago guardado exitosamente con id={}", pagoGuardado.getId());
            return pagoGuardado;
        } catch (Exception e) {
            logger.error("Error al guardar pago: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Pago> listar() {
        logger.info("Listando todos los pagos");
        List<Pago> pagos = pagoRepository.findAll();
        logger.info("Total pagos encontrados: {}", pagos.size());
        return pagos;
    }

    public Pago obtenerPorId(Long id) {
        logger.info("Buscando pago por id={}", id);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pago no encontrado id={}", id);
                    return new ResourceNotFoundException("Pago no existe");
                });
        logger.info("Pago encontrado id={}", id);
        return pago;
    }

    public Pago actualizar(Long id, Pago pago) {
        logger.info("Iniciando actualizar pago id={}", id);
        try {
            Pago existente = pagoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pago no existe"));

            if (pago.getValorNeto() <= 0) throw new IllegalArgumentException("Valor Neto requerido");
            if (pago.getMedioPago() == null || pago.getMedioPago().isBlank()) throw new IllegalArgumentException("Medio Pago Requerido");
            if (pago.getFecha() == null) pago.setFecha(new Date());

            logger.info("Validando existencia de compra para pago idCompra={}", pago.getIdCompra());
            Boolean existeCompra = webClient.get()
                    .uri(String.format(compraPath, pago.getIdCompra()))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            logger.info("Respuesta de validación compra: {}", existeCompra);

            if (existeCompra == null) {
                logger.error("No se pudo validar la existencia de la compra");
                throw new BadRequestException("No se pudo validar la existencia de la compra");
            }
            if (Boolean.FALSE.equals(existeCompra)) {
                logger.warn("Compra no existe con id={}", pago.getIdCompra());
                throw new ResourceNotFoundException("Compra no existe");
            }

            existente.setIdCompra(pago.getIdCompra());
            // Calcular IVA y totalPagar usando las mismas funciones que en guardar
            int valorNeto = pago.getValorNeto();
            int descuento = pago.getDescuento();
            int subtotal = calcularSubtotal(valorNeto, descuento);
            int iva = calcularIVA(subtotal);
            int totalPagar = subtotal + iva;

            existente.setValorNeto(valorNeto);
            existente.setIva(iva);
            existente.setDescuento(descuento);
            existente.setTotalPagar(totalPagar);
            existente.setMedioPago(pago.getMedioPago());
            existente.setFecha(pago.getFecha());

            Pago actualizado = pagoRepository.save(existente);
            logger.info("Pago actualizado exitosamente id={}", actualizado.getId());
            return actualizado;
        } catch (Exception e) {
            logger.error("Error al actualizar pago id={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void eliminar(Long id) {
        logger.info("Iniciando eliminación de pago id={}", id);
        try {
            if (!pagoRepository.existsById(id)) {
                logger.warn("Pago no existe para eliminar id={}", id);
                throw new ResourceNotFoundException("Pago no existe");
            }
            pagoRepository.deleteById(id);
            logger.info("Pago eliminado exitosamente id={}", id);
        } catch (Exception e) {
            logger.error("Error al eliminar pago id={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public int calcularSubtotal(int neto, int porcentajeDescuento) {
        if (neto < 0) {
            throw new IllegalArgumentException("El valor neto no puede ser negativo");
        }
        if (porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
        }
        int subtotal = neto - (neto * porcentajeDescuento / 100);
        return subtotal;
    }

    public int calcularIVA(int subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo");
        }
        int iva = subtotal * 19 / 100;
        return iva;
    }
}
