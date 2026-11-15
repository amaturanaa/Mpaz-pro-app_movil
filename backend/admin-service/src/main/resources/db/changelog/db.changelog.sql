--liquibase formatted sql

--changeset ale:1
CREATE TABLE historial_aprobacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    referencia_id BIGINT NOT NULL,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    responsable_utp VARCHAR(100),
    observacion VARCHAR(500),
    fecha_hora DATETIME NOT NULL
);