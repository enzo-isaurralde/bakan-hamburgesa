package Bakan.Sistema.de.Venta.repository;

import Bakan.Sistema.de.Venta.model.EstadoPedido;
import Bakan.Sistema.de.Venta.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Ejemplo: listar pedidos por estado
    List<Pedido> findByEstado(EstadoPedido estado);
}
