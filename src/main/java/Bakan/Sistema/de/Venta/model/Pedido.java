package Bakan.Sistema.de.Venta.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Reemplazamos ManyToMany por OneToMany con LineaPedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas = new ArrayList<>();

    private Double precioTotal;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido = LocalDateTime.now();

    // Calcular total automáticamente
    public void calcularTotal() {
        this.precioTotal = lineas.stream()
                .mapToDouble(LineaPedido::getSubtotal)
                .sum();
    }

    // Helper para agregar líneas
    public void agregarLinea(Producto producto, Integer cantidad) {
        LineaPedido linea = new LineaPedido();
        linea.setPedido(this);
        linea.setProducto(producto);
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(producto.getPrecio()); // Precio al momento de la compra
        this.lineas.add(linea);
        calcularTotal();
    }

    public enum EstadoPedido {
        PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO
    }
}