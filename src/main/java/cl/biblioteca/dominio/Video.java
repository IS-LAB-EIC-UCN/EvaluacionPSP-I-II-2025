package cl.biblioteca.dominio;

import jakarta.persistence.*;

/**
 * Entidad que representa un material audiovisual de tipo <strong>Video</strong>
 * dentro del catálogo de la biblioteca.
 * <p>
 * Hereda de {@link MaterialBiblioteca} los atributos comunes (id, título y autor/editor)
 * y agrega metadatos específicos: la <em>duración en minutos</em> y el <em>formato</em>
 * (por ejemplo, DVD, BluRay, archivo digital, etc.).
 * </p>
 *
 * <h2>Persistencia</h2>
 * <ul>
 *   <li>Anotada con {@link Entity} para que JPA/Hibernate la mapee a una tabla.</li>
 *   <li>Los campos heredados de {@code MaterialBiblioteca} se persisten en la misma tabla.</li>
 * </ul>
 *
 * <h2>Invariantes esperados</h2>
 * <ul>
 *   <li>{@code duracionMinutos} debe ser un entero mayor o igual a 0.</li>
 *   <li>{@code formato} puede ser {@code null} si no aplica o se desconoce.</li>
 * </ul>
 */
@Entity
public class Video extends MaterialBiblioteca {

    /** Duración total del video en minutos (valor no negativo). */
    private int duracionMinutos;

    /** Formato físico o digital del video (p. ej., DVD, BluRay). */
    private String formato; // DVD, BluRay, etc.

    /**
     * Obtiene la duración del video en minutos.
     *
     * @return duración en minutos (≥ 0).
     */
    public int obtenerDuracionMinutos() { return duracionMinutos; }

    /**
     * Define la duración del video en minutos.
     *
     * @param d duración en minutos (debe ser ≥ 0).
     */
    public void definirDuracionMinutos(int d) { this.duracionMinutos = d; }

    /**
     * Obtiene el formato del video.
     *
     * @return el formato (por ejemplo, {@code "DVD"}), o {@code null} si no está registrado.
     */
    public String obtenerFormato() { return formato; }

    /**
     * Define el formato del video.
     *
     * @param formato formato físico o digital (puede ser {@code null}).
     */
    public void definirFormato(String formato) { this.formato = formato; }
}
