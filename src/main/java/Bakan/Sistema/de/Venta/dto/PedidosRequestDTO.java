package Bakan.Sistema.de.Venta.dto;

import lombok.Getter;
import lombok.Setter;
import Bakan.Sistema.de.Venta.model.Cliente;


import java.util.List;
@Getter @Setter
public class PedidosRequestDTO {
    private Cliente cliente;
    private List<Long> pedidosIds;
}
