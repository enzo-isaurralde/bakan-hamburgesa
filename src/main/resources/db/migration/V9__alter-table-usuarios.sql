-- Cambiar columna rol de ENUM a VARCHAR para soportar múltiples roles
ALTER TABLE usuarios MODIFY COLUMN rol VARCHAR(20) NOT NULL DEFAULT 'ADMIN';

-- Agregar usuario cocina con password: admin123 (mismo hash)
INSERT IGNORE INTO usuarios (username, password, rol, activo) VALUES
('cocina', '$2a$12$4Lz/w5KcKLEjt5WbS3pOR.an.nvynG/YXdtVYUBSq4KVz8PW4856K', 'COCINA', TRUE);