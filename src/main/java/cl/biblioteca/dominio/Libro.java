package cl.biblioteca.dominio;

import jakarta.persistence.*;

/**
 * Entidad de dominio que representa un <strong>Libro</strong> en el catálogo de la biblioteca.
 * <p>
 * Hereda los atributos comunes de {@link MaterialBiblioteca} (id, título y autor/editor) y
 * agrega metadatos propios de los libros, como el <em>ISBN</em> y la cantidad de <em>páginas</em>.
 * </p>
 *
 * <h2>Persistencia</h2>
 * <ul>
 *   <li>La clase está anotada con {@link Entity} para que JPA/Hibernate la mapee a una tabla.</li>
 *   <li>Los campos de esta clase se persisten junto con los heredados desde {@code MaterialBiblioteca}.</li>
 * </ul>
 *
 * <h2>Invariantes</h2>
 * <ul>
 *   <li>{@code isbn} puede ser {@code null} si el material no posee o no registra ISBN.</li>
 *   <li>{@code paginas} debe ser un entero no negativo.</li>
 * </ul>
 */
@Entity
public class Libro extends MaterialBiblioteca {

    /**
     * Código ISBN del libro. No necesariamente único en la base si se admiten
     * ediciones/formatos distintos; su presencia es opcional.
     */
    private String isbn;

    /**
     * Número total de páginas del libro. Se espera un valor mayor o igual a 0.
     */
    private int paginas;

    /**
     * Obtiene el código ISBN del libro.
     *
     * @return cadena con el ISBN, o {@code null} si no está registrado.
     */
    public String obtenerIsbn() { return isbn; }

    /**
     * Define o actualiza el ISBN del libro.
     *
     * @param isbn código ISBN a registrar (puede ser {@code null} si se desconoce).
     */
    public void definirIsbn(String isbn) { this.isbn = isbn; }

    /**
     * Obtiene la cantidad de páginas del libro.
     *
     * @return número de páginas (no negativo).
     */
    public int obtenerPaginas() { return paginas; }

    /**
     * Define la cantidad de páginas del libro.
     *
     * @param paginas número de páginas (debe ser ≥ 0).
     */
    public void definirPaginas(int paginas) { this.paginas = paginas; }
}
