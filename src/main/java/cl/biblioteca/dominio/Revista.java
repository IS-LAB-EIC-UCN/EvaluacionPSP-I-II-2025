package cl.biblioteca.dominio;

import jakarta.persistence.*;

/**
 * Entidad que representa una <strong>Revista</strong> en el catálogo de la biblioteca.
 * <p>
 * Hereda de {@link MaterialBiblioteca} los atributos comunes (id, título y autor/editor)
 * y agrega el campo específico {@link #numeroEdicion}, que identifica el fascículo
 * o número de la revista.
 * </p>
 *
 * <h2>Persistencia</h2>
 * <ul>
 *   <li>Anotada con {@link Entity} para su mapeo por JPA/Hibernate.</li>
 *   <li>Los campos heredados de {@code MaterialBiblioteca} se persisten en la misma tabla.</li>
 * </ul>
 *
 * <h2>Invariantes esperados</h2>
 * <ul>
 *   <li>{@code numeroEdicion} debería ser un entero positivo (≥ 1). No se valida en esta clase.</li>
 * </ul>
 */
@Entity
public class Revista extends MaterialBiblioteca {

    /** Número de edición o fascículo de la revista (por ejemplo, 182, 210, etc.). */
    private int numeroEdicion;

    /**
     * Obtiene el número de edición de la revista.
     *
     * @return número de edición (se espera un entero ≥ 1).
     */
    public int obtenerNumeroEdicion() { return numeroEdicion; }

    /**
     * Define o actualiza el número de edición de la revista.
     *
     * @param numeroEdicion valor del fascículo (idealmente ≥ 1).
     */
    public void definirNumeroEdicion(int numeroEdicion) { this.numeroEdicion = numeroEdicion; }
}
