package Bakan.Sistema.de.Venta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO para crear un nuevo pedido.
 * Contiene los datos del cliente, productos seleccionados y notas opcionales.
 */
@Getter
@Setter
@Schema(description = "Datos para crear un nuevo pedido")
public class PedidosRequestDTO {

    @Schema(description = "Datos del cliente que realiza el pedido", requiredMode = Schema.RequiredMode.REQUIRED)
    private ClienteDTO cliente;

    @Schema(description = "Lista de productos solicitados con cantidades", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ProductoDTO> productos;

    @Schema(description = "Notas o comentarios adicionales para el pedido", example = "Sin cebolla, por favor")
    private String notas;

    /**
     * DTO interno para datos del cliente.
     */
    @Getter
    @Setter
    @Schema(description = "Datos del cliente")
    public static class ClienteDTO {

        @Schema(description = "Nombre del cliente", example = "Juan Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
        private String nombre;
    }
}