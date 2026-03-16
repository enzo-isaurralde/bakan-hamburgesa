-- Agregar campos (sin IF NOT EXISTS, Flyway controla que se ejecute una sola vez)
ALTER TABLE productos ADD COLUMN descripcion TEXT;
ALTER TABLE productos ADD COLUMN emoji VARCHAR(10);
ALTER TABLE productos ADD COLUMN categoria VARCHAR(50) DEFAULT 'HAMBURGUESAS';
ALTER TABLE productos ADD COLUMN es_popular BOOLEAN DEFAULT FALSE;
ALTER TABLE productos ADD COLUMN es_nuevo BOOLEAN DEFAULT FALSE;

-- Actualizar productos existentes
UPDATE productos SET
                     descripcion = 'Carne vaca 180g, queso cheddar, lechuga, tomate, cebolla caramelizada y salsa especial de la casa en pan brioche.',
                     emoji = '🍔',
                     categoria = 'HAMBURGUESAS',
                     es_popular = TRUE
WHERE nombre = 'Bakan Clásica';

-- Insertar más productos
INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo)
SELECT 'Doble Smash', 9200, TRUE, 'Doble carne smash 120g cada una, doble cheddar americano, pickles, cebolla fresca y mostaza en pan de papa.', '🥩', 'HAMBURGUESAS', TRUE, FALSE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Doble Smash');

INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo)
SELECT 'Triple Threat', 11500, TRUE, 'Triple carne 150g, triple queso cheddar, panceta crispy, huevo frito y BBQ en pan brioche tostado.', '🍔', 'HAMBURGUESAS', FALSE, FALSE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Triple Threat');

INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo)
SELECT 'Pollo Crispy', 7800, TRUE, 'Pechuga de pollo crispy, coleslaw, pickles, queso americano y mayo picante en pan brioche.', '🍗', 'HAMBURGUESAS', FALSE, FALSE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Pollo Crispy');

INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo)
SELECT 'Veggie Power', 7200, TRUE, 'Medallón de lentejas y quinoa, queso tybo, palta, rúcula, tomate y alioli en pan integral.', '🌱', 'HAMBURGUESAS', FALSE, TRUE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Veggie Power');

INSERT INTO productos (nombre, precio, disponible, descripcion, emoji, categoria, es_popular, es_nuevo)
SELECT 'Cheese Overload', 9800, TRUE, 'Carne 200g, cuatro quesos (cheddar, mozzarella, blue, emmental), cebolla crispy y miel mostaza.', '🧀', 'HAMBURGUESAS', TRUE, FALSE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Cheese Overload');