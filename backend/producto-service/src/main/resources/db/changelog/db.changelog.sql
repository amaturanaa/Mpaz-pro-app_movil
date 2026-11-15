--liquibase formatted sql

--changeset carlos:1
CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    precio DOUBLE,
    stock INT
);

--changeset carlos:2
INSERT INTO producto (nombre, precio, stock) VALUES
('Laptop', 850000, 10),
('Mouse', 15000, 50),
('Teclado', 30000, 40),
('Monitor', 200000, 15),
('Impresora', 120000, 8),
('Notebook', 650000, 12),
('Disco SSD 1TB', 90000, 25),
('Memoria RAM 16GB', 70000, 30),
('Webcam HD', 25000, 20),
('Audífonos', 40000, 35);