package Bakan.Sistema.de.Venta.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PedidosRequestDTO {
    private ClienteDTO cliente;
    private List<ProductoDTO> productos;
    private String notas; // ← agregar esto

    @Getter
    @Setter
    public static class ClienteDTO {
        private String nombre;
    }
}