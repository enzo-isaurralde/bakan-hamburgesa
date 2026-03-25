package Bakan.Sistema.de.Venta.controller;

import Bakan.Sistema.de.Venta.dto.PedidosRequestDTO;
import Bakan.Sistema.de.Venta.dto.PedidosResponseDTO;
import Bakan.Sistema.de.Venta.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión del ciclo de vida de pedidos.
 * Desde la creación hasta la entrega o cancelación.
 */
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Gestión de pedidos de clientes por WhatsApp")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Crea un nuevo pedido desde el formulario público/WhatsApp.
     * Endpoint público, no requiere autenticación.
     *
     * @param pedidosRequest Datos del pedido (cliente, productos, notas)
     * @return Pedido creado con ID y confirmación
     */
    @PostMapping
    @Operation(
            summary = "Crear pedido",
            description = "Crea un nuevo pedido desde la página pública o WhatsApp. " +
                    "No requiere autenticación. El pedido queda en estado PENDIENTE."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Datos del pedido inválidos")
    })
    public ResponseEntity<PedidosResponseDTO> crearPedido(
            @RequestBody PedidosRequestDTO pedidosRequest) {
        PedidosResponseDTO response = pedidoService.crearPedido(pedidosRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos los pedidos del sistema.
     * Requiere autenticación (ADMIN o COCINA).
     *
     * @return Lista completa de pedidos
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los pedidos",
            description = "Obtiene todos los pedidos del sistema para el panel de administración. " +
                    "Requiere autenticación con rol ADMIN o COCINA.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de pedidos obtenida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<PedidosResponseDTO>> listarTodos() {
        List<PedidosResponseDTO> pedidos = pedidoService.listarTodosLosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Lista pedidos en estado PENDIENTE.
     *
     * @return Pedidos pendientes de validación
     */
    @GetMapping("/pendientes")
    @Operation(
            summary = "Listar pedidos pendientes",
            description = "Obtiene pedidos en estado PENDIENTE (esperando validación del admin). " +
                    "Requiere autenticación.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de pendientes obtenida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<PedidosResponseDTO>> listarPendientes() {
        List<PedidosResponseDTO> pendientes = pedidoService.listarPedidosPendientes();
        return ResponseEntity.ok(pendientes);
    }

    /**
     * Valida un pedido pendiente (cambia a VALIDADO).
     *
     * @param id ID del pedido
     * @return Pedido actualizado
     */
    @PutMapping("/{id}/validar")
    @Operation(
            summary = "Validar pedido",
            description = "Cambia el estado de PENDIENTE a VALIDADO. " +
                    "El admin confirma que el pedido está correcto y puede pasar a cocina. " +
                    "Requiere autenticación con rol ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido validado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos de admin"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PedidosResponseDTO> validarPedido(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.validarPedido(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Pasa un pedido a preparación (cambia a EN_PREPARACION).
     *
     * @param id ID del pedido
     * @return Pedido actualizado
     */
    @PutMapping("/{id}/preparar")
    @Operation(
            summary = "Preparar pedido",
            description = "Cambia el estado de VALIDADO a EN_PREPARACION. " +
                    "La cocina comienza a preparar el pedido. " +
                    "Requiere autenticación con rol ADMIN o COCINA.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido en preparación",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PedidosResponseDTO> prepararPedido(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.prepararPedido(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Marca un pedido como listo (cambia a LISTO).
     *
     * @param id ID del pedido
     * @return Pedido actualizado
     */
    @PutMapping("/{id}/listo")
    @Operation(
            summary = "Marcar pedido listo",
            description = "Cambia el estado de EN_PREPARACION a LISTO. " +
                    "La cocina terminó de preparar el pedido. " +
                    "Requiere autenticación con rol ADMIN o COCINA.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido listo para entrega",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PedidosResponseDTO> marcarListo(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.marcarListo(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Marca un pedido como entregado (cambia a ENTREGADO).
     *
     * @param id ID del pedido
     * @return Pedido actualizado
     */
    @PutMapping("/{id}/entregar")
    @Operation(
            summary = "Entregar pedido",
            description = "Cambia el estado de LISTO a ENTREGADO. " +
                    "El pedido fue entregado al cliente. " +
                    "Requiere autenticación con rol ADMIN o COCINA.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido entregado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PedidosResponseDTO> marcarEntregado(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.marcarEntregado(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela un pedido (cambia a CANCELADO).
     *
     * @param id ID del pedido
     * @return Pedido actualizado
     */
    @PutMapping("/{id}/cancelar")
    @Operation(
            summary = "Cancelar pedido",
            description = "Cambia el estado a CANCELADO. " +
                    "El pedido fue cancelado por el admin o cliente. " +
                    "Requiere autenticación con rol ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido cancelado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos de admin"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PedidosResponseDTO> cancelarPedido(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca un pedido específico por su ID.
     *
     * @param id ID del pedido
     * @return Pedido encontrado
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar pedido por ID",
            description = "Obtiene los detalles de un pedido específico. " +
                    "Requiere autenticación.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PedidosResponseDTO> buscarPorId(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long id) {
        PedidosResponseDTO response = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
}