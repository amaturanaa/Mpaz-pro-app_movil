package com.example.progreso_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.progreso_service.dto.AvanceItem;
import com.example.progreso_service.dto.InformeEstudiante;
import com.example.progreso_service.model.Intento;
import com.example.progreso_service.repository.AsignacionRepository;
import com.example.progreso_service.repository.IntentoRepository;

@Service
public class AvanceService {

    private final AsignacionRepository asignacionRepository;
    private final IntentoRepository intentoRepository;

    public AvanceService(AsignacionRepository asignacionRepository, IntentoRepository intentoRepository) {
        this.asignacionRepository = asignacionRepository;
        this.intentoRepository = intentoRepository;
    }

    /** Avance del estudiante en cada asignacion: intentos usados, mejor nota y promedio. */
    public List<AvanceItem> avanceDeEstudiante(String codigo) {
        return asignacionRepository.findAll().stream()
                .map(a -> {
                    List<Intento> intentos = intentoRepository
                            .findByAsignacionIdAndEstudianteCodigoOrderByNumero(a.getId(), codigo);
                    return new AvanceItem(a.getId(), a.getPruebaId(), a.getObjetivoId(), intentos.size(),
                            a.getMaxIntentos(), mejor(intentos), promedio(intentos));
                })
                .toList();
    }

    /** Informe de una asignacion: por cada codigo de estudiante, intentos, mejor nota y promedio. */
    public List<InformeEstudiante> informeDeAsignacion(Long asignacionId) {
        if (!asignacionRepository.existsById(asignacionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La asignacion no existe");
        }
        Map<String, List<Intento>> porEstudiante = intentoRepository.findByAsignacionId(asignacionId).stream()
                .collect(Collectors.groupingBy(Intento::getEstudianteCodigo, TreeMap::new, Collectors.toList()));
        return porEstudiante.entrySet().stream()
                .map(e -> new InformeEstudiante(e.getKey(), e.getValue().size(),
                        mejor(e.getValue()), promedio(e.getValue())))
                .toList();
    }

    private BigDecimal mejor(List<Intento> intentos) {
        return intentos.stream().map(Intento::getNota).max(Comparator.naturalOrder()).orElse(null);
    }

    private BigDecimal promedio(List<Intento> intentos) {
        if (intentos.isEmpty()) {
            return null;
        }
        BigDecimal suma = intentos.stream().map(Intento::getNota).reduce(BigDecimal.ZERO, BigDecimal::add);
        return suma.divide(BigDecimal.valueOf(intentos.size()), 1, RoundingMode.HALF_UP);
    }
}