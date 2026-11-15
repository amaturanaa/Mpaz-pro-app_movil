package com.example.contenidos_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.contenidos_service.model.Asignatura;
import com.example.contenidos_service.repository.AsignaturaRepository;
import com.example.contenidos_service.repository.UnidadRepository;

@Service
public class AsignaturaService {

	private final AsignaturaRepository asignaturaRepository;
	private final UnidadRepository unidadRepository;

	public AsignaturaService(AsignaturaRepository asignaturaRepository, UnidadRepository unidadRepository) {
		this.asignaturaRepository = asignaturaRepository;
		this.unidadRepository = unidadRepository;
	}

	public List<Asignatura> listar() {
		return asignaturaRepository.findAll();
	}

	public Optional<Asignatura> buscar(Long id) {
		return asignaturaRepository.findById(id);
	}

	public Asignatura guardar(Asignatura asignatura) {
		asignatura.setId(null);
		return asignaturaRepository.save(asignatura);
	}

	public Optional<Asignatura> actualizar(Long id, Asignatura datos) {
		if (!asignaturaRepository.existsById(id)) {
			return Optional.empty();
		}
		datos.setId(id);
		return Optional.of(asignaturaRepository.save(datos));
	}

	public boolean eliminar(Long id) {
		if (!asignaturaRepository.existsById(id)) {
			return false;
		}
		if (unidadRepository.existsByAsignaturaId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"No se puede eliminar: la asignatura tiene unidades");
		}
		asignaturaRepository.deleteById(id);
		return true;
	}
}