package cl.biblioteca.servicio.decorator;

import cl.biblioteca.dominio.Prestamo;

/** Si el vencimiento cae en domingo, pone la multa en 0 (luego otros decoradores pueden actuar). */
public class ExencionFeriado extends MultaDecorador {
    public ExencionFeriado(CalculadoraDeMulta siguiente) { super(siguiente); }


    public double calcular(Prestamo p) {
        double valor = siguiente.calcular(p);
        if (vencimientoEsDomingo(p)) {
            valor = 0.0;
        }
        return valor;
    }
}
