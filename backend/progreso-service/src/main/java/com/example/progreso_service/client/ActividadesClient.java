package com.example.progreso_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Consulta a actividades-service si una prueba existe. No envía token. */
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
}