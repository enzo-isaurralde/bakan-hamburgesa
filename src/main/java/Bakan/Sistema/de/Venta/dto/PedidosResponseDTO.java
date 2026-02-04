package Bakan.Sistema.de.Venta.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter
public class PedidosResponseDTO {
    private Long idPedido;
    private String estado;
    private double precioTotal;
    private List<ProductoDTO> productos;
    private String mensajeConfirmacion;
}
