package Bakan.Sistema.de.Venta.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Entity(name="pedido")
@Table(name="pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    @ManyToMany
    private List<Producto> productos;

    private Double precioTotal;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    // Getters y Setters
}
