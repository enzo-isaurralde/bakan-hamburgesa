package Bakan.Sistema.de.Venta.exception;

/**
 * Excepción lanzada cuando se intenta crear un pedido
 * fuera del horario de atención del local.
 */
public class HorarioException extends RuntimeException {

    public HorarioException(String mensaje) {
        super(mensaje);
    }
}