package Bakan.Sistema.de.Venta.controller;

import Bakan.Sistema.de.Venta.model.Producto;
import Bakan.Sistema.de.Venta.repository.ProductoRepository;
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
 * Controller para gestión de productos del menú.
 * Permite listar productos disponibles y administrar su estado.
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@Tag(name = "Productos", description = "Gestión del menú de hamburguesas y productos")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Lista todos los productos disponibles para la carta/menu principal.
     * Endpoint público, no requiere autenticación.
     *
     * @return Lista de productos con disponible=true
     */
    @GetMapping
    @Operation(
            summary = "Listar productos disponibles",
            description = "Obtiene todos los productos disponibles para mostrar en el menú público. " +
                    "Este endpoint es público y no requiere autenticación."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de productos obtenida exitosamente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Producto.class)
            )
    )
    public ResponseEntity<List<Producto>> listarDisponibles() {
        List<Producto> productos = productoRepository.findByDisponibleTrue();
        return ResponseEntity.ok(productos);
    }

    /**
     * Lista TODOS los productos (incluyendo no disponibles).
     * Requiere autenticación de administrador.
     *
     * @return Lista completa de productos
     */
    @GetMapping("/todos")
    @Operation(
            summary = "Listar todos los productos",
            description = "Obtiene todos los productos del sistema, incluyendo los no disponibles. " +
                    "Requiere autenticación con rol ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista completa obtenida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos de administrador")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Producto>> listarTodos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }

    /**
     * Cambia el estado de disponibilidad de un producto.
     * Si está disponible pasa a no disponible y viceversa.
     *
     * @param id ID del producto a modificar
     * @return Producto actualizado
     */
    @PutMapping("/{id}/toggle")
    @Operation(
            summary = "Cambiar disponibilidad",
            description = "Activa o desactiva un producto del menú. " +
                    "Si el producto está disponible, lo desactiva. Si está desactivado, lo activa. " +
                    "Requiere autenticación con rol ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos de administrador"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Producto> toggleDisponibilidad(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setDisponible(!producto.getDisponible());
        productoRepository.save(producto);

        return ResponseEntity.ok(producto);
    }
}