package Bakan.Sistema.de.Venta.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para respuesta de autenticación exitosa.
 * Contiene el token JWT y metadatos asociados.
 */
@Schema(description = "Respuesta de autenticación exitosa con token JWT")
public record DatosTokenJWT(

        @Schema(
                description = "Token JWT para autenticación en endpoints protegidos. " +
                        "Incluir en header Authorization como: Bearer {token}",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlzcyI6ImJha2FuLWFwaSIsImV4cCI6MTcxMTM4MjQwMCwicm9sIjoiQURNSU4ifQ.xxxxxx",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String token,

        @Schema(
                description = "Tipo de token. Siempre 'Bearer' para este sistema.",
                example = "Bearer",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String tipo,

        @Schema(
                description = "Tiempo de validez del token en segundos desde su emisión. " +
                        "Por defecto 7200 segundos (2 horas).",
                example = "7200",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long expiraEn

) {}