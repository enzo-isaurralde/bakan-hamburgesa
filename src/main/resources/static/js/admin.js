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
        const response = await fetch(`${API_URL}/productos/todos`);
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
        <div class="pedido-cliente">${pedido.nombreCliente || 'Sin nombre'}</div>
        ${pedido.notas ? `<div class="pedido-notas">📝 ${pedido.notas}</div>` : ''}
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
        mostrarNotificacion(`${data.disponible ? '✓ En Stock' : '✕ Sin Stock — producto desactivado'}`,
                            data.disponible ? 'success' : 'error');
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
                    ${p.disponible ? '✓ En Stock' : '✕ Sin Stock'}
                </span>
                <div style="display: flex; align-items: center; gap: 10px;">
                    <span style="font-size: 0.8rem; color: ${p.disponible ? 'var(--text-light)' : '#ef4444'};">
                        ${p.disponible ? 'Desactivar' : 'Sin stock'}
                    </span>
                    <div class="toggle-switch ${p.disponible ? 'active' : ''}"
                         onclick="toggleDisponibilidad(${p.id}, ${p.disponible})">
                    </div>
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
async function buscarPedido() {
    const id = document.getElementById('input-buscar-pedido').value.trim();
    const resultado = document.getElementById('resultado-busqueda');

    if (!id) {
        resultado.innerHTML = '<p style="color:var(--warning);">Ingresá un número de pedido.</p>';
        return;
    }

    try {
        const response = await fetch(`${API_URL}/pedidos/${id}`);

        if (!response.ok) {
            resultado.innerHTML = `<p style="color:var(--danger);">❌ Pedido #${id} no encontrado.</p>`;
            return;
        }

        const pedido = await response.json();

        const colores = {
            PENDIENTE: 'var(--warning)',
            VALIDADO: 'var(--info)',
            EN_PREPARACION: 'var(--primary)',
            LISTO: 'var(--success)',
            ENTREGADO: '#9B59B6',
            CANCELADO: 'var(--danger)'
        };

        resultado.innerHTML = `
            <div class="resultado-card">
                <span class="pedido-id">#${pedido.idPedido}</span>
                <span class="pedido-cliente">👤 ${pedido.nombreCliente || 'Sin nombre'}</span>
                <span style="color:${colores[pedido.estado] || 'var(--text)'}; font-weight:700;">
                    ● ${pedido.estado.replace('_', ' ')}
                </span>
                <span style="color:var(--success); font-weight:700;">
                    $${formatearPrecio(pedido.precioTotal)}
                </span>
            </div>
        `;

    } catch (error) {
        resultado.innerHTML = '<p style="color:var(--danger);">Error al buscar el pedido.</p>';
    }
}

function limpiarBusqueda() {
    document.getElementById('input-buscar-pedido').value = '';
    document.getElementById('resultado-busqueda').innerHTML = '';
}