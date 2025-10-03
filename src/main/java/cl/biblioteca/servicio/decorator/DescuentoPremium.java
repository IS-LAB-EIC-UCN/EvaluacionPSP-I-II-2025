package cl.biblioteca.servicio.decorator;

import cl.biblioteca.dominio.Prestamo;

/** Si el socio es premium, aplica 20% de descuento. */
public class DescuentoPremium extends MultaDecorador {
    private final double porcentaje; // 0.20 => 20%

    public DescuentoPremium(CalculadoraDeMulta siguiente) { this(siguiente, 0.20); }
    public DescuentoPremium(CalculadoraDeMulta siguiente, double porcentaje) {
        super(siguiente);
        this.porcentaje = porcentaje;
    }

    public double calcular(Prestamo p) {
        double valor = siguiente.calcular(p);
        if (p.obtenerSocio() != null && p.obtenerSocio().esPremium()) {
            valor = valor * (1.0 - porcentaje);
        }
        return valor;
    }
}
