package Bakan.Sistema.de.Venta.service;

import Bakan.Sistema.de.Venta.dto.PedidosRequestDTO;
import Bakan.Sistema.de.Venta.dto.PedidosResponseDTO;
import Bakan.Sistema.de.Venta.dto.ProductoDTO;
import Bakan.Sistema.de.Venta.model.Cliente;
import Bakan.Sistema.de.Venta.model.EstadoPedido;
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

/**
 * ============================================================
 *  PedidoService — Capa de lógica de negocio para pedidos
 * ============================================================
 *
 * Esta clase es el "cerebro" del sistema de pedidos.
 * Se encarga de:
 *   - Crear nuevos pedidos (con sus validaciones)
 *   - Mover un pedido por los distintos estados del flujo
 *   - Listar pedidos según su estado
 *
 * Flujo de estados de un pedido:
 *   PENDIENTE → VALIDADO → EN_PREPARACION → LISTO → ENTREGADO
 *                                                 ↘ CANCELADO (antes de LISTO)
 *
 * @see EstadoPedido para ver todos los estados disponibles
 * @see HorarioService para la lógica de horarios de atención
 */
@Service
public class PedidoService {

    // =========================================================
    //  Dependencias inyectadas por constructor
    // =========================================================

    private final PedidoRepository   pedidoRepository;   // Acceso a la tabla "pedidos"
    private final ClienteRepository  clienteRepository;  // Acceso a la tabla "clientes"
    private final ProductoRepository productoRepository; // Acceso a la tabla "productos"
    private final HorarioService     horarioService;     // Valida si el local está abierto

    /*
     * Spring inyecta automáticamente estas dependencias al arrancar.
     * Usamos inyección por constructor (en vez de @Autowired en el campo)
     * porque es la forma recomendada: hace el código más testeable y claro.
     */
    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProductoRepository productoRepository,
                         HorarioService horarioService) {
        this.pedidoRepository   = pedidoRepository;
        this.clienteRepository  = clienteRepository;
        this.productoRepository = productoRepository;
        this.horarioService     = horarioService;
    }

    // =========================================================
    //  CREAR PEDIDO
    // =========================================================

    /**
     * Crea un nuevo pedido a partir de los datos enviados por el cliente.
     *
     * Pasos internos:
     *   1. Valida que el local esté abierto (horario de atención)
     *   2. Crea y guarda el cliente
     *   3. Crea el pedido y le asigna estado PENDIENTE
     *   4. Agrega cada producto al pedido (con su cantidad y precio)
     *   5. Guarda el pedido completo en la base de datos
     *
     * @param pedidosRequest DTO con los datos del cliente y los productos pedidos
     * @return DTO de respuesta con el resumen del pedido creado
     */
    @Transactional // Si algo falla en el medio, toda la operación se revierte (rollback)
    public PedidosResponseDTO crearPedido(PedidosRequestDTO pedidosRequest) {

        // ── PASO 1: Validar horario ───────────────────────────────────────────
        // Si el local está cerrado, este método lanza una HorarioException.
        // El GlobalExceptionHandler la captura y devuelve HTTP 400 con
        // mensaje amigable. El código debajo de esta línea NO se ejecuta.
        horarioService.validarHorario();

        // ── PASO 2: Crear el cliente ──────────────────────────────────────────
        // Por ahora solo guardamos el nombre. En el futuro podría incluir
        // teléfono, dirección, etc.
        Cliente cliente = new Cliente();
        cliente.setNombre(pedidosRequest.getCliente().getNombre());
        clienteRepository.save(cliente);

        // ── PASO 3: Crear el pedido base ──────────────────────────────────────
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setNotas(pedidosRequest.getNotas());   // Ej: "sin cebolla", "extra queso"
        pedido.setEstado(EstadoPedido.PENDIENTE);     // Todo pedido arranca como PENDIENTE

        // ── PASO 4: Agregar cada producto al pedido ───────────────────────────
        // Recorremos la lista de productos que mandó el cliente en el request
        for (ProductoDTO productoDTO : pedidosRequest.getProductos()) {

            // Buscamos el producto en la base de datos por su ID.
            // Si no existe, lanzamos una excepción que el handler convierte en 400.
            Producto producto = productoRepository.findById(productoDTO.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto no encontrado: " + productoDTO.getId()));

            // Si no se especificó cantidad, asumimos 1 por defecto
            Integer cantidad = productoDTO.getCantidad() != null ? productoDTO.getCantidad() : 1;

            // agregarLinea() crea una LineaPedido y recalcula el total automáticamente
            pedido.agregarLinea(producto, cantidad);
        }

        // ── PASO 5: Persistir el pedido completo ──────────────────────────────
        // Gracias a CascadeType.ALL en Pedido, también se guardan las LineaPedido
        pedidoRepository.save(pedido);

        return mapToResponseDTO(pedido, "Pedido recibido para " + cliente.getNombre());
    }

    // =========================================================
    //  CONSULTAS / LISTADOS
    // =========================================================

    /**
     * Devuelve todos los pedidos con estado PENDIENTE.
     * Usado por el panel admin para ver qué pedidos están esperando ser validados.
     */
    public List<PedidosResponseDTO> listarPedidosPendientes() {
        return pedidoRepository.findByEstado(EstadoPedido.PENDIENTE)
                .stream()
                .map(p -> mapToResponseDTO(p, "Pedido pendiente"))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve TODOS los pedidos sin importar su estado.
     * Usado por el panel admin para tener una vista general.
     */
    public List<PedidosResponseDTO> listarTodosLosPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(p -> mapToResponseDTO(p, "Pedido #" + p.getId()))
                .collect(Collectors.toList());
    }

    // =========================================================
    //  TRANSICIONES DE ESTADO
    // =========================================================

    /**
     * PENDIENTE → VALIDADO
     *
     * El admin revisa el pedido y lo aprueba.
     * Solo se pueden validar pedidos que estén en estado PENDIENTE.
     *
     * @param id ID del pedido a validar
     * @throws RuntimeException si el pedido no existe o no está PENDIENTE
     */
    @Transactional
    public PedidosResponseDTO validarPedido(Long id) {
        Pedido pedido = buscarPedidoOFallar(id);

        // Guardamos la integridad del flujo: no se puede validar algo que ya fue validado
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new RuntimeException("Solo se pueden validar pedidos pendientes");
        }

        pedido.setEstado(EstadoPedido.VALIDADO);
        pedidoRepository.save(pedido);
        return mapToResponseDTO(pedido, "Pedido validado correctamente");
    }

    /**
     * VALIDADO → EN_PREPARACION
     *
     * El pedido pasa a cocina. A partir de acá ya no debería cancelarse
     * porque los cocineros ya empezaron a trabajar.
     *
     * @param id ID del pedido a poner en preparación
     */
    @Transactional
    public PedidosResponseDTO prepararPedido(Long id) {
        Pedido pedido = buscarPedidoOFallar(id);

        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        pedidoRepository.save(pedido);
        return mapToResponseDTO(pedido, "Pedido en preparación");
    }

    /**
     * EN_PREPARACION → LISTO
     *
     * Cocina terminó. El pedido está listo para ser retirado o entregado.
     *
     * @param id ID del pedido a marcar como listo
     */
    @Transactional
    public PedidosResponseDTO marcarListo(Long id) {
        Pedido pedido = buscarPedidoOFallar(id);

        pedido.setEstado(EstadoPedido.LISTO);
        pedidoRepository.save(pedido);
        return mapToResponseDTO(pedido, "Pedido listo para entregar");
    }

    /**
     * LISTO → ENTREGADO
     *
     * El delivery confirmó la entrega. Estado final "feliz" del pedido.
     *
     * @param id ID del pedido a marcar como entregado
     */
    @Transactional
    public PedidosResponseDTO marcarEntregado(Long id) {
        Pedido pedido = buscarPedidoOFallar(id);

        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);
        return mapToResponseDTO(pedido, "Pedido entregado");
    }

    /**
     * CUALQUIER ESTADO → CANCELADO (excepto LISTO y ENTREGADO)
     *
     * Cancela el pedido. No se permite cancelar si ya está listo o entregado
     * porque en ese punto el producto ya fue preparado o despachado.
     *
     * @param id ID del pedido a cancelar
     * @throws RuntimeException si el pedido ya está LISTO o ENTREGADO
     */
    @Transactional
    public PedidosResponseDTO cancelarPedido(Long id) {
        Pedido pedido = buscarPedidoOFallar(id);

        // Bloqueamos la cancelación si el pedido ya no tiene vuelta atrás
        if (pedido.getEstado() == EstadoPedido.LISTO ||
                pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new RuntimeException("No se puede cancelar un pedido ya listo o entregado");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
        return mapToResponseDTO(pedido, "Pedido cancelado correctamente");
    }

    // =========================================================
    //  MÉTODOS PRIVADOS (helpers internos)
    // =========================================================

    /**
     * Busca un pedido por ID o lanza una excepción si no existe.
     *
     * Este helper evita repetir el mismo bloque findById + orElseThrow
     * en cada método de transición de estado.
     *
     * @param id ID del pedido buscado
     * @return el Pedido encontrado
     * @throws RuntimeException si no existe ningún pedido con ese ID
     */
    private Pedido buscarPedidoOFallar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: #" + id));
    }

    /**
     * Convierte un Pedido (entidad de base de datos) en un PedidosResponseDTO
     * (objeto que se envía como respuesta JSON al cliente).
     *
     * Esta conversión se llama "mapeo" o "mapping". Lo hacemos manualmente
     * para tener control total sobre qué datos exponemos hacia afuera.
     *
     * @param pedido  la entidad Pedido con todos sus datos
     * @param mensaje texto de confirmación personalizado según la operación realizada
     * @return DTO listo para serializar a JSON
     */
    private PedidosResponseDTO mapToResponseDTO(Pedido pedido, String mensaje) {
        PedidosResponseDTO response = new PedidosResponseDTO();

        response.setIdPedido(pedido.getId());
        response.setEstado(pedido.getEstado().name());     // Ej: "PENDIENTE", "LISTO"
        response.setPrecioTotal(pedido.getPrecioTotal());
        response.setMensajeConfirmacion(mensaje);
        response.setNotas(pedido.getNotas());
        response.setNombreCliente(pedido.getCliente().getNombre());

        // Mapeamos cada línea del pedido a su DTO correspondiente
        List<ProductoDTO> productosDTO = pedido.getLineas().stream().map(linea -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(linea.getProducto().getId());
            dto.setNombre(linea.getProducto().getNombre());
            dto.setPrecio(linea.getPrecioUnitario());       // Precio al momento del pedido
            dto.setCantidad(linea.getCantidad());
            dto.setSubtotal(linea.getSubtotal());           // cantidad × precio unitario
            return dto;
        }).collect(Collectors.toList());

        response.setProductos(productosDTO);

        return response;
    }
}