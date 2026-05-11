CREATE DATABASE IF NOT EXISTS techpoint;
USE techpoint;

CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    margen_sugerido DECIMAL(5,2) NOT NULL,
    descripcion VARCHAR(200)
);

CREATE TABLE canal_venta (
    id_canal INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    comision_pct DECIMAL(5,2) NOT NULL,
    descripcion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE tipo_cambio (
    id_cotizacion INT AUTO_INCREMENT PRIMARY KEY,
    valor_usd DECIMAL(10,2) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    fuente VARCHAR(100),
    validado BOOLEAN DEFAULT FALSE
);

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200),
    costo_usd DECIMAL(10,2) NOT NULL,
    costo_ars DECIMAL(12,2),
    margen_seguridad DECIMAL(5,2) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    id_categoria INT NOT NULL,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE margen_rentabilidad (
    id_margen INT AUTO_INCREMENT PRIMARY KEY,
    precio_sugerido_ars DECIMAL(12,2) NOT NULL,
    ganancia_real DECIMAL(12,2),
    alerta_activa BOOLEAN DEFAULT FALSE,
    fecha_calculo DATETIME NOT NULL,
    id_producto INT NOT NULL,
    id_cotizacion INT NOT NULL,
    id_canal INT NOT NULL,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    FOREIGN KEY (id_cotizacion) REFERENCES tipo_cambio(id_cotizacion),
    FOREIGN KEY (id_canal) REFERENCES canal_venta(id_canal)
);

INSERT INTO categoria (nombre, margen_sugerido, descripcion) VALUES
('Mobile y Wearables', 25.00, 'Smartphones y relojes inteligentes'),
('Gaming', 20.00, 'Consolas y periféricos'),
('Audio', 30.00, 'Parlantes y auriculares'),
('Hogar y Tecnología Varios', 35.00, 'Drones, cafeteros, termos');

INSERT INTO canal_venta (nombre, comision_pct, descripcion, activo) VALUES
('Marketplace', 12.00, 'Venta a través de marketplace propio', TRUE),
('Mercado Libre', 17.00, 'Venta a través de Mercado Libre', TRUE),
('Redes Sociales', 5.00, 'Venta directa por Instagram/Facebook', TRUE);

INSERT INTO tipo_cambio (valor_usd, fecha_hora, fuente, validado) VALUES
(1180.00, NOW(), 'API Dolarito', TRUE),
(1195.00, NOW(), 'API Dolarito', TRUE);

INSERT INTO producto (nombre, descripcion, costo_usd, costo_ars, margen_seguridad, id_categoria) VALUES
('iPhone 15 128GB', 'Smartphone Apple última generación', 650.00, 767000.00, 25.00, 1),
('Samsung Galaxy S24', 'Smartphone Samsung flagship', 550.00, 649000.00, 25.00, 1),
('PlayStation 5', 'Consola Sony última generación', 450.00, 531000.00, 20.00, 2),
('Sony WH-1000XM5', 'Auriculares premium noise cancelling', 280.00, 330400.00, 30.00, 3),
('DJI Mini 4 Pro', 'Drone compacto profesional', 760.00, 896800.00, 35.00, 4);

INSERT INTO margen_rentabilidad (precio_sugerido_ars, ganancia_real, alerta_activa, fecha_calculo, id_producto, id_cotizacion, id_canal) VALUES
(958750.00, 191750.00, FALSE, NOW(), 1, 1, 1),
(811250.00, 162250.00, FALSE, NOW(), 2, 1, 2),
(637200.00, 106200.00, FALSE, NOW(), 3, 1, 3),
(429520.00, 99120.00, FALSE, NOW(), 4, 2, 1),
(1210680.00, 313880.00, FALSE, NOW(), 5, 2, 2);
