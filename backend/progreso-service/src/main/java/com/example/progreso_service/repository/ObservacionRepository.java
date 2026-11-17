package com.example.progreso_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.progreso_service.model.Observacion;

@Repository
public interface ObservacionRepository extends JpaRepository<Observacion, Long> {

    List<Observacion> findByEstudianteCodigoOrderByFechaDesc(String estudianteCodigo);
}