--liquibase formatted sql

--changeset mpaz:1
CREATE TABLE desafio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    objetivo_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    explicacion TEXT,
    multimedia VARCHAR(255),
    dificultad VARCHAR(20),
    orden INT,
    estado VARCHAR(30) NOT NULL
);

CREATE TABLE prueba_final (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    objetivo_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    limite_intentos INT,
    fecha_habilitacion DATE,
    fecha_rendicion_hasta DATE,
    criterios_puntuacion TEXT,
    solucionario TEXT,
    estado VARCHAR(30) NOT NULL
);

CREATE TABLE pregunta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    desafio_id BIGINT,
    prueba_id BIGINT,
    enunciado VARCHAR(300) NOT NULL,
    orden INT,
    CONSTRAINT fk_pregunta_desafio FOREIGN KEY (desafio_id) REFERENCES desafio(id),
    CONSTRAINT fk_pregunta_prueba FOREIGN KEY (prueba_id) REFERENCES prueba_final(id)
);

CREATE TABLE alternativa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pregunta_id BIGINT NOT NULL,
    texto VARCHAR(200) NOT NULL,
    correcta BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_alternativa_pregunta FOREIGN KEY (pregunta_id) REFERENCES pregunta(id)
);