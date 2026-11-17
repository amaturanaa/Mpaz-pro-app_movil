package com.example.progreso_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.progreso_service.model.Intento;

@Repository
public interface IntentoRepository extends JpaRepository<Intento, Long> {

    List<Intento> findByAsignacionIdAndEstudianteCodigoOrderByNumero(Long asignacionId, String estudianteCodigo);

    long countByAsignacionIdAndEstudianteCodigo(Long asignacionId, String estudianteCodigo);

    List<Intento> findByEstudianteCodigo(String estudianteCodigo);

    List<Intento> findByAsignacionId(Long asignacionId);
}