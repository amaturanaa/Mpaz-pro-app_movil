package com.example.contenidos_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.contenidos_service.model.Estados;
import com.example.contenidos_service.model.Unidad;
import com.example.contenidos_service.repository.AsignaturaRepository;
import com.example.contenidos_service.repository.ObjetivoAprendizajeRepository;
import com.example.contenidos_service.repository.UnidadRepository;

@Service
public class UnidadService {

	private final UnidadRepository unidadRepository;
	private final AsignaturaRepository asignaturaRepository;
	private final ObjetivoAprendizajeRepository objetivoRepository;

	public UnidadService(UnidadRepository unidadRepository, AsignaturaRepository asignaturaRepository,
			ObjetivoAprendizajeRepository objetivoRepository) {
		this.unidadRepository = unidadRepository;
		this.asignaturaRepository = asignaturaRepository;
		this.objetivoRepository = objetivoRepository;
	}

	/** Si viene asignaturaId filtra por esa asignatura; si no, devuelve todas. */
	public List<Unidad> listar(Long asignaturaId) {
		if (asignaturaId != null) {
			return unidadRepository.findByAsignaturaIdOrderByOrden(asignaturaId);
		}
		return unidadRepository.findAll();
	}

	public Optional<Unidad> buscar(Long id) {
		return unidadRepository.findById(id);
	}

	public Unidad guardar(Unidad unidad) {
		validar(unidad);
		unidad.setId(null);
		return unidadRepository.save(unidad);
	}

	public Optional<Unidad> actualizar(Long id, Unidad datos) {
		if (!unidadRepository.existsById(id)) {
			return Optional.empty();
		}
		validar(datos);
		datos.setId(id);
		return Optional.of(unidadRepository.save(datos));
	}

	public boolean eliminar(Long id) {
		if (!unidadRepository.existsById(id)) {
			return false;
		}
		if (objetivoRepository.existsByUnidadId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"No se puede eliminar: la unidad tiene objetivos de aprendizaje");
		}
		unidadRepository.deleteById(id);
		return true;
	}

	private void validar(Unidad unidad) {
		if (unidad.getAsignaturaId() == null || !asignaturaRepository.existsById(unidad.getAsignaturaId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La asignatura indicada no existe");
		}
		unidad.setEstado(Estados.normalizar(unidad.getEstado()));
	}
}