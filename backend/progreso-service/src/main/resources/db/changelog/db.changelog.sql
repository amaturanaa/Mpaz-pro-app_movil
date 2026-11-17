--liquibase formatted sql

--changeset mpaz:1
CREATE TABLE asignacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prueba_id BIGINT NOT NULL,
    objetivo_id BIGINT NOT NULL,
    curso VARCHAR(20) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    max_intentos INT NOT NULL,
    permite_revisar BOOLEAN NOT NULL DEFAULT FALSE,
    avisar_curso BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_asignacion_prueba_curso UNIQUE (prueba_id, curso)
);

CREATE TABLE intento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asignacion_id BIGINT NOT NULL,
    estudiante_codigo VARCHAR(20) NOT NULL,
    numero INT NOT NULL,
    nota DECIMAL(3,1),
    fecha DATETIME NOT NULL,
    CONSTRAINT fk_intento_asignacion FOREIGN KEY (asignacion_id) REFERENCES asignacion(id)
);

CREATE TABLE respuesta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intento_id BIGINT NOT NULL,
    pregunta_id BIGINT NOT NULL,
    alternativa_id BIGINT,
    correcta BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_respuesta_intento FOREIGN KEY (intento_id) REFERENCES intento(id)
);

CREATE TABLE observacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante_codigo VARCHAR(20) NOT NULL,
    objetivo_id BIGINT NOT NULL,
    texto VARCHAR(300) NOT NULL,
    fecha DATE NOT NULL
);