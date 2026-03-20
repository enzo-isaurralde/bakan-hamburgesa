const API_URL = 'http://localhost:8080/api';

let productos = [];
let carrito = {};
let categoriaActual = 'HAMBURGUESAS';
let localAbierto = false;

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
                   ${p.imagenUrl
                       ? `<img src="${p.imagenUrl}" alt="${p.nombre}" style="width:100%; height:100%; object-fit:cover; position:absolute; top:0; left:0; border-radius: 20px 20px 0 0;">`
                       : (p.emoji || '🍔')
                   }
               </div>
                <div class="producto-info">
                    <div class="producto-header">
                        <h3 class="producto-nombre">${p.nombre}</h3>
                        <span class="producto-precio">$${p.precio.toLocaleString()}</span>
                    </div>
                    <p class="producto-desc">${p.descripcion || ''}</p>
                    
                   ${agotado
                       ? '<button class="btn-stock agotado" disabled>Sin Stock</button>'
                       : !localAbierto
                           ? '<button class="btn-stock" disabled style="background:#1a1a1a; color:#ef4444; border: 1px solid #ef4444; cursor:not-allowed;">Local cerrado</button>'
                           : `<button class="btn-stock" onclick="agregarAlCarrito(${p.id})">
                               ${enCarrito > 0 ? `En Carrito (${enCarrito})` : 'En Stock'}
                              </button>`
                   }
                </div>
            </div>
        `;
    }).join('');
}

function filtrarCategoria(categoria) {
    categoriaActual = categoria;

    // Scroll al título "Nuestro Menú"
    const menuTitulo = document.getElementById('menu-titulo');
    const headerHeight = document.querySelector('.header').offsetHeight;
    const categoriasHeight = document.querySelector('.categorias').offsetHeight;
    const offsetTop = menuTitulo.getBoundingClientRect().top + window.scrollY;
    window.scrollTo({
        top: offsetTop - headerHeight - categoriasHeight - 10,
        behavior: 'smooth'
    });

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
                <div class="item-imagen">
                    ${producto.imagenUrl
                        ? `<img src="${producto.imagenUrl}" alt="${producto.nombre}" style="width:100%; height:100%; object-fit:cover; border-radius:8px;">`
                        : (producto.emoji || '🍔')
                    }
                </div>
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
    document.getElementById('resumen-total').textContent = `$${subtotal.toLocaleString()}`;
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
// Enviar pedido por WhatsApp
async function enviarWhatsApp() {
    const nombre = document.getElementById('cliente-nombre').value.trim();
    if (!nombre) {
        mostrarMensajeError('Por favor ingresá tu nombre');
        return;
    }

    const notas = document.querySelector('.notas-pedido[rows]').value;
    const items = Object.entries(carrito);

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
                productos: productosPedido,
                notas: notas
            })
        });

        if (!response.ok) {
            // Capturamos el JSON de error y mostramos el mensaje
            const err = await response.json();
            mostrarMensajeError(err.mensaje || 'Error al procesar el pedido');
            return;
        }

        const pedido = await response.json();

        // Si todo salió bien, mostramos mensaje de éxito
        mostrarMensajeExito('Pedido enviado correctamente ✅');

        // ... resto de tu código para abrir WhatsApp ...
        let mensaje = `🍔 *PEDIDO BAKAN BURGER - #${pedido.idPedido}*\n`;
        mensaje += "━━━━━━━━━━━━━━━━━━━━\n\n";

        let total = 0;
        pedido.productos.forEach((p, index) => {
            mensaje += `*${index + 1}. ${p.nombre}* x${p.cantidad}\n`;
            mensaje += `   💰 $${p.subtotal.toLocaleString()}\n\n`;
            total += p.subtotal;
        });

        mensaje += "━━━━━━━━━━━━━━━━━━━━\n";
        mensaje += `*TOTAL:* $${total.toLocaleString()}\n\n`;
        mensaje += `👤 *Cliente:* ${nombre}\n`;

        if (notas) {
            mensaje += `📝 *Notas:* ${notas}\n`;
        }

        mensaje += "💳 *Pago:* Efectivo/Transferencia\n";

        const telefonoLocal = "5491123456789";
        const url = `https://wa.me/${telefonoLocal}?text=${encodeURIComponent(mensaje)}`;

        window.open(url, '_blank');

        carrito = {};
        renderizarProductos();
        actualizarCarritoFlotante();
        cerrarModal();

    } catch (error) {
        mostrarMensajeError('Error al procesar el pedido. Intentá de nuevo.');
        console.error(error);
    }
}

// Funciones auxiliares para mostrar mensajes
function mostrarMensajeError(mensaje) {
    const contenedor = document.getElementById("mensaje-pedido");
    contenedor.innerText = mensaje;
    contenedor.classList.remove("exito");
    contenedor.classList.add("error");
}

function mostrarMensajeExito(mensaje) {
    const contenedor = document.getElementById("mensaje-pedido");
    contenedor.innerText = mensaje;
    contenedor.classList.remove("error");
    contenedor.classList.add("exito");
}


// Función para actualizar el estado del horario
async function actualizarEstadoHorario() {
    try {
        // ── MODO TEST ──────────────────────────────────────────
        // const data = { abierto: false, cierreTexto: 'Abre hoy a las 10:00 hs' };
        // const data = { abierto: true,  cierreTexto: 'Cierra 22:00 hs' };
        // ── FIN MODO TEST ──────────────────────────────────────

        const res = await fetch(`${API_URL}/horario/estado`);
        const data = await res.json();

        const estadoEl  = document.getElementById('estado-local');
        const horarioEl = document.getElementById('horario-cierre');

        if (data.abierto) {
            localAbierto = true;
            estadoEl.textContent = 'Abierto ahora';
            estadoEl.style.color = '#22c55e';
            document.documentElement.style.setProperty('--estado-color', '#22c55e');
        } else {
            localAbierto = false;
            estadoEl.textContent = 'Cerrado';
            estadoEl.style.color = '#ef4444';
            document.documentElement.style.setProperty('--estado-color', '#ef4444');
        }

        horarioEl.textContent = data.cierreTexto;

    } catch (error) {
        // Si el backend no responde, asumimos abierto para no bloquear la página
        console.error('Error al obtener estado del horario:', error);
        localAbierto = true;
        document.getElementById('estado-local').textContent = 'Estado desconocido';
    } finally {
        // Siempre renderiza los productos, pase lo que pase
        renderizarProductos();
    }
}
document.addEventListener('DOMContentLoaded', actualizarEstadoHorario);
setInterval(actualizarEstadoHorario, 5 * 60 * 1000); // refresca cada 5 min

        


// Inicializar
document.addEventListener('DOMContentLoaded', cargarProductos);