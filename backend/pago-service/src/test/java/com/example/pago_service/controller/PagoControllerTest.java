package com.example.pago_service.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.pago_service.model.Pago;
import com.example.pago_service.dto.PagoDTO;
import com.example.pago_service.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PagoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PagoService pagoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Pago pago;
    private PagoDTO pagoDto;

    @BeforeEach
    void setUp() {
        // model returned by the service
        pago = new Pago(
            1L,
            2L,
            1000,
            190,
            10,
            1090,
            "Tarjeta",
            new Date()
        );

        // DTO used in requests
        pagoDto = new PagoDTO(
            null,
            2L,
            1000,
            10,
            null,
            null,
            "Tarjeta",
            null
        );
        PagoController controller = new PagoController(pagoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testListarPagos() throws Exception {
        when(pagoService.listar()).thenReturn(List.of(pago));

        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].idCompra").value(2L))
                .andExpect(jsonPath("$[0].medioPago").value("Tarjeta"))
                .andExpect(jsonPath("$[0].valorNeto").value(1000))
                .andExpect(jsonPath("$[0].descuento").value(10))
                .andExpect(jsonPath("$[0].totalPagar").value(1090));
    }

    @Test
    public void testObtenerPago() throws Exception {
        when(pagoService.obtenerPorId((long) 1)).thenReturn(pago);

        mockMvc.perform(get("/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idCompra").value(2L))
                .andExpect(jsonPath("$.medioPago").value("Tarjeta"))
                .andExpect(jsonPath("$.valorNeto").value(1000))
                .andExpect(jsonPath("$.descuento").value(10))
                .andExpect(jsonPath("$.totalPagar").value(1090));
    }

    @Test
    public void testCrearPago() throws Exception {
        when(pagoService.guardar(any(Pago.class))).thenReturn(pago);

        mockMvc.perform(post("/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idCompra").value(2L))
                .andExpect(jsonPath("$.medioPago").value("Tarjeta"))
                .andExpect(jsonPath("$.valorNeto").value(1000))
                .andExpect(jsonPath("$.descuento").value(10))
                .andExpect(jsonPath("$.totalPagar").value(1090));
    }

    @Test
    public void testActualizarPago() throws Exception {
        when(pagoService.actualizar(eq((long)1), any(Pago.class))).thenReturn(pago);

        mockMvc.perform(put("/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idCompra").value(2L))
                .andExpect(jsonPath("$.medioPago").value("Tarjeta"))
                .andExpect(jsonPath("$.valorNeto").value(1000))
                .andExpect(jsonPath("$.descuento").value(10))
                .andExpect(jsonPath("$.totalPagar").value(1090));
    }

    @Test
    public void testEliminarPago() throws Exception {
        doNothing().when(pagoService).eliminar((long) 1);
        mockMvc.perform(delete("/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pago Eliminado Exitosamente"));
        verify(pagoService, times(1)).eliminar((long) 1);
    }
}
