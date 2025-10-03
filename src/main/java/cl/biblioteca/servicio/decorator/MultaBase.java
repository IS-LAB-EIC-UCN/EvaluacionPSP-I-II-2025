package cl.biblioteca.servicio.decorator;

import cl.biblioteca.dominio.Prestamo;

import java.time.temporal.ChronoUnit;

/** Base: $100 por día de atraso. Si no hay devolución, multa 0. */
public class MultaBase implements CalculadoraDeMulta {

    private final int costoPorDia;

    public MultaBase() { this(100); }
    public MultaBase(int costoPorDia) { this.costoPorDia = costoPorDia; }

    @Override
    public double calcular(Prestamo p) {
        if (p.obtenerFechaDevolucion() == null) return 0.0;
        long diasAtraso = Math.max(0, ChronoUnit.DAYS.between(
                p.obtenerFechaVencimiento(), p.obtenerFechaDevolucion()));
        return diasAtraso * (double) costoPorDia;
    }
}