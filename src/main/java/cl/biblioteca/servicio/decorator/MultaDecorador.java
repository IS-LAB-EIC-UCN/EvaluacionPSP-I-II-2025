package cl.biblioteca.servicio.decorator;

import cl.biblioteca.dominio.Prestamo;

import java.time.temporal.ChronoUnit;

public abstract class MultaDecorador implements CalculadoraDeMulta {
    protected final CalculadoraDeMulta siguiente;

    protected MultaDecorador(CalculadoraDeMulta siguiente) {
        this.siguiente = siguiente;
    }

    protected long diasAtraso(Prestamo p) {
        if (p.obtenerFechaDevolucion() == null || p.obtenerFechaVencimiento() == null) return 0;
        return Math.max(0, ChronoUnit.DAYS.between(
                p.obtenerFechaVencimiento(), p.obtenerFechaDevolucion()));
    }

    protected boolean vencimientoEsDomingo(Prestamo p) {
        return p.obtenerFechaVencimiento() != null
                && p.obtenerFechaVencimiento().getDayOfWeek().getValue() == 7;
    }
}