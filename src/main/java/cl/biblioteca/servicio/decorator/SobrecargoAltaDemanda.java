package cl.biblioteca.servicio.decorator;

import cl.biblioteca.dominio.Prestamo;

/** Si el atraso es > umbral (3 días por defecto), suma un recargo fijo ($200). */
public class SobrecargoAltaDemanda extends MultaDecorador {
    private final int umbralDias;
    private final int recargoFijo;

    public SobrecargoAltaDemanda(CalculadoraDeMulta siguiente) {
        this(siguiente, 3, 200);
    }

    public SobrecargoAltaDemanda(CalculadoraDeMulta siguiente, int umbralDias, int recargoFijo) {
        super(siguiente);
        this.umbralDias = umbralDias;
        this.recargoFijo = recargoFijo;
    }

    public double calcular(Prestamo p) {
        double valor = siguiente.calcular(p);
        if (diasAtraso(p) > umbralDias) {
            valor += recargoFijo;
        }
        return valor;
    }
}