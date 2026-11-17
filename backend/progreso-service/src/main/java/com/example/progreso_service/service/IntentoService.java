package com.example.progreso_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.progreso_service.client.ActividadesClient;
import com.example.progreso_service.client.ActividadesClient.AlternativaRemota;
import com.example.progreso_service.client.ActividadesClient.PreguntaRemota;
import com.example.progreso_service.dto.IntentoRequest;
import com.example.progreso_service.dto.RespuestaRequest;
import com.example.progreso_service.model.Asignacion;
import com.example.progreso_service.model.Intento;
import com.example.progreso_service.model.Respuesta;
import com.example.progreso_service.repository.AsignacionRepository;
import com.example.progreso_service.repository.IntentoRepository;
import com.example.progreso_service.repository.RespuestaRepository;

@Service
public class IntentoService {

    private final AsignacionRepository asignacionRepository;
    private final IntentoRepository intentoRepository;
    private final RespuestaRepository respuestaRepository;
    private final ActividadesClient actividadesClient;

    public IntentoService(AsignacionRepository asignacionRepository, IntentoRepository intentoRepository,
                          RespuestaRepository respuestaRepository, ActividadesClient actividadesClient) {
        this.asignacionRepository = asignacionRepository;
        this.intentoRepository = intentoRepository;
        this.respuestaRepository = respuestaRepository;
        this.actividadesClient = actividadesClient;
    }

    /** Recibe las respuestas, las califica con la clave actual de la prueba y guarda el intento. */
    @Transactional
    public Intento registrar(Long asignacionId, IntentoRequest req) {
        Asignacion asignacion = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La asignacion no existe"));

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(asignacion.getFechaInicio()) || hoy.isAfter(asignacion.getFechaFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La prueba no esta disponible en este momento");
        }
        if (req.estudianteCodigo() == null || req.estudianteCodigo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El codigo del estudiante es obligatorio");
        }
        long usados = intentoRepository.countByAsignacionIdAndEstudianteCodigo(asignacionId, req.estudianteCodigo());
        if (usados >= asignacion.getMaxIntentos()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya no tienes intentos disponibles");
        }

        List<PreguntaRemota> preguntas = actividadesClient.preguntasDePrueba(asignacion.getPruebaId());
        if (preguntas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La prueba no tiene preguntas");
        }

        Map<Long, Long> correctaPorPregunta = new HashMap<>();
        Map<Long, List<Long>> alternativasPorPregunta = new HashMap<>();
        for (PreguntaRemota p : preguntas) {
            Long preguntaId = p.pregunta().id();
            List<Long> ids = new java.util.ArrayList<>();
            for (AlternativaRemota a : p.alternativas()) {
                ids.add(a.id());
                if (Boolean.TRUE.equals(a.correcta())) {
                    correctaPorPregunta.put(preguntaId, a.id());
                }
            }
            alternativasPorPregunta.put(preguntaId, ids);
        }

        List<RespuestaRequest> respuestas = req.respuestas() == null ? List.of() : req.respuestas();
        Set<Long> preguntasRespondidas = new HashSet<>();
        for (RespuestaRequest r : respuestas) {
            List<Long> validas = alternativasPorPregunta.get(r.preguntaId());
            if (validas == null || r.alternativaId() == null || !validas.contains(r.alternativaId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Una respuesta no corresponde a la prueba");
            }
            if (!preguntasRespondidas.add(r.preguntaId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Una pregunta se respondio mas de una vez");
            }
        }

        long correctas = respuestas.stream()
                .filter(r -> r.alternativaId().equals(correctaPorPregunta.get(r.preguntaId())))
                .count();
        BigDecimal fraccion = BigDecimal.valueOf(correctas)
                .divide(BigDecimal.valueOf(preguntas.size()), 4, RoundingMode.HALF_UP);
        BigDecimal nota = BigDecimal.ONE.add(fraccion.multiply(BigDecimal.valueOf(6)))
                .setScale(1, RoundingMode.HALF_UP);

        Intento intento = new Intento(null, asignacionId, req.estudianteCodigo(),
                (int) usados + 1, nota, LocalDateTime.now());
        Intento guardado = intentoRepository.save(intento);

        for (RespuestaRequest r : respuestas) {
            boolean correcta = r.alternativaId().equals(correctaPorPregunta.get(r.preguntaId()));
            respuestaRepository.save(new Respuesta(null, guardado.getId(), r.preguntaId(), r.alternativaId(), correcta));
        }
        return guardado;
    }
}