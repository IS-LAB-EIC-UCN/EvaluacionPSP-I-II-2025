package cl.biblioteca.servicio.decorator;

import cl.biblioteca.dominio.Prestamo;

public interface CalculadoraDeMulta {
    double calcular(Prestamo prestamo);
}