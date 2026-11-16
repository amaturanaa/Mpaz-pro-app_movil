package com.example.actividades_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.actividades_service.model.Desafio;

@Repository
public interface DesafioRepository extends JpaRepository<Desafio, Long> {

    List<Desafio> findByObjetivoIdOrderByOrden(Long objetivoId);
}