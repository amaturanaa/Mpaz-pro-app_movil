package com.example.actividades_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.actividades_service.model.Alternativa;

@Repository
public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {

    List<Alternativa> findByPreguntaId(Long preguntaId);

    void deleteByPreguntaId(Long preguntaId);
}