package Bakan.Sistema.de.Venta.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer cantidad;    // NUEVO: cantidad pedida
    private Double subtotal;      // NUEVO: calculado (cantidad * precio)
}