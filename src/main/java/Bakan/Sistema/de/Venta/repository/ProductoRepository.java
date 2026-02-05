package Bakan.Sistema.de.Venta.repository;

import Bakan.Sistema.de.Venta.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository <Producto, Long> {

// Ejemplo: buscar productos disponibles //
// List<Producto> findByDisponibleTrue();
}
