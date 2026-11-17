package com.example.progreso_service.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Consultas a actividades-service. No envía token. */
@Component
public class ActividadesClient {

    private final RestClient restClient;

    public ActividadesClient(@Value("${actividades.url}") String actividadesUrl) {
        this.restClient = RestClient.create(actividadesUrl);
    }

    public boolean existePrueba(Long pruebaId) {
        return restClient.get()
                .uri("/pruebas/{id}", pruebaId)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> { })
                .toBodilessEntity()
                .getStatusCode()
                .is2xxSuccessful();
    }

    /** Preguntas de una prueba con sus alternativas, incluido el campo "correcta". */
    public List<PreguntaRemota> preguntasDePrueba(Long pruebaId) {
        List<PreguntaRemota> preguntas = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/preguntas").queryParam("pruebaId", pruebaId).build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<PreguntaRemota>>() {});
        return preguntas == null ? List.of() : preguntas;
    }

    public record PreguntaRemota(PreguntaInfo pregunta, List<AlternativaRemota> alternativas) {
    }

    public record PreguntaInfo(Long id) {
    }

    public record AlternativaRemota(Long id, Long preguntaId, Boolean correcta) {
    }
}