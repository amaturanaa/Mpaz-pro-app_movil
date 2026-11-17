package com.example.progreso_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.progreso_service.dto.IntentoRevision;
import com.example.progreso_service.dto.RespuestaRevision;
import com.example.progreso_service.model.Asignacion;
import com.example.progreso_service.model.Intento;
import com.example.progreso_service.repository.AsignacionRepository;
import com.example.progreso_service.repository.IntentoRepository;
import com.example.progreso_service.repository.RespuestaRepository;

@Service
public class RevisionService {

    private final IntentoRepository intentoRepository;
    private final AsignacionRepository asignacionRepository;
    private final RespuestaRepository respuestaRepository;

    public RevisionService(IntentoRepository intentoRepository, AsignacionRepository asignacionRepository,
                           RespuestaRepository respuestaRepository) {
        this.intentoRepository = intentoRepository;
        this.asignacionRepository = asignacionRepository;
        this.respuestaRepository = respuestaRepository;
    }

    /** Devuelve las respuestas de un intento, solo al dueño y solo si el docente habilito la revision. */
    public IntentoRevision revisar(Long intentoId, String estudianteCodigo) {
        Intento intento = intentoRepository.findById(intentoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El intento no existe"));

        if (estudianteCodigo == null || !estudianteCodigo.equals(intento.getEstudianteCodigo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este intento no pertenece al estudiante");
        }

        Asignacion asignacion = asignacionRepository.findById(intento.getAsignacionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La asignacion no existe"));
        if (!Boolean.TRUE.equals(asignacion.getPermiteRevisar())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El docente no habilito la revision de respuestas");
        }

        List<RespuestaRevision> respuestas = respuestaRepository.findByIntentoId(intentoId).stream()
                .map(r -> new RespuestaRevision(r.getPreguntaId(), r.getAlternativaId(), r.getCorrecta()))
                .toList();

        return new IntentoRevision(intento.getId(), intento.getAsignacionId(), intento.getNumero(),
                intento.getNota(), intento.getFecha(), respuestas);
    }
}