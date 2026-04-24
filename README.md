# 🍔 Bakan Hamburgesa
Sistema de gestión para una hamburguesería, desarrollado con **Java + Spring Boot** y frontend integrado con **HTML, CSS y JavaScript**.

<img width="1920" height="1080" alt="Captura de pantalla 2026-03-27 123014" src="https://github.com/user-attachments/assets/f0e7c0aa-e0cf-4cb0-96cb-1feb1515cae1" />
<img width="1920" height="1080" alt="Captura de pantalla 2026-03-27 123723" src="https://github.com/user-attachments/assets/fb6acd0b-00e0-4be3-9dfb-4f75ae85128d" />
<img width="1920" height="1080" alt="Captura de pantalla 2026-03-27 123824" src="https://github.com/user-attachments/assets/bf5eb06b-4a31-4719-9f5b-5b5c0acf7308" />





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

- [ ] Dockerizar la aplicación
- [ ] Tests unitarios e integración

## 🚀 Demo y video técnico 
https://drive.google.com/file/d/1P_M95yKMAtPGoH5G3pRB4f5rf8BtMALg/view

https://drive.google.com/file/d/17LvLyhXgnrUfNELZWm_oa3vil_EBJlQE/view

## 👤 Autor

**Enzo Ariel Isaurralde**  
[LinkedIn](https://www.linkedin.com/in/enzo-ariel-isaurralde/) · [GitHub](https://github.com/enzo-isaurralde)

