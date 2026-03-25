-- Tabla de usuarios (para admin interno)
CREATE TABLE IF NOT EXISTS usuarios (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN') DEFAULT 'ADMIN',
    activo BOOLEAN DEFAULT TRUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Usuario admin con password: admin123 (BCrypt)
INSERT IGNORE INTO usuarios (username, password, rol, activo) VALUES
('admin', '$2a$12$4Lz/w5KcKLEjt5WbS3pOR.an.nvynG/YXdtVYUBSq4KVz8PW4856K', 'ADMIN', TRUE);