package Bakan.Sistema.de.Venta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Información general de la API
                .info(new Info()
                        .title("Sistema de Ventas Bakan Hamburguesa")
                        .description("API REST para gestión de menú y ventas por WhatsApp")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Bakan Hamburguesa")
                                .email("contacto@bakan.com")
                                .url("https://bakan.com"))
                        .license(new License()
                                .name("Privado")
                                .url("https://bakan.com")))

                // Configuración de seguridad JWT
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))

                // Servidores (opcional)
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local"),
                        new Server().url("https://api.bakan.com").description("Producción")
                ));
    }
}