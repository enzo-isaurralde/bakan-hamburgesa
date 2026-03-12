package Bakan.Sistema.de.Venta.service;

import Bakan.Sistema.de.Venta.dto.PedidosRequestDTO;
import Bakan.Sistema.de.Venta.dto.PedidosResponseDTO;
import Bakan.Sistema.de.Venta.dto.ProductoDTO;
import Bakan.Sistema.de.Venta.model.Cliente;
import Bakan.Sistema.de.Venta.model.EstadoPedido;  // Importar el enum SEPARADO
import Bakan.Sistema.de.Venta.model.LineaPedido;
import Bakan.Sistema.de.Venta.model.Pedido;
import Bakan.Sistema.de.Venta.model.Producto;
import Bakan.Sistema.de.Venta.repository.ClienteRepository;
import Bakan.Sistema.de.Venta.repository.PedidoRepository;
import Bakan.Sistema.de.Venta.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public PedidosResponseDTO crearPedido(PedidosRequestDTO pedidosRequest) {
        Cliente cliente = new Cliente();
        cliente.setNombre(pedidosRequest.getCliente().getNombre());
        clienteRepository.save(cliente);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEstado(EstadoPedido.PENDIENTE);  // Usar el enum SEPARADO

        for (ProductoDTO productoDTO : pedidosRequest.getProductos()) {
            Producto producto = productoRepository.findById(productoDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoDTO.getId()));

            Integer cantidad = productoDTO.getCantidad() != null ? productoDTO.getCantidad() : 1;
            pedido.agregarLinea(producto, cantidad);
        }

        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido recibido para " + cliente.getNombre());
    }

    public List<PedidosResponseDTO> listarPedidosPendientes() {
        return pedidoRepository.findByEstado(EstadoPedido.PENDIENTE)  // Usar el enum SEPARADO
                .stream()
                .map(p -> mapToResponseDTO(p, "Pedido pendiente"))
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidosResponseDTO prepararPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(EstadoPedido.EN_PREPARACION);  // Usar el enum SEPARADO
        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido en preparación");
    }

    @Transactional
    public PedidosResponseDTO marcarListo(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(EstadoPedido.LISTO);  // Usar el enum SEPARADO
        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido listo para entregar");
    }

    @Transactional
    public PedidosResponseDTO marcarEntregado(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(EstadoPedido.ENTREGADO);  // Usar el enum SEPARADO
        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido entregado");
    }

    @Transactional
    public PedidosResponseDTO cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() == EstadoPedido.LISTO ||
                pedido.getEstado() == EstadoPedido.ENTREGADO) {  // Usar el enum SEPARADO
            throw new RuntimeException("No se puede cancelar un pedido ya listo o entregado");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);  // Usar el enum SEPARADO
        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido cancelado correctamente");
    }

    private PedidosResponseDTO mapToResponseDTO(Pedido pedido, String mensaje) {
        PedidosResponseDTO response = new PedidosResponseDTO();
        response.setIdPedido(pedido.getId());
        response.setEstado(pedido.getEstado().name());
        response.setPrecioTotal(pedido.getPrecioTotal());

        List<ProductoDTO> productosDTO = pedido.getLineas().stream().map(linea -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(linea.getProducto().getId());
            dto.setNombre(linea.getProducto().getNombre());
            dto.setPrecio(linea.getPrecioUnitario());
            dto.setCantidad(linea.getCantidad());
            dto.setSubtotal(linea.getSubtotal());
            return dto;
        }).collect(Collectors.toList());

        response.setProductos(productosDTO);
        response.setMensajeConfirmacion(mensaje);
        return response;
    }
    @Transactional
    public PedidosResponseDTO validarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new RuntimeException("Solo se pueden validar pedidos pendientes");
        }

        pedido.setEstado(EstadoPedido.VALIDADO);
        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido validado correctamente");
    }
    // Lista Pedidos

    public List<PedidosResponseDTO> listarTodosLosPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(p -> mapToResponseDTO(p, "Pedido #" + p.getId()))
                .collect(Collectors.toList());
    }
}