--liquibase formatted sql

--changeset mpaz:1
CREATE TABLE asignatura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    nivel VARCHAR(50) NOT NULL
);

CREATE TABLE unidad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    asignatura_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    orden INT,
    estado VARCHAR(30) NOT NULL,
    responsable_utp VARCHAR(150),
    fecha_actualizacion DATE,
    CONSTRAINT fk_unidad_asignatura FOREIGN KEY (asignatura_id) REFERENCES asignatura(id)
);

CREATE TABLE objetivo_aprendizaje (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unidad_id BIGINT NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    eje_curricular VARCHAR(150),
    texto_referencia VARCHAR(500),
    estado VARCHAR(30) NOT NULL,
    responsable_utp VARCHAR(150),
    fecha_actualizacion DATE,
    CONSTRAINT fk_objetivo_unidad FOREIGN KEY (unidad_id) REFERENCES unidad(id)
);

CREATE TABLE contenido_didactico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    objetivo_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    explicacion_escrita TEXT,
    archivo_audio VARCHAR(255),
    imagenes VARCHAR(500),
    ejemplos_guiados TEXT,
    instrucciones VARCHAR(500),
    orden INT,
    responsable_origen VARCHAR(150),
    CONSTRAINT fk_contenido_objetivo FOREIGN KEY (objetivo_id) REFERENCES objetivo_aprendizaje(id)
);

--changeset mpaz:2
-- Datos de ejemplo FICTICIOS (el caso exige no usar datos reales)
INSERT INTO asignatura (nombre, nivel) VALUES
('Lenguaje y Comunicación', '2.º Básico'),
('Matemática', '2.º Básico'),
('Ciencias Naturales', '2.º Básico'),
('Historia, Geografía y Ciencias Sociales', '2.º Básico');

INSERT INTO unidad (asignatura_id, titulo, orden, estado, responsable_utp, fecha_actualizacion) VALUES
(1, 'Unidad 1: Leemos y comprendemos cuentos', 1, 'Publicado', 'UTP Ficticia', CURRENT_DATE),
(2, 'Unidad 1: Números hasta el 100', 1, 'Publicado', 'UTP Ficticia', CURRENT_DATE),
(3, 'Unidad 1: Los seres vivos y su entorno', 1, 'Publicado', 'UTP Ficticia', CURRENT_DATE),
(4, 'Unidad 1: Mi familia y mi comunidad', 1, 'Publicado', 'UTP Ficticia', CURRENT_DATE);

INSERT INTO objetivo_aprendizaje (unidad_id, codigo, descripcion, eje_curricular, texto_referencia, estado, responsable_utp, fecha_actualizacion) VALUES
(1, 'LYC-2B-01', 'Leer en voz alta textos breves y responder preguntas sobre lo que leyó.', 'Lectura', 'Texto de referencia de ejemplo (ficticio).', 'Publicado', 'UTP Ficticia', CURRENT_DATE),
(2, 'MAT-2B-01', 'Contar, leer y escribir números del 0 al 100.', 'Números y operaciones', 'Texto de referencia de ejemplo (ficticio).', 'Publicado', 'UTP Ficticia', CURRENT_DATE),
(3, 'CNA-2B-01', 'Reconocer características de plantas y animales y los lugares donde viven.', 'Ciencias de la vida', 'Texto de referencia de ejemplo (ficticio).', 'Publicado', 'UTP Ficticia', CURRENT_DATE),
(4, 'HGC-2B-01', 'Describir cómo es una familia y qué actividades se realizan en la comunidad.', 'Historia', 'Texto de referencia de ejemplo (ficticio).', 'Publicado', 'UTP Ficticia', CURRENT_DATE);

INSERT INTO contenido_didactico (objetivo_id, titulo, explicacion_escrita, archivo_audio, imagenes, ejemplos_guiados, instrucciones, orden, responsable_origen) VALUES
(1, 'Leemos un cuento corto', 'Un cuento tiene inicio, desarrollo y final. Leemos despacio y pensamos qué pasó.', 'audio/lyc-2b-01.mp3', 'img/lyc-2b-01.png', 'Ejemplo: "Había una vez un gato que quería volar..."', 'Escucha el audio y lee junto a él.', 1, 'Material propio (ficticio)'),
(2, 'Contamos hasta 100', 'Contamos de 1 en 1 y también de 10 en 10: 10, 20, 30...', 'audio/mat-2b-01.mp3', 'img/mat-2b-01.png', 'Ejemplo: 10, 20, 30, 40...', 'Cuenta en voz alta junto al audio.', 1, 'Material propio (ficticio)'),
(3, 'Plantas y animales', 'Las plantas y los animales son seres vivos. Viven en distintos lugares.', 'audio/cna-2b-01.mp3', 'img/cna-2b-01.png', 'Ejemplo: el pez vive en el agua.', 'Mira las imágenes y escucha con atención.', 1, 'Material propio (ficticio)'),
(4, 'Mi familia', 'Una familia son personas que se cuidan. Hay familias grandes y pequeñas.', 'audio/hgc-2b-01.mp3', 'img/hgc-2b-01.png', 'Ejemplo: en mi familia vivimos cuatro personas.', 'Escucha y cuenta cómo es tu familia.', 1, 'Material propio (ficticio)');