package com.example.actividades_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Consulta a contenidos-service si un objetivo de aprendizaje existe. */
@Component
public class ObjetivoClient {

    private final RestClient restClient;

    public ObjetivoClient(@Value("${contenidos.url}") String contenidosUrl) {
        this.restClient = RestClient.create(contenidosUrl);
    }

    public boolean existe(Long objetivoId) {
        Boolean respuesta = restClient.get()
                .uri("/objetivos/{id}/exists", objetivoId)
                .retrieve()
                .body(Boolean.class);
        return Boolean.TRUE.equals(respuesta);
    }
}