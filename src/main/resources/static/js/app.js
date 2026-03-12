// Configuración
const API_URL = 'http://localhost:8080/api/pedidos';

// Estado de la aplicación
let pedidos = [];
let productosDisponibles = [];

// Inicialización
document.addEventListener('DOMContentLoaded', () => {
    cargarPedidos();
    configurarFiltros();
    configurarFormulario();

    // Auto-refresh cada 30 segundos
    setInterval(cargarPedidos, 30000);
});

// Cargar pedidos desde la API
async function cargarPedidos() {
    try {
        const response = await fetch(`${API_URL}`);
        if (!response.ok) throw new Error('Error al cargar pedidos');

        pedidos = await response.json();
        renderizarPedidos();
        actualizarEstadisticas();
    } catch (error) {
        console.error('Error:', error);
        mostrarNotificacion('Error al cargar pedidos', 'error');
    }
}

// Renderizar pedidos en el kanban
function renderizarPedidos(filtro = 'todos') {
    // Limpiar columnas
    ['PENDIENTE', 'VALIDADO', 'EN_PREPARACION', 'LISTO'].forEach(estado => {
        document.getElementById(`column-${estado}`).innerHTML = '';
    });

    // Filtrar pedidos
    const pedidosFiltrados = filtro === 'todos'
        ? pedidos
        : pedidos.filter(p => p.estado === filtro);

    // Renderizar cada pedido
    pedidosFiltrados.forEach(pedido => {
        const card = crearPedidoCard(pedido);
        const columna = document.getElementById(`column-${pedido.estado}`);
        if (columna) columna.appendChild(card);
    });
}

// Crear card de pedido
function crearPedidoCard(pedido) {
    const div = document.createElement('div');
    div.className = 'pedido-card';
    div.setAttribute('data-estado', pedido.estado);
    div.setAttribute('data-id', pedido.idPedido);

    const tiempo = calcularTiempo(pedido.fechaPedido);
    const productosHtml = pedido.productos.map(p => `
        <div class="producto-item">
            <span>${p.cantidad}x ${p.nombre}</span>
            <span>$${formatearPrecio(p.subtotal)}</span>
        </div>
    `).join('');

    const acciones = obtenerAcciones(pedido.estado, pedido.idPedido);

    div.innerHTML = `
        <div class="pedido-header">
            <span class="pedido-id">#${pedido.idPedido}</span>
            <span class="pedido-tiempo">${tiempo}</span>
        </div>
        <div class="pedido-cliente">${pedido.cliente?.nombre || 'Sin nombre'}</div>
        <div class="pedido-productos">
            ${productosHtml}
        </div>
        <div class="pedido-total">Total: $${formatearPrecio(pedido.precioTotal)}</div>
        <div class="pedido-acciones">
            ${acciones}
        </div>
    `;

    return div;
}

// Obtener botones de acción según estado
function obtenerAcciones(estado, id) {
    const acciones = {
        'PENDIENTE': `
            <button class="btn btn-success btn-sm" onclick="cambiarEstado(${id}, 'validar')">✓ Validar</button>
            <button class="btn btn-danger btn-sm" onclick="cambiarEstado(${id}, 'cancelar')">✕ Cancelar</button>
        `,
        'VALIDADO': `
            <button class="btn btn-primary btn-sm" onclick="cambiarEstado(${id}, 'preparar')">👨‍🍳 A Cocina</button>
            <button class="btn btn-danger btn-sm" onclick="cambiarEstado(${id}, 'cancelar')">✕ Cancelar</button>
        `,
        'EN_PREPARACION': `
            <button class="btn btn-success btn-sm" onclick="cambiarEstado(${id}, 'listo')">✓ Listo</button>
        `,
        'LISTO': `
            <button class="btn btn-secondary btn-sm" onclick="cambiarEstado(${id}, 'entregar')">🛵 Entregar</button>
        `
    };
    return acciones[estado] || '';
}

// Cambiar estado de pedido
async function cambiarEstado(id, accion) {
    try {
        const response = await fetch(`${API_URL}/${id}/${accion}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) throw new Error('Error al actualizar');

        const data = await response.json();
        mostrarNotificacion(data.mensajeConfirmacion);
        cargarPedidos(); // Recargar

    } catch (error) {
        console.error('Error:', error);
        mostrarNotificacion('Error al actualizar pedido', 'error');
    }
}

// Actualizar estadísticas
function actualizarEstadisticas() {
    const estados = ['PENDIENTE', 'VALIDADO', 'EN_PREPARACION', 'LISTO', 'ENTREGADO'];
    estados.forEach(estado => {
        const count = pedidos.filter(p => p.estado === estado).length;
        const elemento = document.getElementById(`count-${estado.toLowerCase()}`);
        if (elemento) elemento.textContent = count;
    });
}

// Configurar filtros
function configurarFiltros() {
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            renderizarPedidos(btn.dataset.filter);
        });
    });
}

// Modal nuevo pedido
function mostrarModalNuevoPedido() {
    document.getElementById('modal-pedido').classList.add('active');
    cargarProductosDisponibles();
}

function cerrarModal() {
    document.getElementById('modal-pedido').classList.remove('active');
}

// Cargar productos para el formulario
async function cargarProductosDisponibles() {
    // Necesitamos un endpoint de productos, por ahora hardcodeado
    productosDisponibles = [
        { id: 1, nombre: 'Bakan Clásica', precio: 8500 },
        { id: 2, nombre: 'Bakan Doble', precio: 12500 },
        { id: 3, nombre: 'Bakan Premium', precio: 18500 },
        { id: 4, nombre: 'Bakan Vegana', precio: 9500 }
    ];
    agregarProducto();
}

function agregarProducto() {
    const container = document.getElementById('productos-container');
    const row = document.createElement('div');
    row.className = 'producto-row';

    const options = productosDisponibles.map(p =>
        `<option value="${p.id}" data-precio="${p.precio}">${p.nombre} - $${formatearPrecio(p.precio)}</option>`
    ).join('');

    row.innerHTML = `
        <select class="producto-select" onchange="calcularTotal()">
            <option value="">Seleccionar producto</option>
            ${options}
        </select>
        <input type="number" class="producto-cantidad" value="1" min="1" onchange="calcularTotal()">
        <button type="button" class="btn btn-danger btn-sm" onclick="this.parentElement.remove(); calcularTotal()">✕</button>
    `;

    container.appendChild(row);
}

function calcularTotal() {
    let total = 0;
    document.querySelectorAll('.producto-row').forEach(row => {
        const select = row.querySelector('.producto-select');
        const cantidad = row.querySelector('.producto-cantidad');
        if (select.value && cantidad.value) {
            const precio = parseFloat(select.selectedOptions[0].dataset.precio);
            total += precio * parseInt(cantidad.value);
        }
    });
    document.getElementById('total-pedido').textContent = formatearPrecio(total);
}

// Configurar formulario
function configurarFormulario() {
    document.getElementById('form-pedido').addEventListener('submit', async (e) => {
        e.preventDefault();

        const productos = [];
        document.querySelectorAll('.producto-row').forEach(row => {
            const select = row.querySelector('.producto-select');
            const cantidad = row.querySelector('.producto-cantidad');
            if (select.value) {
                productos.push({
                    id: parseInt(select.value),
                    cantidad: parseInt(cantidad.value)
                });
            }
        });

        const pedido = {
            cliente: { nombre: document.getElementById('cliente-nombre').value },
            productos: productos
        };

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(pedido)
            });

            if (!response.ok) throw new Error('Error al crear');

            const data = await response.json();
            mostrarNotificacion(data.mensajeConfirmacion);
            cerrarModal();
            cargarPedidos();

        } catch (error) {
            mostrarNotificacion('Error al crear pedido', 'error');
        }
    });
}

// Utilidades
function formatearPrecio(precio) {
    return precio.toLocaleString('es-AR');
}

function calcularTiempo(fecha) {
    const diff = Date.now() - new Date(fecha).getTime();
    const minutos = Math.floor(diff / 60000);
    if (minutos < 1) return 'Ahora';
    if (minutos < 60) return `${minutos}m`;
    const horas = Math.floor(minutos / 60);
    return `${horas}h ${minutos % 60}m`;
}

function mostrarNotificacion(mensaje, tipo = 'success') {
    const notif = document.createElement('div');
    notif.className = `notificacion ${tipo}`;
    notif.textContent = mensaje;
    document.body.appendChild(notif);
    setTimeout(() => notif.remove(), 3000);
}