-- Tabla de productos (hamburguesas)
CREATE TABLE IF NOT EXISTS productos (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         nombre VARCHAR(150) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    disponible BOOLEAN DEFAULT TRUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de clientes (solo datos básicos para WhatsApp)
CREATE TABLE IF NOT EXISTS clientes (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nombre VARCHAR(100) NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de pedidos
CREATE TABLE IF NOT EXISTS pedidos (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       cliente_id BIGINT NOT NULL,
                                       precio_total DECIMAL(10,2),
    estado ENUM('PENDIENTE', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'CANCELADO') DEFAULT 'PENDIENTE',
    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla intermedia para relación ManyToMany Pedido-Producto
CREATE TABLE IF NOT EXISTS pedido_productos (
                                                pedido_id BIGINT NOT NULL,
                                                producto_id BIGINT NOT NULL,
                                                PRIMARY KEY (pedido_id, producto_id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;