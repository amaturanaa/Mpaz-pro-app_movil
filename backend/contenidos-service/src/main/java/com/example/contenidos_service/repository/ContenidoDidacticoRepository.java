package com.example.contenidos_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.contenidos_service.model.ContenidoDidactico;

@Repository
public interface ContenidoDidacticoRepository extends JpaRepository<ContenidoDidactico, Long> {

	List<ContenidoDidactico> findByObjetivoIdOrderByOrden(Long objetivoId);

	boolean existsByObjetivoId(Long objetivoId);
}