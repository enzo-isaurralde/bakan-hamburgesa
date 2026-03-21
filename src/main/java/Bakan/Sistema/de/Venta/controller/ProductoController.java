package Bakan.Sistema.de.Venta.controller;

import Bakan.Sistema.de.Venta.model.Producto;
import Bakan.Sistema.de.Venta.repository.ProductoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Listar productos disponibles (para la página principal)
    @GetMapping
    public ResponseEntity<List<Producto>> listarDisponibles() {
        List<Producto> productos = productoRepository.findByDisponibleTrue();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Producto>> listarTodos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }

    // Toggle disponibilidad (para el panel admin)
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Producto> toggleDisponibilidad(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setDisponible(!producto.getDisponible());
        productoRepository.save(producto);

        return ResponseEntity.ok(producto);
    }
}