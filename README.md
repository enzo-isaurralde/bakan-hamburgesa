# 🍔 Bakan Hamburgesa

Sistema de gestión para una hamburguesería, desarrollado con **Java + Spring Boot** y frontend integrado con **HTML, CSS y JavaScript**.

## 📋 Descripción

Bakan es una aplicación web fullstack orientada a la administración interna de una hamburguesería. Permite gestionar el menú, registrar y buscar pedidos, y controlar los horarios de atención del local.

## ✨ Funcionalidades

- 🍟 **Gestión de productos/menú** — Alta, baja y modificación de productos disponibles
- 📦 **Sistema de pedidos** — Registro y seguimiento de pedidos
- 🔍 **Buscador de pedidos** — Búsqueda y filtrado de pedidos existentes
- 🕐 **Gestión de horarios** — Configuración de horarios de atención del local
- 🔐 **Seguridad** — Autenticación y autorización con Spring Security *(en desarrollo)*

## 🛠️ Tecnologías utilizadas

| Capa | Tecnología |
|------|------------|
| Backend | Java, Spring Boot, Spring Security |
| ORM | Hibernate / JPA |
| Base de datos | MySQL / PostgreSQL |
| Migraciones | Flyway |
| Utilidades | Lombok |
| Frontend | HTML, CSS, JavaScript |

## 🗄️ Base de datos

Las migraciones de base de datos son gestionadas con **Flyway**, lo que garantiza un control de versiones del esquema de la base de datos.

## 🚀 Cómo ejecutar el proyecto

### Requisitos previos

- Java 17+
- Maven
- MySQL o PostgreSQL instalado y corriendo

### Pasos

1. Clonar el repositorio:
```bash
git clone https://github.com/enzo-isaurralde/bakan-hamburgesa.git
cd bakan-hamburgesa
```

2. Configurar la base de datos en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bakan_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

3. Ejecutar el proyecto:
```bash
./mvnw spring-boot:run
```

4. Acceder en el navegador:
```
http://localhost:8080
```

## 📁 Estructura del proyecto

```
src/
├── main/
│   ├── java/
│   │   └── Bakan.Sistema.de.Venta/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── exception/
│   │       ├── infra.security/    ← Configuración Spring Security
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       └── SistemaDeVentaApplication.java
│   └── resources/
│       ├── db.migration/          ← Scripts Flyway
│       ├── static/
│       │   ├── css/
│       │   │   ├── admin.css
│       │   │   └── styles.css
│       │   ├── img.productos/
│       │   ├── js/
│       │   │   ├── admin.js
│       │   │   └── app.js
│       │   ├── admin.html         ← Vista administrador
│       │   ├── cocina.html        ← Vista cocina
│       │   └── index.html         ← Vista principal
│       └── application.properties
```

## 🔜 Próximas mejoras

- [ ] Implementación completa de roles con Spring Security (admin / empleado)
- [ ] Dockerizar la aplicación
- [ ] Tests unitarios e integración

## 👤 Autor

**Enzo Ariel Isaurralde**  
[LinkedIn](https://www.linkedin.com/in/enzo-ariel-isaurralde/) · [GitHub](https://github.com/enzo-isaurralde)
