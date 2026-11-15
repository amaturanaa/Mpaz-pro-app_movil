package com.example.compra_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.compra_service.model.Compra;

@Repository
public interface CompraRepository  extends JpaRepository<Compra, Long> {

}
