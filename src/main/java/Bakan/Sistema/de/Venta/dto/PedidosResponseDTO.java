package Bakan.Sistema.de.Venta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO de respuesta con los datos de un pedido creado o consultado.
 * Incluye el ID generado, estado actual y confirmación.
 */
@Getter
@Setter
@Schema(description = "Respuesta con datos de un pedido")
public class PedidosResponseDTO {

    @Schema(description = "ID único del pedido", example = "1")
    private Long idPedido;

    @Schema(description = "Estado actual del pedido",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "VALIDADO", "EN_PREPARACION", "LISTO", "ENTREGADO", "CANCELADO"})
    private String estado;

    @Schema(description = "Precio total del pedido", example = "8500.00")
    private Double precioTotal;

    @Schema(description = "Lista de productos incluidos en el pedido")
    private List<ProductoDTO> productos;

    @Schema(description = "Mensaje de confirmación para el cliente",
            example = "Pedido #123 recibido. Tiempo estimado: 30 minutos.")
    private String mensajeConfirmacion;

    @Schema(description = "Notas o comentarios del pedido", example = "Sin cebolla")
    private String notas;

    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    private String nombreCliente;
}