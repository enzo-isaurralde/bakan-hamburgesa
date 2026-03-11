package Bakan.Sistema.de.Venta.controller;

import Bakan.Sistema.de.Venta.dto.PedidosRequestDTO;
import Bakan.Sistema.de.Venta.dto.PedidosResponseDTO;
import Bakan.Sistema.de.Venta.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Crear un pedido (cliente)
    @PostMapping
    public ResponseEntity<PedidosResponseDTO> crearPedido(@RequestBody PedidosRequestDTO pedidosRequest) {
        PedidosResponseDTO response = pedidoService.crearPedido(pedidosRequest);
        return ResponseEntity.ok(response);
    }

    // Listar pedidos pendientes (admin)
    @GetMapping("/pendientes")
    public ResponseEntity<List<PedidosResponseDTO>> listarPendientes() {
        List<PedidosResponseDTO> pendientes = pedidoService.listarPedidosPendientes();
        return ResponseEntity.ok(pendientes);
    }

    // Validar pedido (admin)
    @PutMapping("/{id}/validar")
    public ResponseEntity<PedidosResponseDTO> validarPedido(@PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.validarPedido(id);
        return ResponseEntity.ok(response);
    }

    // Cancelar pedido (admin)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidosResponseDTO> cancelarPedido(@PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(response);
    }
}
