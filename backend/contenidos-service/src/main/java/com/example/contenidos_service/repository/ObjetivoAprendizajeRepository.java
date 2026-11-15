package com.example.contenidos_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.contenidos_service.model.ObjetivoAprendizaje;

@Repository
public interface ObjetivoAprendizajeRepository extends JpaRepository<ObjetivoAprendizaje, Long> {

	List<ObjetivoAprendizaje> findByUnidadId(Long unidadId);

	boolean existsByUnidadId(Long unidadId);
}