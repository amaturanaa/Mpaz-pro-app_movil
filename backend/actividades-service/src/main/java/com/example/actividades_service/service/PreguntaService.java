package com.example.actividades_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.actividades_service.dto.PreguntaRequest;
import com.example.actividades_service.dto.PreguntaResponse;
import com.example.actividades_service.model.Alternativa;
import com.example.actividades_service.model.Pregunta;
import com.example.actividades_service.repository.AlternativaRepository;
import com.example.actividades_service.repository.DesafioRepository;
import com.example.actividades_service.repository.PreguntaRepository;
import com.example.actividades_service.repository.PruebaFinalRepository;

@Service
public class PreguntaService {

    private static final int ALTERNATIVAS_REQUERIDAS = 4;

    private final PreguntaRepository preguntaRepository;
    private final AlternativaRepository alternativaRepository;
    private final DesafioRepository desafioRepository;
    private final PruebaFinalRepository pruebaRepository;

    public PreguntaService(PreguntaRepository preguntaRepository, AlternativaRepository alternativaRepository,
                           DesafioRepository desafioRepository, PruebaFinalRepository pruebaRepository) {
        this.preguntaRepository = preguntaRepository;
        this.alternativaRepository = alternativaRepository;
        this.desafioRepository = desafioRepository;
        this.pruebaRepository = pruebaRepository;
    }

    /** Filtra por desafio o por prueba si viene uno de los dos; si no, devuelve todas. */
    public List<PreguntaResponse> listar(Long desafioId, Long pruebaId) {
        List<Pregunta> preguntas;
        if (desafioId != null) {
            preguntas = preguntaRepository.findByDesafioIdOrderByOrden(desafioId);
        } else if (pruebaId != null) {
            preguntas = preguntaRepository.findByPruebaIdOrderByOrden(pruebaId);
        } else {
            preguntas = preguntaRepository.findAll();
        }
        return preguntas.stream().map(this::conAlternativas).toList();
    }

    public Optional<PreguntaResponse> buscar(Long id) {
        return preguntaRepository.findById(id).map(this::conAlternativas);
    }

    @Transactional
    public PreguntaResponse guardar(PreguntaRequest req) {
        validar(req);
        Pregunta pregunta = new Pregunta(null, req.desafioId(), req.pruebaId(), req.enunciado(), req.orden());
        Pregunta guardada = preguntaRepository.save(pregunta);
        return guardarAlternativas(guardada, req.alternativas());
    }

    @Transactional
    public Optional<PreguntaResponse> actualizar(Long id, PreguntaRequest req) {
        if (!preguntaRepository.existsById(id)) {
            return Optional.empty();
        }
        validar(req);
        Pregunta pregunta = new Pregunta(id, req.desafioId(), req.pruebaId(), req.enunciado(), req.orden());
        Pregunta guardada = preguntaRepository.save(pregunta);
        alternativaRepository.deleteByPreguntaId(id);
        return Optional.of(guardarAlternativas(guardada, req.alternativas()));
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!preguntaRepository.existsById(id)) {
            return false;
        }
        alternativaRepository.deleteByPreguntaId(id);
        preguntaRepository.deleteById(id);
        return true;
    }

    private PreguntaResponse guardarAlternativas(Pregunta pregunta, List<Alternativa> alternativas) {
        List<Alternativa> guardadas = alternativas.stream().map(a -> {
            a.setId(null);
            a.setPreguntaId(pregunta.getId());
            return alternativaRepository.save(a);
        }).toList();
        return new PreguntaResponse(pregunta, guardadas);
    }

    private PreguntaResponse conAlternativas(Pregunta pregunta) {
        return new PreguntaResponse(pregunta, alternativaRepository.findByPreguntaId(pregunta.getId()));
    }

    private void validar(PreguntaRequest req) {
        boolean enDesafio = req.desafioId() != null;
        boolean enPrueba = req.pruebaId() != null;
        if (enDesafio == enPrueba) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La pregunta debe pertenecer a un desafio o a una prueba (solo a uno de los dos)");
        }
        if (enDesafio && !desafioRepository.existsById(req.desafioId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El desafio indicado no existe");
        }
        if (enPrueba && !pruebaRepository.existsById(req.pruebaId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La prueba indicada no existe");
        }
        if (req.enunciado() == null || req.enunciado().length() < 10 || req.enunciado().length() > 150) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El enunciado debe tener entre 10 y 150 caracteres");
        }
        if (req.alternativas() == null || req.alternativas().size() != ALTERNATIVAS_REQUERIDAS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cada pregunta debe tener exactamente " + ALTERNATIVAS_REQUERIDAS + " alternativas");
        }
        long correctas = 0;
        for (Alternativa a : req.alternativas()) {
            if (a.getTexto() == null || a.getTexto().isBlank() || a.getTexto().length() > 60) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cada alternativa debe tener entre 1 y 60 caracteres");
            }
            if (Boolean.TRUE.equals(a.getCorrecta())) {
                correctas++;
            }
        }
        if (correctas != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe haber exactamente una alternativa correcta");
        }
    }
}