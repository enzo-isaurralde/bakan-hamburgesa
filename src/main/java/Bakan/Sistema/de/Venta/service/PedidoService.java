package Bakan.Sistema.de.Venta.service;

import Bakan.Sistema.de.Venta.dto.PedidosRequestDTO;
import Bakan.Sistema.de.Venta.dto.PedidosResponseDTO;
import Bakan.Sistema.de.Venta.dto.ProductoDTO;
import Bakan.Sistema.de.Venta.model.Cliente;
import Bakan.Sistema.de.Venta.model.Pedido;
import Bakan.Sistema.de.Venta.model.Producto;
import Bakan.Sistema.de.Venta.model.EstadoPedido;
import Bakan.Sistema.de.Venta.repository.ClienteRepository;
import Bakan.Sistema.de.Venta.repository.PedidoRepository;
import Bakan.Sistema.de.Venta.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    // Constructor con inyección de dependencias
    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    public PedidosResponseDTO crearPedido(PedidosRequestDTO pedidosRequest) {
        // 1. Crear Cliente
        Cliente cliente = new Cliente();
        cliente.setNombre(pedidosRequest.getCliente().getNombre());
        clienteRepository.save(cliente);

        // 2. Buscar productos por IDs
        List<Producto> productos = productoRepository.findAllById(pedidosRequest.getPedidosIds());

        // 3. Calcular precio total
        Double precioTotal = productos.stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        // 4. Crear Pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProductos(productos);
        pedido.setPrecioTotal(precioTotal);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        pedidoRepository.save(pedido);

        // 5. Armar respuesta DTO
        PedidosResponseDTO response = new PedidosResponseDTO();
        response.setIdPedido(pedido.getId());
        response.setEstado(pedido.getEstado().name());
        response.setPrecioTotal(precioTotal);
        response.setProductos(productos.stream().map(p -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre());
            dto.setPrecio(p.getPrecio());
            return dto;
        }).collect(Collectors.toList()));
        response.setMensajeConfirmacion("Pedido recibido para " + cliente.getNombre());

        return response;
    }
}
