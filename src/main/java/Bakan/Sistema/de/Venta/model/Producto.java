package Bakan.Sistema.de.Venta.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity(name="producto")
@Table(name="productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double precio;
    private Boolean disponible = true;

    // Campos agregados en V4
    private String descripcion;
    private String emoji;
    private String categoria;
    private Boolean esPopular = false;
    private Boolean esNuevo = false;
    // Campos agregados en V6
    @Column(name = "imagen_url")
    private String imagenUrl;
}