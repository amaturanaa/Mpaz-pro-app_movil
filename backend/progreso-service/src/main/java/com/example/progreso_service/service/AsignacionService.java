package com.example.progreso_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.progreso_service.client.ActividadesClient;
import com.example.progreso_service.client.ObjetivoClient;
import com.example.progreso_service.model.Asignacion;
import com.example.progreso_service.repository.AsignacionRepository;

@Service
public class AsignacionService {

    private final AsignacionRepository repository;
    private final ActividadesClient actividadesClient;
    private final ObjetivoClient objetivoClient;

    public AsignacionService(AsignacionRepository repository, ActividadesClient actividadesClient,
                             ObjetivoClient objetivoClient) {
        this.repository = repository;
        this.actividadesClient = actividadesClient;
        this.objetivoClient = objetivoClient;
    }

    /** Si viene curso filtra por ese curso; si no, devuelve todas. */
    public List<Asignacion> listar(String curso) {
        if (curso != null) {
            return repository.findByCurso(curso);
        }
        return repository.findAll();
    }

    public Optional<Asignacion> buscar(Long id) {
        return repository.findById(id);
    }

    public Asignacion guardar(Asignacion asignacion) {
        validar(asignacion, null);
        asignacion.setId(null);
        return repository.save(asignacion);
    }

    public Optional<Asignacion> actualizar(Long id, Asignacion datos) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        validar(datos, id);
        datos.setId(id);
        return Optional.of(repository.save(datos));
    }

    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private void validar(Asignacion a, Long idActual) {
        if (a.getPruebaId() == null || !actividadesClient.existePrueba(a.getPruebaId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La prueba indicada no existe");
        }
        if (a.getObjetivoId() == null || !objetivoClient.existe(a.getObjetivoId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El objetivo indicado no existe");
        }
        if (a.getCurso() == null || a.getCurso().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El curso es obligatorio");
        }
        boolean yaAsignada = repository.findByCurso(a.getCurso()).stream()
                .anyMatch(x -> x.getPruebaId().equals(a.getPruebaId()) && !Objects.equals(x.getId(), idActual));
        if (yaAsignada) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esta prueba ya fue asignada a este curso");
        }
        if (a.getFechaInicio() == null || a.getFechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las fechas son obligatorias");
        }
        if (a.getFechaFin().isBefore(a.getFechaInicio()) || a.getFechaFin().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de termino no puede ser anterior a la de inicio ni a la de hoy");
        }
        if (a.getMaxIntentos() == null) {
            a.setMaxIntentos(1);
        }
        if (a.getMaxIntentos() < 1 || a.getMaxIntentos() > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El maximo de intentos debe ser entre 1 y 3");
        }
        if (a.getPermiteRevisar() == null) {
            a.setPermiteRevisar(false);
        }
        if (a.getAvisarCurso() == null) {
            a.setAvisarCurso(false);
        }
    }
}