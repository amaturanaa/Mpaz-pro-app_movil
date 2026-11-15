package com.example.contenidos_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.contenidos_service.model.Estados;
import com.example.contenidos_service.model.ObjetivoAprendizaje;
import com.example.contenidos_service.repository.ContenidoDidacticoRepository;
import com.example.contenidos_service.repository.ObjetivoAprendizajeRepository;
import com.example.contenidos_service.repository.UnidadRepository;

@Service
public class ObjetivoAprendizajeService {

	private final ObjetivoAprendizajeRepository objetivoRepository;
	private final UnidadRepository unidadRepository;
	private final ContenidoDidacticoRepository contenidoRepository;

	public ObjetivoAprendizajeService(ObjetivoAprendizajeRepository objetivoRepository,
			UnidadRepository unidadRepository, ContenidoDidacticoRepository contenidoRepository) {
		this.objetivoRepository = objetivoRepository;
		this.unidadRepository = unidadRepository;
		this.contenidoRepository = contenidoRepository;
	}

	/** Si viene unidadId filtra por esa unidad; si no, devuelve todos. */
	public List<ObjetivoAprendizaje> listar(Long unidadId) {
		if (unidadId != null) {
			return objetivoRepository.findByUnidadId(unidadId);
		}
		return objetivoRepository.findAll();
	}

	public Optional<ObjetivoAprendizaje> buscar(Long id) {
		return objetivoRepository.findById(id);
	}

	public ObjetivoAprendizaje guardar(ObjetivoAprendizaje objetivo) {
		validar(objetivo);
		objetivo.setId(null);
		return objetivoRepository.save(objetivo);
	}

	public Optional<ObjetivoAprendizaje> actualizar(Long id, ObjetivoAprendizaje datos) {
		if (!objetivoRepository.existsById(id)) {
			return Optional.empty();
		}
		validar(datos);
		datos.setId(id);
		return Optional.of(objetivoRepository.save(datos));
	}

	/** Cambia solo el estado, respetando el flujo permitido (ver Estados). */
	public Optional<ObjetivoAprendizaje> cambiarEstado(Long id, String nuevoEstado) {
		Optional<ObjetivoAprendizaje> existente = objetivoRepository.findById(id);
		if (existente.isEmpty()) {
			return Optional.empty();
		}
		ObjetivoAprendizaje objetivo = existente.get();
		Estados.validarTransicion(objetivo.getEstado(), nuevoEstado);
		objetivo.setEstado(nuevoEstado);
		return Optional.of(objetivoRepository.save(objetivo));
	}

	public boolean eliminar(Long id) {
		if (!objetivoRepository.existsById(id)) {
			return false;
		}
		if (contenidoRepository.existsByObjetivoId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"No se puede eliminar: el objetivo tiene contenidos didácticos");
		}
		objetivoRepository.deleteById(id);
		return true;
	}

	/** Usado por otros servicios para saber si un objetivo existe. */
	public boolean existe(Long id) {
		return objetivoRepository.existsById(id);
	}

	private void validar(ObjetivoAprendizaje objetivo) {
		if (objetivo.getUnidadId() == null || !unidadRepository.existsById(objetivo.getUnidadId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La unidad indicada no existe");
		}
		objetivo.setEstado(Estados.normalizar(objetivo.getEstado()));
	}
}