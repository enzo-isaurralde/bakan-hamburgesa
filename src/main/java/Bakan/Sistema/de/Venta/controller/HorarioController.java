package Bakan.Sistema.de.Venta.controller;

import Bakan.Sistema.de.Venta.service.HorarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para consultar el estado de apertura del local.
 * Permite saber si está abierto y el horario de cierre.
 */
@RestController
@RequestMapping("/api/horario")
@Tag(name = "Horario", description = "Consulta de estado de apertura del local")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    /**
     * Obtiene el estado actual del local (abierto/cerrado) y el horario de cierre.
     * Endpoint público, no requiere autenticación.
     *
     * @return Mapa con estado booleano y texto descriptivo del horario
     */
    @GetMapping("/estado")
    @Operation(
            summary = "Estado del local",
            description = "Verifica si el local está abierto actualmente y devuelve información " +
                    "sobre el horario de cierre o próxima apertura. " +
                    "Este endpoint es público y no requiere autenticación. " +
                    "Considera el horario de Argentina (America/Argentina/Buenos_Aires)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Estado obtenido exitosamente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            description = "Respuesta con estado del local",
                            example = """
                                    {
                                      "abierto": true,
                                      "cierreTexto": "Cierra 22:00 hs"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<Map<String, Object>> getEstado() {
        boolean abierto = horarioService.estaAbierto();

        LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        DayOfWeek dia = ahora.getDayOfWeek();

        String cierreTexto;

        if (abierto) {
            cierreTexto = switch (dia) {
                case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Cierra 22:00 hs";
                case SATURDAY -> "Cierra 23:00 hs";
                default -> "";
            };
        } else {
            cierreTexto = switch (dia) {
                case SUNDAY -> "Abre el lunes a las 10:00 hs";
                default -> "Abre hoy a las 10:00 hs";
            };
        }

        Map<String, Object> response = new HashMap<>();
        response.put("abierto", abierto);
        response.put("cierreTexto", cierreTexto);

        return ResponseEntity.ok(response);
    }
}