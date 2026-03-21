-- Cambiar columna rol de ENUM a VARCHAR para soportar múltiples roles
ALTER TABLE usuarios MODIFY COLUMN rol VARCHAR(20) NOT NULL DEFAULT 'ADMIN';

-- Agregar usuario cocina si no existe
INSERT IGNORE INTO usuarios (username, password, rol) VALUES
('cocina', '$2a$10$Y9Ze4mLBKj0MqJuMSBpnVOHMFqnALgfVbUwJbO6QKzPiSHaLxSJkS', 'COCINA');

-- Actualizar password del admin a BCrypt (admin123)
UPDATE usuarios
SET password = '$2a$10$Y9Ze4mLBKj0MqJuMSBpnVOHMFqnALgfVbUwJbO6QKzPiSHaLxSJkS'
WHERE username = 'admin';