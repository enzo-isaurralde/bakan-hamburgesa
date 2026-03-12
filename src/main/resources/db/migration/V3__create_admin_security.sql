-- Tabla de usuarios (para admin interno)
CREATE TABLE IF NOT EXISTS usuarios (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN') DEFAULT 'ADMIN',
    activo BOOLEAN DEFAULT TRUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Usuario admin (password: admin - encriptar en producción)
INSERT INTO usuarios (username, password) VALUES
    ('admin', '{noop}admin');