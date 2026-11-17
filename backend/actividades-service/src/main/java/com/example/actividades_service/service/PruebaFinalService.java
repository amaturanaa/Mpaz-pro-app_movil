package com.example.actividades_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.actividades_service.client.ObjetivoClient;
import com.example.actividades_service.model.PruebaFinal;
import com.example.actividades_service.repository.PreguntaRepository;
import com.example.actividades_service.repository.PruebaFinalRepository;

@Service
public class PruebaFinalService {

    private final PruebaFinalRepository pruebaRepository;
    private final PreguntaRepository preguntaRepository;
    private final ObjetivoClient objetivoClient;

    public PruebaFinalService(PruebaFinalRepository pruebaRepository, PreguntaRepository preguntaRepository,
                              ObjetivoClient objetivoClient) {
        this.pruebaRepository = pruebaRepository;
        this.preguntaRepository = preguntaRepository;
        this.objetivoClient = objetivoClient;
    }

    /** Si viene objetivoId filtra por ese objetivo; si no, devuelve todas. */
    public List<PruebaFinal> listar(Long objetivoId) {
        if (objetivoId != null) {
            return pruebaRepository.findByObjetivoId(objetivoId);
        }
        return pruebaRepository.findAll();
    }

    public Optional<PruebaFinal> buscar(Long id) {
        return pruebaRepository.findById(id);
    }

    public PruebaFinal guardar(PruebaFinal prueba) {
        validar(prueba);
        prueba.setId(null);
        return pruebaRepository.save(prueba);
    }

    public Optional<PruebaFinal> actualizar(Long id, PruebaFinal datos) {
        if (!pruebaRepository.existsById(id)) {
            return Optional.empty();
        }
        validar(datos);
        datos.setId(id);
        return Optional.of(pruebaRepository.save(datos));
    }

    public boolean eliminar(Long id) {
        if (!pruebaRepository.existsById(id)) {
            return false;
        }
        if (preguntaRepository.existsByPruebaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: la prueba tiene preguntas");
        }
        pruebaRepository.deleteById(id);
        return true;
    }

    private void validar(PruebaFinal prueba) {
        if (prueba.getObjetivoId() == null || !objetivoClient.existe(prueba.getObjetivoId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El objetivo indicado no existe");
        }
        if (prueba.getTitulo() == null || prueba.getTitulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El titulo es obligatorio");
        }
        if (prueba.getLimiteIntentos() == null) {
            prueba.setLimiteIntentos(1);
        }
        if (prueba.getLimiteIntentos() < 1 || prueba.getLimiteIntentos() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El limite de intentos debe ser entre 1 y 3");
        }
        if (prueba.getFechaHabilitacion() != null && prueba.getFechaRendicionHasta() != null
                && prueba.getFechaRendicionHasta().isBefore(prueba.getFechaHabilitacion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de termino no puede ser anterior a la de habilitacion");
        }
        if (prueba.getEstado() == null || prueba.getEstado().isBlank()) {
            prueba.setEstado("Borrador");
        }
    }
}