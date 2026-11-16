package com.example.actividades_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.actividades_service.client.ObjetivoClient;
import com.example.actividades_service.model.Desafio;
import com.example.actividades_service.repository.DesafioRepository;
import com.example.actividades_service.repository.PreguntaRepository;

@Service
public class DesafioService {

    private static final List<String> TIPOS = List.of("ASOCIACION", "SECUENCIA", "COMPLETACION", "SELECCION");

    private final DesafioRepository desafioRepository;
    private final PreguntaRepository preguntaRepository;
    private final ObjetivoClient objetivoClient;

    public DesafioService(DesafioRepository desafioRepository, PreguntaRepository preguntaRepository,
                          ObjetivoClient objetivoClient) {
        this.desafioRepository = desafioRepository;
        this.preguntaRepository = preguntaRepository;
        this.objetivoClient = objetivoClient;
    }

    /** Si viene objetivoId filtra por ese objetivo; si no, devuelve todos. */
    public List<Desafio> listar(Long objetivoId) {
        if (objetivoId != null) {
            return desafioRepository.findByObjetivoIdOrderByOrden(objetivoId);
        }
        return desafioRepository.findAll();
    }

    public Optional<Desafio> buscar(Long id) {
        return desafioRepository.findById(id);
    }

    public Desafio guardar(Desafio desafio) {
        validar(desafio);
        desafio.setId(null);
        return desafioRepository.save(desafio);
    }

    public Optional<Desafio> actualizar(Long id, Desafio datos) {
        if (!desafioRepository.existsById(id)) {
            return Optional.empty();
        }
        validar(datos);
        datos.setId(id);
        return Optional.of(desafioRepository.save(datos));
    }

    public boolean eliminar(Long id) {
        if (!desafioRepository.existsById(id)) {
            return false;
        }
        if (preguntaRepository.existsByDesafioId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: el desafio tiene preguntas");
        }
        desafioRepository.deleteById(id);
        return true;
    }

    private void validar(Desafio desafio) {
        if (desafio.getObjetivoId() == null || !objetivoClient.existe(desafio.getObjetivoId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El objetivo indicado no existe");
        }
        if (desafio.getTitulo() == null || desafio.getTitulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El titulo es obligatorio");
        }
        if (desafio.getTipo() == null || !TIPOS.contains(desafio.getTipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo invalido. Valores permitidos: " + TIPOS);
        }
        if (desafio.getEstado() == null || desafio.getEstado().isBlank()) {
            desafio.setEstado("Borrador");
        }
    }
}