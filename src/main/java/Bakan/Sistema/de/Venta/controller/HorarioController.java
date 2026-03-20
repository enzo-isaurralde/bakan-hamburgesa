package Bakan.Sistema.de.Venta.controller;

import Bakan.Sistema.de.Venta.service.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/horario")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @GetMapping("/estado")
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