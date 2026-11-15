--liquibase formatted sql

--changeset carlos:1
INSERT INTO cliente (nombre, correo) VALUES
('Ronald Sepulveda', "r.sepulveda@duoc.cl"),
('Carlos Villalobos', "c.villalobos@duoc.cl");
