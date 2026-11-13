package com.example.contenidos_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.contenidos_service.model.Unidad;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long> {

	List<Unidad> findByAsignaturaIdOrderByOrden(Long asignaturaId);

	boolean existsByAsignaturaId(Long asignaturaId);
}