package com.example.pago_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pago_service.model.Pago;

@Repository
public interface PagoRepository  extends JpaRepository<Pago, Long> {

}
