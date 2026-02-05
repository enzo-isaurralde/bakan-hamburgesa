package Bakan.Sistema.de.Venta.repository;

import Bakan.Sistema.de.Venta.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Aquí puedes agregar consultas personalizadas si lo necesitas
}
