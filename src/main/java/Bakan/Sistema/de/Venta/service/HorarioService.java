package Bakan.Sistema.de.Venta.service;

import Bakan.Sistema.de.Venta.exception.HorarioException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Servicio que centraliza la lógica del horario de atención de Bakan.
 *
 * Horario:
 *   - Lunes a Viernes: 10:00 a 22:00
 *   - Sábado:          10:00 a 23:00
 *   - Domingo:         CERRADO
 */
@Service
public class HorarioService {

    private static final LocalTime APERTURA_SEMANA   = LocalTime.of(10, 0);
    private static final LocalTime CIERRE_SEMANA     = LocalTime.of(22, 0);

    private static final LocalTime APERTURA_SABADO   = LocalTime.of(10, 0);
    private static final LocalTime CIERRE_SABADO     = LocalTime.of(23, 0);

    /**
     * Valida si en este momento el local está abierto.
     * Lanza {@link HorarioException} con un mensaje claro si está cerrado.
     */
    public void validarHorario() {
        validarHorario(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));
    }

    /**
     * Overload que acepta un momento específico (facilita los tests unitarios).
     */
    public void validarHorario(LocalDateTime momento) {
        DayOfWeek dia  = momento.getDayOfWeek();
        LocalTime hora = momento.toLocalTime();

        switch (dia) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> {
                if (hora.isBefore(APERTURA_SEMANA) || !hora.isBefore(CIERRE_SEMANA)) {
                    throw new HorarioException(
                            "Lo sentimos, Bakan atiende de lunes a viernes de " +
                                    formatHora(APERTURA_SEMANA) + " a " + formatHora(CIERRE_SEMANA) +
                                    ". Volvé en horario de atención 🍔"
                    );
                }
            }
            case SATURDAY -> {
                if (hora.isBefore(APERTURA_SABADO) || !hora.isBefore(CIERRE_SABADO)) {
                    throw new HorarioException(
                            "Lo sentimos, los sábados Bakan atiende de " +
                                    formatHora(APERTURA_SABADO) + " a " + formatHora(CIERRE_SABADO) +
                                    ". Volvé en horario de atención 🍔"
                    );
                }
            }
            case SUNDAY -> throw new HorarioException(
                    "Lo sentimos, los domingos Bakan está cerrado. " +
                            "¡Te esperamos el lunes desde las " + formatHora(APERTURA_SEMANA) + "! 🍔"
            );
        }
    }

    /**
     * Devuelve true si el local está abierto en este momento (sin lanzar excepción).
     * Útil para exponer un endpoint de estado o para el frontend.
     */
    public boolean estaAbierto() {
        try {
            validarHorario();
            return true;
        } catch (HorarioException e) {
            return false;
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private String formatHora(LocalTime hora) {
        return String.format("%d:%02d hs", hora.getHour(), hora.getMinute());
    }
}