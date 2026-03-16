package Bakan.Sistema.de.Venta.repository;

import Bakan.Sistema.de.Venta.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("SELECT p FROM producto p WHERE p.disponible = true")
    List<Producto> findByDisponibleTrue();

}