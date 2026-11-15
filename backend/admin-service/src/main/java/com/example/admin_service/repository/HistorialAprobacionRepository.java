package com.example.admin_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.admin_service.model.HistorialAprobacion;

@Repository
public interface HistorialAprobacionRepository extends JpaRepository<HistorialAprobacion, Long> {

	// Todo el historial, del más nuevo al más antiguo
	List<HistorialAprobacion> findAllByOrderByFechaHoraDesc();

	// Historial de una unidad u objetivo en particular
	List<HistorialAprobacion> findByTipoAndReferenciaIdOrderByFechaHoraDesc(String tipo, Long referenciaId);
}