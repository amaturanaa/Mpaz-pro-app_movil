package com.example.admin_service.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.example.admin_service.dto.SolicitudCambioEstado;
import com.example.admin_service.model.HistorialAprobacion;
import com.example.admin_service.repository.HistorialAprobacionRepository;

@Service
public class AprobacionService {

	private static final Pattern SUB = Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");

	private final HistorialAprobacionRepository repository;
	private final RestClient contenidos;

	public AprobacionService(HistorialAprobacionRepository repository,
			@Value("${contenidos.url}") String contenidosUrl) {
		this.repository = repository;
		this.contenidos = RestClient.builder().baseUrl(contenidosUrl).build();
	}

	public HistorialAprobacion cambiarEstado(SolicitudCambioEstado solicitud, String authorization) {
		if (solicitud.getTipo() == null || solicitud.getReferenciaId() == null
				|| solicitud.getEstado() == null || solicitud.getEstado().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Faltan datos: tipo, referenciaId y estado son obligatorios");
		}

		String tipo = solicitud.getTipo().trim().toUpperCase();
		String uri = "/" + rutaDe(tipo) + "/" + solicitud.getReferenciaId();

		try {
			// 1) Estado actual
			Map<?, ?> actual = contenidos.get()
					.uri(uri)
					.retrieve()
					.body(Map.class);
			String estadoAnterior = String.valueOf(actual.get("estado"));

			// 2) Pedir el cambio (contenidos-service valida las reglas)
			contenidos.patch()
					.uri(uri + "/estado")
					.contentType(MediaType.APPLICATION_JSON)
					.body(Map.of("estado", solicitud.getEstado()))
					.retrieve()
					.toBodilessEntity();

			// 3) Guardar en el historial
			HistorialAprobacion h = new HistorialAprobacion();
			h.setTipo(tipo);
			h.setReferenciaId(solicitud.getReferenciaId());
			h.setEstadoAnterior(estadoAnterior);
			h.setEstadoNuevo(solicitud.getEstado());
			h.setResponsableUtp(emailDelToken(authorization));
			h.setObservacion(solicitud.getObservacion());
			return repository.save(h);

		} catch (RestClientResponseException e) {
			// Errores de contenidos-service (404, 409, 400...) se devuelven tal cual
			throw new ResponseStatusException(e.getStatusCode(), e.getResponseBodyAsString());
		} catch (RestClientException e) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
					"No se pudo conectar con contenidos-service");
		}
	}

	public List<HistorialAprobacion> historial(String tipo, Long referenciaId) {
		if (tipo != null && referenciaId != null) {
			return repository.findByTipoAndReferenciaIdOrderByFechaHoraDesc(tipo.trim().toUpperCase(), referenciaId);
		}
		return repository.findAllByOrderByFechaHoraDesc();
	}

	private String rutaDe(String tipo) {
		return switch (tipo) {
			case "UNIDAD" -> "unidades";
			case "OBJETIVO" -> "objetivos";
			default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"tipo debe ser UNIDAD u OBJETIVO");
		};
	}

	// Lee el correo (campo "sub") del payload del JWT. El gateway ya validó el token.
	private String emailDelToken(String authorization) {
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return "desconocido";
		}
		try {
			String[] partes = authorization.substring(7).split("\\.");
			String payload = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
			Matcher m = SUB.matcher(payload);
			return m.find() ? m.group(1) : "desconocido";
		} catch (Exception e) {
			return "desconocido";
		}
	}
}