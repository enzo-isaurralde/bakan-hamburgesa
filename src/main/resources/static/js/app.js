const API_URL = 'http://localhost:8080/api';

let productos = [];
let carrito = {};
let categoriaActual = 'HAMBURGUESAS';

// Cargar productos desde el backend
async function cargarProductos() {
    try {
        const response = await fetch(`${API_URL}/productos`);
        if (!response.ok) throw new Error('Error al cargar productos');
        
        productos = await response.json();
        renderizarProductos();
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('productos-grid').innerHTML = 
            '<p style="text-align: center; color: var(--gris); padding: 40px;">Error al cargar el menú. Intentá recargar la página.</p>';
    }
}

// Renderizar productos
function renderizarProductos() {
    const grid = document.getElementById('productos-grid');
    const productosFiltrados = productos.filter(p => p.categoria === categoriaActual);
    
    if (productosFiltrados.length === 0) {
        grid.innerHTML = '<p style="text-align: center; color: var(--gris); padding: 40px;">No hay productos en esta categoría</p>';
        return;
    }
    
    grid.innerHTML = productosFiltrados.map(p => {
        const enCarrito = carrito[p.id] || 0;
        const agotado = !p.disponible;
        
        return `
            <div class="producto-card ${agotado ? 'agotado' : ''}">
                <div class="producto-imagen">
                    ${p.esNuevo ? '<span class="badge badge-nuevo">NUEVA</span>' : ''}
                    ${p.esPopular && !p.esNuevo ? '<span class="badge badge-popular">MÁS VENDIDA</span>' : ''}
                    ${p.emoji || '🍔'}
                </div>
                <div class="producto-info">
                    <div class="producto-header">
                        <h3 class="producto-nombre">${p.nombre}</h3>
                        <span class="producto-precio">$${p.precio.toLocaleString()}</span>
                    </div>
                    <p class="producto-desc">${p.descripcion}</p>
                    
                    ${agotado 
                        ? '<button class="btn-stock agotado">Sin Stock</button>'
                        : `<button class="btn-stock" onclick="agregarAlCarrito(${p.id})">
                            ${enCarrito > 0 ? `En Carrito (${enCarrito})` : 'En Stock'}
                           </button>`
                    }
                </div>
            </div>
        `;
    }).join('');
}

// Filtrar por categoría
function filtrarCategoria(categoria) {
    categoriaActual = categoria;
    
    // Actualizar botones
    document.querySelectorAll('.cat-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.textContent.includes(categoria === 'HAMBURGUESAS' ? 'Hamburguesas' : 
                                      categoria === 'PAPAS' ? 'Papas' : 'Bebidas')) {
            btn.classList.add('active');
        }
    });
    
    renderizarProductos();
}

// Agregar al carrito
function agregarAlCarrito(id) {
    const producto = productos.find(p => p.id === id);
    if (!producto || !producto.disponible) return;
    
    carrito[id] = (carrito[id] || 0) + 1;
    
    // Re-renderizar para actualizar el botón
    renderizarProductos();
    actualizarCarritoFlotante();
}

// Actualizar carrito flotante
function actualizarCarritoFlotante() {
    const items = Object.values(carrito).reduce((a, b) => a + b, 0);
    const total = Object.entries(carrito).reduce((sum, [id, cantidad]) => {
        const producto = productos.find(p => p.id == id);
        return sum + (producto ? producto.precio * cantidad : 0);
    }, 0);
    
    const carritoFlotante = document.getElementById('carrito-flotante');
    const carritoTotal = document.getElementById('carrito-total');
    const carritoItems = document.getElementById('carrito-items');
    
    if (items > 0) {
        carritoFlotante.classList.add('visible');
        carritoTotal.textContent = `$${total.toLocaleString()}`;
        carritoItems.textContent = `${items} ${items === 1 ? 'item' : 'items'}`;
    } else {
        carritoFlotante.classList.remove('visible');
    }
}

// Abrir modal
function abrirModal() {
    const items = Object.keys(carrito).length;
    if (items === 0) return;
    
    document.getElementById('modal-overlay').classList.add('active');
    renderizarModalCarrito();
}

// Cerrar modal
function cerrarModal(event) {
    if (!event || event.target.id === 'modal-overlay') {
        document.getElementById('modal-overlay').classList.remove('active');
    }
}

// Renderizar modal
function renderizarModalCarrito() {
    const lista = document.getElementById('lista-carrito');
    const items = Object.entries(carrito);
    
    let subtotal = 0;
    
    lista.innerHTML = items.map(([id, cantidad]) => {
        const producto = productos.find(p => p.id == id);
        if (!producto) return '';
        
        const itemTotal = producto.precio * cantidad;
        subtotal += itemTotal;
        
        return `
            <div class="item-carrito">
                <div class="item-imagen">${producto.emoji || '🍔'}</div>
                <div class="item-info">
                    <div class="item-nombre">${producto.nombre}</div>
                    <div class="item-detalles">$${producto.precio.toLocaleString()} c/u</div>
                </div>
                <div class="item-cantidad">
                    <button class="btn-mini" onclick="modificarCantidad(${id}, -1)">−</button>
                    <span>${cantidad}</span>
                    <button class="btn-mini" onclick="modificarCantidad(${id}, 1)">+</button>
                </div>
                <div class="item-precio">$${itemTotal.toLocaleString()}</div>
            </div>
        `;
    }).join('');
    
    // Mostrar resumen
    document.getElementById('resumen-pedido').style.display = 'block';
    document.getElementById('formulario-pedido').style.display = 'block';
    document.getElementById('resumen-subtotal').textContent = `$${subtotal.toLocaleString()}`;
    document.getElementById('resumen-total').textContent = `$${(subtotal + 500).toLocaleString()}`;
}

// Modificar cantidad en modal
function modificarCantidad(id, cambio) {
    const nuevaCantidad = (carrito[id] || 0) + cambio;
    
    if (nuevaCantidad <= 0) {
        delete carrito[id];
    } else {
        carrito[id] = nuevaCantidad;
    }
    
    renderizarProductos();
    actualizarCarritoFlotante();
    renderizarModalCarrito();
    
    // Cerrar si quedó vacío
    if (Object.keys(carrito).length === 0) {
        cerrarModal();
    }
}

// Enviar por WhatsApp
async function enviarWhatsApp() {
    const nombre = document.getElementById('cliente-nombre').value.trim();
    if (!nombre) {
        alert('Por favor ingresá tu nombre');
        return;
    }
    
    const notas = document.querySelector('.notas-pedido').value;
    const items = Object.entries(carrito);
    
    // Crear pedido en el backend primero
    const productosPedido = items.map(([id, cantidad]) => ({
        id: parseInt(id),
        cantidad: cantidad
    }));
    
    try {
        const response = await fetch(`${API_URL}/pedidos`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                cliente: { nombre: nombre },
                productos: productosPedido
            })
        });
        
        if (!response.ok) throw new Error('Error al crear pedido');
        
        const pedido = await response.json();
        
        // Generar mensaje de WhatsApp
        let mensaje = `🍔 *PEDIDO BAKAN BURGER - #${pedido.idPedido}*\n`;
        mensaje += "━━━━━━━━━━━━━━━━━━━━\n\n";
        
        let total = 0;
        pedido.productos.forEach((p, index) => {
            mensaje += `*${index + 1}. ${p.nombre}* x${p.cantidad}\n`;
            mensaje += `   💰 $${p.subtotal.toLocaleString()}\n\n`;
            total += p.subtotal;
        });
        
        mensaje += "━━━━━━━━━━━━━━━━━━━━\n";
        mensaje += `*Subtotal:* $${total.toLocaleString()}\n`;
        mensaje += `*Envío:* $500\n`;
        mensaje += `*TOTAL:* $${(total + 500).toLocaleString()}\n\n`;
        
        if (notas) {
            mensaje += `📝 *Notas:* ${notas}\n\n`;
        }
        
        mensaje += `👤 *Cliente:* ${nombre}\n`;
        mensaje += "📞 *Teléfono:* ___________\n";
        mensaje += "🏠 *Dirección:* ___________\n";
        mensaje += "💳 *Pago:* Efectivo/Transferencia\n";
        
        // Número de WhatsApp del local (cambiar por el real)
        const telefonoLocal = "5491123456789";
        const url = `https://wa.me/${telefonoLocal}?text=${encodeURIComponent(mensaje)}`;
        
        window.open(url, '_blank');
        
        // Limpiar carrito
        carrito = {};
        renderizarProductos();
        actualizarCarritoFlotante();
        cerrarModal();
        
    } catch (error) {
        alert('Error al procesar el pedido. Intentá de nuevo.');
        console.error(error);
    }
}

// Inicializar
document.addEventListener('DOMContentLoaded', cargarProductos);