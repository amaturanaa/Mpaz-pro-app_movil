package com.example.contenidos_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.contenidos_service.model.ContenidoDidactico;
import com.example.contenidos_service.repository.ContenidoDidacticoRepository;
import com.example.contenidos_service.repository.ObjetivoAprendizajeRepository;

@Service
public class ContenidoDidacticoService {

	private final ContenidoDidacticoRepository contenidoRepository;
	private final ObjetivoAprendizajeRepository objetivoRepository;

	public ContenidoDidacticoService(ContenidoDidacticoRepository contenidoRepository,
			ObjetivoAprendizajeRepository objetivoRepository) {
		this.contenidoRepository = contenidoRepository;
		this.objetivoRepository = objetivoRepository;
	}

	/** Si viene objetivoId filtra por ese objetivo; si no, devuelve todos. */
	public List<ContenidoDidactico> listar(Long objetivoId) {
		if (objetivoId != null) {
			return contenidoRepository.findByObjetivoIdOrderByOrden(objetivoId);
		}
		return contenidoRepository.findAll();
	}

	public Optional<ContenidoDidactico> buscar(Long id) {
		return contenidoRepository.findById(id);
	}

	public ContenidoDidactico guardar(ContenidoDidactico contenido) {
		validar(contenido);
		contenido.setId(null);
		return contenidoRepository.save(contenido);
	}

	public Optional<ContenidoDidactico> actualizar(Long id, ContenidoDidactico datos) {
		if (!contenidoRepository.existsById(id)) {
			return Optional.empty();
		}
		validar(datos);
		datos.setId(id);
		return Optional.of(contenidoRepository.save(datos));
	}

	public boolean eliminar(Long id) {
		if (!contenidoRepository.existsById(id)) {
			return false;
		}
		contenidoRepository.deleteById(id);
		return true;
	}

	private void validar(ContenidoDidactico contenido) {
		if (contenido.getObjetivoId() == null || !objetivoRepository.existsById(contenido.getObjetivoId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El objetivo de aprendizaje indicado no existe");
		}
	}
}