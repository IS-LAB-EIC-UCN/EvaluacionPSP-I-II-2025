package cl.biblioteca.servicio;

import cl.biblioteca.dominio.Prestamo;
import java.time.temporal.ChronoUnit;

/**
 * Calcula el monto de la <strong>multa</strong> de un {@link Prestamo} según reglas vigentes.
 *
 * <p><strong>Contexto LEGADO:</strong> esta implementación utiliza <em>banderas</em> y
 * condicionales encadenados para activar o desactivar reglas. Cada nueva regla obliga
 * a modificar esta clase, lo que genera dificultad de mantenimiento y pruebas.
 * </p>
 *
 * <h2>Reglas actuales</h2>
 * <ul>
 *   <li><strong>Base:</strong> $100 por día de atraso (si no hay devolución, multa 0).</li>
 *   <li><strong>Exención por feriado:</strong> si el vencimiento cae en domingo, multa 0.</li>
 *   <li><strong>Descuento premium:</strong> si el socio es <em>premium</em>, 20% de descuento.</li>
 *   <li><strong>Sobrecargo por alta demanda:</strong> si hay más de 3 días de atraso, +$200 fijos.</li>
 * </ul>
 *
 * <h2>Consideraciones</h2>
 * <ul>
 *   <li>El cálculo utiliza {@link ChronoUnit#DAYS} sobre fechas de tipo {@code LocalDate},
 *       sin componente horario.</li>
 *   <li>El orden de aplicación de reglas es el histórico del sistema (legado) y debe
 *       preservarse durante el refactor.</li>
 * </ul>
 *
 */
public class CalculadoraMultas {

    /** Activa la exención total si el vencimiento cae en domingo. */
    private final boolean exencionFeriado;

    /** Aplica 20% de descuento si el socio es premium. */
    private final boolean descuentoPremium;

    /** Suma $200 si el atraso supera los 3 días. */
    private final boolean sobrecargoAltaDemanda;

    /**
     * Crea una calculadora con las banderas de reglas activas.
     *
     * @param exencionFeriado        {@code true} para activar la exención por feriado (domingo).
     * @param descuentoPremium       {@code true} para aplicar 20% de descuento a socios premium.
     * @param sobrecargoAltaDemanda  {@code true} para sumar $200 cuando el atraso &gt; 3 días.
     */
    public CalculadoraMultas(boolean exencionFeriado, boolean descuentoPremium, boolean sobrecargoAltaDemanda) {
        this.exencionFeriado = exencionFeriado;
        this.descuentoPremium = descuentoPremium;
        this.sobrecargoAltaDemanda = sobrecargoAltaDemanda;
    }

    /**
     * Calcula la multa correspondiente a un préstamo.
     *
     * <p><strong>Algoritmo (orden legado):</strong></p>
     * <ol>
     *   <li>Si {@code fechaDevolucion} es {@code null}, retorna 0.</li>
     *   <li>Base = días de atraso × $100 (días negativos cuentan como 0).</li>
     *   <li>Si {@code exencionFeriado} y el vencimiento es domingo → Base = 0.</li>
     *   <li>Si {@code descuentoPremium} y el socio es premium → Base *= 0.8.</li>
     *   <li>Si {@code sobrecargoAltaDemanda} y atraso &gt; 3 días → Base += 200.</li>
     * </ol>
     *
     * @param prestamo préstamo a evaluar (debe tener fechas coherentes).
     * @return monto de la multa en unidades monetarias.
     * @throws NullPointerException si {@code prestamo} es {@code null}.
     */
    public double calcular(Prestamo prestamo) {
        if (prestamo.obtenerFechaDevolucion() == null) return 0.0;

        long diasAtraso = Math.max(0, ChronoUnit.DAYS.between(
                prestamo.obtenerFechaVencimiento(),
                prestamo.obtenerFechaDevolucion()
        ));
        double base = diasAtraso * 100; // $100 por día de atraso

        // Exención total en feriado (domingo)
        if (exencionFeriado && cruzaFeriado(prestamo)) {
            base = 0;
        }
        // Descuento por socio premium (20%)
        if (descuentoPremium && prestamo.obtenerSocio() != null && prestamo.obtenerSocio().esPremium()) {
            base *= 0.8;
        }
        // Sobrecargo por alta demanda si atraso > 3 días
        if (sobrecargoAltaDemanda && diasAtraso > 3) {
            base += 200;
        }
        return base;
    }

    /**
     * Indica si el vencimiento del préstamo cae en domingo (política de ejemplo).
     *
     * @param prestamo préstamo a evaluar.
     * @return {@code true} si el vencimiento es domingo; {@code false} en caso contrario
     *         o si la fecha de vencimiento es {@code null}.
     */
    private boolean cruzaFeriado(Prestamo prestamo) {
        return prestamo.obtenerFechaVencimiento() != null
                && prestamo.obtenerFechaVencimiento().getDayOfWeek().getValue() == 7;
    }
}
