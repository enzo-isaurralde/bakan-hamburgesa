const API_URL = 'http://localhost:8080/api';

let pedidos = [];
let productos = [];

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    cargarPedidos();
    cargarProductos();
    setInterval(cargarPedidos, 10000); // Refresh cada 10 segundos
});

// Cargar pedidos
async function cargarPedidos() {
    try {
        const response = await fetch(`${API_URL}/pedidos`);
        if (!response.ok) throw new Error('Error al cargar pedidos');

        pedidos = await response.json();
        renderizarPedidos();
        actualizarEstadisticas();
    } catch (error) {
        console.error('Error:', error);
        mostrarNotificacion('Error al cargar pedidos', 'error');
    }
}

// Cargar productos
async function cargarProductos() {
    try {
        const response = await fetch(`${API_URL}/productos`);
        if (!response.ok) throw new Error('Error al cargar productos');

        productos = await response.json();
        renderizarProductosAdmin();
    } catch (error) {
        console.error('Error:', error);
    }
}

// Renderizar pedidos en kanban
function renderizarPedidos(filtro = 'todos') {
    // Limpiar columnas
    ['PENDIENTE', 'VALIDADO', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'CANCELADO'].forEach(estado => {
        const columna = document.getElementById(`column-${estado}`);
        if (columna) columna.innerHTML = '';
    });

    // Filtrar
    const pedidosFiltrados = filtro === 'todos'
        ? pedidos
        : pedidos.filter(p => p.estado === filtro);

    // Renderizar
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
    const productosHtml = (pedido.productos || []).map(p => `
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
        <div class="pedido-productos">${productosHtml}</div>
        <div class="pedido-total">Total: $${formatearPrecio(pedido.precioTotal)}</div>
        <div class="pedido-acciones">${acciones}</div>
    `;

    return div;
}

// Obtener acciones según estado
function obtenerAcciones(estado, idPedido) {
    const acciones = {
        'PENDIENTE': `
            <button class="btn btn-success btn-sm" onclick="cambiarEstado(${idPedido}, 'validar')">✓ Validar</button>
            <button class="btn btn-danger btn-sm" onclick="cambiarEstado(${idPedido}, 'cancelar')">✕ Cancelar</button>
        `,
        'VALIDADO': `
            <button class="btn btn-primary btn-sm" onclick="cambiarEstado(${idPedido}, 'preparar')">👨‍🍳 Cocina</button>
            <button class="btn btn-danger btn-sm" onclick="cambiarEstado(${idPedido}, 'cancelar')">✕ Cancelar</button>
        `,
        'EN_PREPARACION': `
            <button class="btn btn-success btn-sm" onclick="cambiarEstado(${idPedido}, 'listo')">✓ Listo</button>
        `,
        'LISTO': `
            <button class="btn btn-secondary btn-sm" onclick="cambiarEstado(${idPedido}, 'entregar')">🛵 Entregar</button>
        `,
        'ENTREGADO': `<span class="estado-badge estado-disponible">✓ Completado</span>`,
        'CANCELADO': `<span class="estado-badge estado-agotado">✕ Cancelado</span>`
    };
    return acciones[estado] || '';
}

// Cambiar estado
async function cambiarEstado(idPedido, accion) {
    try {
        const response = await fetch(`${API_URL}/pedidos/${idPedido}/${accion}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) throw new Error('Error al actualizar');

        const data = await response.json();
        mostrarNotificacion(data.mensajeConfirmacion, 'success');
        cargarPedidos();

    } catch (error) {
        mostrarNotificacion('Error al actualizar pedido', 'error');
    }
}

// Toggle disponibilidad producto
async function toggleDisponibilidad(idProducto, disponibleActual) {
    try {
        const response = await fetch(`${API_URL}/productos/${idProducto}/toggle`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) throw new Error('Error al actualizar');

        const data = await response.json();
        mostrarNotificacion(`Producto ${data.disponible ? 'disponible' : 'agotado'}`, 'success');
        cargarProductos();

    } catch (error) {
        mostrarNotificacion('Error al actualizar producto', 'error');
    }
}

// Renderizar productos en admin
function renderizarProductosAdmin() {
    const container = document.getElementById('productos-admin-list');
    if (!container) return;

    container.innerHTML = productos.map(p => `
        <div class="producto-admin-card">
            <div class="producto-admin-header">
                <div>
                    <div class="producto-admin-nombre">${p.emoji || '🍔'} ${p.nombre}</div>
                    <div style="color: var(--text-light); font-size: 0.875rem;">${p.categoria}</div>
                </div>
                <div class="producto-admin-precio">$${formatearPrecio(p.precio)}</div>
            </div>
            <div class="producto-admin-desc">${p.descripcion || 'Sin descripción'}</div>
            <div class="producto-admin-estado">
                <span class="estado-badge ${p.disponible ? 'estado-disponible' : 'estado-agotado'}">
                    ${p.disponible ? '✓ En Stock' : '✕ Agotado'}
                </span>
                <div class="toggle-switch ${p.disponible ? 'active' : ''}"
                     onclick="toggleDisponibilidad(${p.id}, ${p.disponible})">
                </div>
            </div>
        </div>
    `).join('');
}

// Actualizar estadísticas
function actualizarEstadisticas() {
    const estados = ['PENDIENTE', 'VALIDADO', 'EN_PREPARACION', 'LISTO', 'ENTREGADO', 'CANCELADO'];
    estados.forEach(estado => {
        const count = pedidos.filter(p => p.estado === estado).length;
        const elemento = document.getElementById(`count-${estado.toLowerCase()}`);
        if (elemento) elemento.textContent = count;
    });
}

// Filtrar pedidos
function filtrarPedidos(filtro) {
    document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    renderizarPedidos(filtro);
}

// Utilidades
function formatearPrecio(precio) {
    return (precio || 0).toLocaleString('es-AR');
}

function calcularTiempo(fecha) {
    const diff = Date.now() - new Date(fecha).getTime();
    const minutos = Math.floor(diff / 60000);
    if (minutos < 1) return 'Ahora';
    if (minutos < 60) return `${minutos}m`;
    const horas = Math.floor(minutos / 60);
    return `${horas}h ${minutos % 60}m`;
}

function mostrarNotificacion(mensaje, tipo = 'info') {
    const notif = document.createElement('div');
    notif.className = `notificacion ${tipo}`;
    notif.textContent = mensaje;
    document.body.appendChild(notif);
    setTimeout(() => notif.remove(), 3000);
}