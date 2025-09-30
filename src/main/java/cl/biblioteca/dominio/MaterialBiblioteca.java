package cl.biblioteca.dominio;

import jakarta.persistence.*;

/**
 * Superclase mapeada para los materiales de la biblioteca.
 *
 * <p>
 * Define los atributos comunes a cualquier material del catálogo
 * (por ejemplo: {@link Libro}, {@link Revista}, {@link Video}):
 * un identificador técnico, un título y el autor o editor.
 * </p>
 *
 * <h2>Persistencia</h2>
 * <ul>
 *   <li>Anotada con {@link MappedSuperclass}: no genera una tabla propia; sus
 *       campos se <em>heredan</em> y se mapean en las tablas de las subclases
 *       concretas.</li>
 *   <li>{@code id} usa estrategia {@link GenerationType#IDENTITY}.</li>
 *   <li>{@code titulo} es obligatorio a nivel de base de datos
 *       ({@code nullable = false}).</li>
 * </ul>
 *
 * <h2>Invariantes esperados</h2>
 * <ul>
 *   <li>{@code titulo} no debe ser {@code null} ni vacío.</li>
 *   <li>{@code autorOEditor} puede ser {@code null} si no aplica.</li>
 * </ul>
 */
@MappedSuperclass
public abstract class MaterialBiblioteca {

    /** Identificador técnico autogenerado. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /** Título del material (obligatorio). */
    @Column(nullable = false)
    protected String titulo;

    /** Autor o editor del material (opcional). */
    @Column
    protected String autorOEditor;

    /**
     * Obtiene el identificador técnico del material.
     * @return id autogenerado, o {@code null} si aún no se ha persistido.
     */
    public Long obtenerId() { return id; }

    /**
     * Obtiene el título del material.
     * @return título no nulo.
     */
    public String obtenerTitulo() { return titulo; }

    /**
     * Define o actualiza el título del material.
     * @param titulo nuevo título (no debe ser {@code null}).
     */
    public void definirTitulo(String titulo) { this.titulo = titulo; }

    /**
     * Obtiene el autor o editor del material.
     * @return nombre del autor/editor, o {@code null} si no está registrado.
     */
    public String obtenerAutorOEditor() { return autorOEditor; }

    /**
     * Define o actualiza el autor o editor del material.
     * @param a nombre del autor/editor (puede ser {@code null}).
     */
    public void definirAutorOEditor(String a) { this.autorOEditor = a; }
}
