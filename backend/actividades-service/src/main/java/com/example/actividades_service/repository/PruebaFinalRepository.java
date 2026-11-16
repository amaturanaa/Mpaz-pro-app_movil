package com.example.actividades_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.actividades_service.model.PruebaFinal;

@Repository
public interface PruebaFinalRepository extends JpaRepository<PruebaFinal, Long> {

    List<PruebaFinal> findByObjetivoId(Long objetivoId);
}