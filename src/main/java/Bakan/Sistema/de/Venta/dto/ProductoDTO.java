package Bakan.Sistema.de.Venta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para representar un producto en pedidos.
 * Usado tanto en requests (cantidad solicitada) como en responses (con subtotal calculado).
 */
@Getter
@Setter
@Schema(description = "Producto en un pedido con cantidad y subtotal calculado")
public class ProductoDTO {

    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Hamburguesa Clásica")
    private String nombre;

    @Schema(description = "Precio unitario del producto", example = "8500.00")
    private Double precio;

    @Schema(description = "Cantidad solicitada del producto", example = "2")
    private Integer cantidad;

    @Schema(description = "Subtotal calculado (cantidad × precio)", example = "17000.00")
    private Double subtotal;
}