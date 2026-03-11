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

    private  String nombre;
    private  Double precio;
    private  Boolean disponible = true;

}
