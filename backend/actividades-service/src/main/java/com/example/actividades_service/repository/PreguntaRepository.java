package com.example.actividades_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.actividades_service.model.Pregunta;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByDesafioIdOrderByOrden(Long desafioId);

    List<Pregunta> findByPruebaIdOrderByOrden(Long pruebaId);

    boolean existsByDesafioId(Long desafioId);

    boolean existsByPruebaId(Long pruebaId);
}