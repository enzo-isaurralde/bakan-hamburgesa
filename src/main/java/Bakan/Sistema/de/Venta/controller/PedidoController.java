package Bakan.Sistema.de.Venta.controller;

import Bakan.Sistema.de.Venta.dto.PedidosRequestDTO;
import Bakan.Sistema.de.Venta.dto.PedidosResponseDTO;
import Bakan.Sistema.de.Venta.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // Permitir cualquier origen (desarrollo)
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // Crear un pedido (cliente/WhatsApp)
    @PostMapping
    public ResponseEntity<PedidosResponseDTO> crearPedido(@RequestBody PedidosRequestDTO pedidosRequest) {
        PedidosResponseDTO response = pedidoService.crearPedido(pedidosRequest);
        return ResponseEntity.ok(response);
    }

    // Listar TODOS los pedidos (para el panel admin)
    @GetMapping
    public ResponseEntity<List<PedidosResponseDTO>> listarTodos() {
        // Necesitamos agregar este método al service
        List<PedidosResponseDTO> pedidos = pedidoService.listarTodosLosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    // Listar pedidos pendientes (admin)
    @GetMapping("/pendientes")
    public ResponseEntity<List<PedidosResponseDTO>> listarPendientes() {
        List<PedidosResponseDTO> pendientes = pedidoService.listarPedidosPendientes();
        return ResponseEntity.ok(pendientes);
    }

    // Validar pedido (admin revisa y aprueba)
    @PutMapping("/{id}/validar")
    public ResponseEntity<PedidosResponseDTO> validarPedido(@PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.validarPedido(id);
        return ResponseEntity.ok(response);
    }

    // Preparar pedido (pasa a cocina)
    @PutMapping("/{id}/preparar")
    public ResponseEntity<PedidosResponseDTO> prepararPedido(@PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.prepararPedido(id);
        return ResponseEntity.ok(response);
    }

    // Marcar listo (cocina terminó)
    @PutMapping("/{id}/listo")
    public ResponseEntity<PedidosResponseDTO> marcarListo(@PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.marcarListo(id);
        return ResponseEntity.ok(response);
    }

    // Entregar pedido (delivery)
    @PutMapping("/{id}/entregar")
    public ResponseEntity<PedidosResponseDTO> marcarEntregado(@PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.marcarEntregado(id);
        return ResponseEntity.ok(response);
    }

    // Cancelar pedido
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidosResponseDTO> cancelarPedido(@PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(response);
    }
}