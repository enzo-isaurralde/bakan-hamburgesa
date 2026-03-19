SET NAMES utf8mb4;

-- Papas (ya están en la BD pero no en migraciones, las agregamos con IF NOT EXISTS)
INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo, imagen_url)
SELECT 'Papas Fritas Común', 3000.00, 1, 'Papas fritas crocantes recién hechas', '🍟', 'PAPAS', 0, 0, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Papas Fritas Común');

INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo, imagen_url)
SELECT 'Papas Fritas con Cheddar', 4500.00, 1, 'Papas fritas bañadas en salsa cheddar', '🧀', 'PAPAS', 0, 0, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Papas Fritas con Cheddar');

INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo, imagen_url)
SELECT 'Papas Fritas con Bacon', 5000.00, 1, 'Papas fritas con bacon crocante', '🥓', 'PAPAS', 0, 0, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Papas Fritas con Bacon');

-- Bebidas nuevas
INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo, imagen_url)
SELECT 'Agua Mineral', 1500.00, 1, 'Agua mineral fría 500ml', '💧', 'BEBIDAS', 0, 0, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Agua Mineral');

INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo, imagen_url)
SELECT 'Pepsi', 2000.00, 1, 'Pepsi fría 500ml', '🥤', 'BEBIDAS', 0, 0, NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Pepsi');

-- Fix emojis todos los productos
UPDATE productos SET emoji = '🍔' WHERE id IN (1,2,3,4,6);
UPDATE productos SET emoji = '🥩' WHERE id = 5;
UPDATE productos SET emoji = '🍗' WHERE id = 7;
UPDATE productos SET emoji = '🌱' WHERE id = 8;
UPDATE productos SET emoji = '🧀' WHERE id IN (9,11);
UPDATE productos SET emoji = '🍟' WHERE id = 10;
UPDATE productos SET emoji = '🥓' WHERE id = 12;