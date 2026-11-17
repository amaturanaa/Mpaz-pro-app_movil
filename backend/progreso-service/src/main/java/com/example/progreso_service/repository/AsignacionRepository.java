package com.example.progreso_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.progreso_service.model.Asignacion;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    List<Asignacion> findByCurso(String curso);

    boolean existsByPruebaIdAndCurso(Long pruebaId, String curso);
}