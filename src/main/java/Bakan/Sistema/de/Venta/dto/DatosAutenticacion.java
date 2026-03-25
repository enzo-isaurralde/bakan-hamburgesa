package Bakan.Sistema.de.Venta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para recibir credenciales de autenticación.
 */
@Schema(description = "Datos de autenticación para login")
public record DatosAutenticacion(

        @Schema(
                description = "Nombre de usuario",
                example = "admin",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El username es obligatorio")
        String username,

        @Schema(
                description = "Contraseña del usuario",
                example = "admin123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}