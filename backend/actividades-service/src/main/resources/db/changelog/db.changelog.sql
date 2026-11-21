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


--changeset mpaz:2
-- Pruebas de ejemplo FICTICIAS: 3 preguntas por unidad (objetivos 1 a 4 de contenidos-service)

INSERT INTO prueba_final (objetivo_id, titulo, limite_intentos, estado)
VALUES (1, 'Prueba final: Unidad 1: Leemos y comprendemos cuentos', 3, 'Publicado');

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Qué parte de un cuento nos cuenta cómo comienza la historia?', 1 FROM prueba_final WHERE objetivo_id = 1;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT 'El final' AS texto, FALSE AS correcta
    UNION ALL SELECT 'El inicio' AS texto, TRUE AS correcta
    UNION ALL SELECT 'El índice' AS texto, FALSE AS correcta
) t WHERE p.enunciado = '¿Qué parte de un cuento nos cuenta cómo comienza la historia?';

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Quién cuenta lo que pasa en un cuento?', 2 FROM prueba_final WHERE objetivo_id = 1;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT 'El narrador' AS texto, TRUE AS correcta
    UNION ALL SELECT 'El lector' AS texto, FALSE AS correcta
    UNION ALL SELECT 'La portada' AS texto, FALSE AS correcta
) t WHERE p.enunciado = '¿Quién cuenta lo que pasa en un cuento?';

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Qué pasa al final de un cuento?', 3 FROM prueba_final WHERE objetivo_id = 1;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT 'Se presentan los personajes' AS texto, FALSE AS correcta
    UNION ALL SELECT 'Empieza la historia' AS texto, FALSE AS correcta
    UNION ALL SELECT 'Se resuelve la historia' AS texto, TRUE AS correcta
) t WHERE p.enunciado = '¿Qué pasa al final de un cuento?';

INSERT INTO prueba_final (objetivo_id, titulo, limite_intentos, estado)
VALUES (2, 'Prueba final: Unidad 1: Números hasta el 100', 3, 'Publicado');

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Qué número viene después del 49?', 1 FROM prueba_final WHERE objetivo_id = 2;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT '48' AS texto, FALSE AS correcta
    UNION ALL SELECT '59' AS texto, FALSE AS correcta
    UNION ALL SELECT '50' AS texto, TRUE AS correcta
) t WHERE p.enunciado = '¿Qué número viene después del 49?';

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, 'Contamos de 10 en 10: 10, 20, 30... ¿Qué número sigue?', 2 FROM prueba_final WHERE objetivo_id = 2;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT '40' AS texto, TRUE AS correcta
    UNION ALL SELECT '31' AS texto, FALSE AS correcta
    UNION ALL SELECT '50' AS texto, FALSE AS correcta
) t WHERE p.enunciado = 'Contamos de 10 en 10: 10, 20, 30... ¿Qué número sigue?';

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Cuál de estos números es el mayor?', 3 FROM prueba_final WHERE objetivo_id = 2;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT '78' AS texto, FALSE AS correcta
    UNION ALL SELECT '87' AS texto, TRUE AS correcta
    UNION ALL SELECT '69' AS texto, FALSE AS correcta
) t WHERE p.enunciado = '¿Cuál de estos números es el mayor?';

INSERT INTO prueba_final (objetivo_id, titulo, limite_intentos, estado)
VALUES (3, 'Prueba final: Unidad 1: Los seres vivos y su entorno', 3, 'Publicado');

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Cuál de estos es un ser vivo?', 1 FROM prueba_final WHERE objetivo_id = 3;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT 'Una piedra' AS texto, FALSE AS correcta
    UNION ALL SELECT 'Una silla' AS texto, FALSE AS correcta
    UNION ALL SELECT 'Un perro' AS texto, TRUE AS correcta
) t WHERE p.enunciado = '¿Cuál de estos es un ser vivo?';

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Dónde vive un pez?', 2 FROM prueba_final WHERE objetivo_id = 3;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT 'En el agua' AS texto, TRUE AS correcta
    UNION ALL SELECT 'En el desierto' AS texto, FALSE AS correcta
    UNION ALL SELECT 'En un árbol' AS texto, FALSE AS correcta
) t WHERE p.enunciado = '¿Dónde vive un pez?';

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Qué necesitan las plantas para crecer?', 3 FROM prueba_final WHERE objetivo_id = 3;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT 'Solo oscuridad' AS texto, FALSE AS correcta
    UNION ALL SELECT 'Agua y luz' AS texto, TRUE AS correcta
    UNION ALL SELECT 'Solo plástico' AS texto, FALSE AS correcta
) t WHERE p.enunciado = '¿Qué necesitan las plantas para crecer?';

INSERT INTO prueba_final (objetivo_id, titulo, limite_intentos, estado)
VALUES (4, 'Prueba final: Unidad 1: Mi familia y mi comunidad', 3, 'Publicado');

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Qué hacen las personas de una familia?', 1 FROM prueba_final WHERE objetivo_id = 4;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT 'Se cuidan entre sí' AS texto, TRUE AS correcta
    UNION ALL SELECT 'Nunca se hablan' AS texto, FALSE AS correcta
    UNION ALL SELECT 'Viven solo en el bosque' AS texto, FALSE AS correcta
) t WHERE p.enunciado = '¿Qué hacen las personas de una familia?';

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Qué es una comunidad?', 2 FROM prueba_final WHERE objetivo_id = 4;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT 'Un tipo de animal' AS texto, FALSE AS correcta
    UNION ALL SELECT 'Personas que viven cerca y comparten lugares' AS texto, TRUE AS correcta
    UNION ALL SELECT 'Un juego de mesa' AS texto, FALSE AS correcta
) t WHERE p.enunciado = '¿Qué es una comunidad?';

INSERT INTO pregunta (prueba_id, enunciado, orden)
SELECT id, '¿Dónde compramos el pan en la comunidad?', 3 FROM prueba_final WHERE objetivo_id = 4;
INSERT INTO alternativa (pregunta_id, texto, correcta)
SELECT p.id, t.texto, t.correcta FROM pregunta p JOIN (
    SELECT 'En el hospital' AS texto, FALSE AS correcta
    UNION ALL SELECT 'En la escuela' AS texto, FALSE AS correcta
    UNION ALL SELECT 'En la panadería' AS texto, TRUE AS correcta
) t WHERE p.enunciado = '¿Dónde compramos el pan en la comunidad?';