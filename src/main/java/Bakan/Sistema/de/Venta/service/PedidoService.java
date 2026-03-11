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

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    // Crear pedido
    public PedidosResponseDTO crearPedido(PedidosRequestDTO pedidosRequest) {
        Cliente cliente = new Cliente();
        cliente.setNombre(pedidosRequest.getCliente().getNombre());
        clienteRepository.save(cliente);

        List<Producto> productos = productoRepository.findAllById(pedidosRequest.getPedidosIds());

        Double precioTotal = productos.stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProductos(productos);
        pedido.setPrecioTotal(precioTotal);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido recibido para " + cliente.getNombre());
    }

    // Listar pedidos pendientes
    public List<PedidosResponseDTO> listarPedidosPendientes() {
        return pedidoRepository.findByEstado(EstadoPedido.PENDIENTE)
                .stream()
                .map(p -> mapToResponseDTO(p, "Pedido pendiente de validación"))
                .collect(Collectors.toList());
    }

    // Validar pedido
    public PedidosResponseDTO validarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(EstadoPedido.VALIDADO);
        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido validado correctamente");
    }

    // Cancelar pedido
    public PedidosResponseDTO cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido cancelado correctamente");
    }

    // Método auxiliar para mapear Pedido → PedidosResponseDTO
    private PedidosResponseDTO mapToResponseDTO(Pedido pedido, String mensaje) {
        PedidosResponseDTO response = new PedidosResponseDTO();
        response.setIdPedido(pedido.getId());
        response.setEstado(pedido.getEstado().name());
        response.setPrecioTotal(pedido.getPrecioTotal());
        response.setProductos(pedido.getProductos().stream().map(p -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre());
            dto.setPrecio(p.getPrecio());
            return dto;
        }).collect(Collectors.toList()));
        response.setMensajeConfirmacion(mensaje);
        return response;
    }
}
