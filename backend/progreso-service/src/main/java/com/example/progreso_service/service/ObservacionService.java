package com.example.progreso_service.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.progreso_service.model.Observacion;
import com.example.progreso_service.repository.AsignacionRepository;
import com.example.progreso_service.repository.ObservacionRepository;

@Service
public class ObservacionService {

    private final ObservacionRepository observacionRepository;
    private final AsignacionRepository asignacionRepository;

    public ObservacionService(ObservacionRepository observacionRepository,
                              AsignacionRepository asignacionRepository) {
        this.observacionRepository = observacionRepository;
        this.asignacionRepository = asignacionRepository;
    }

    public List<Observacion> listar(String estudianteCodigo) {
        return observacionRepository.findByEstudianteCodigoOrderByFechaDesc(estudianteCodigo);
    }

    public Observacion guardar(Observacion observacion) {
        if (observacion.getEstudianteCodigo() == null || observacion.getEstudianteCodigo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El codigo del estudiante es obligatorio");
        }
        if (observacion.getObjetivoId() == null || !asignacionRepository.existsByObjetivoId(observacion.getObjetivoId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se puede comentar un objetivo que ya fue asignado");
        }
        if (observacion.getTexto() == null || observacion.getTexto().isBlank()
                || observacion.getTexto().length() > 300) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La observacion debe tener entre 1 y 300 caracteres");
        }
        observacion.setId(null);
        observacion.setFecha(LocalDate.now());
        return observacionRepository.save(observacion);
    }
}