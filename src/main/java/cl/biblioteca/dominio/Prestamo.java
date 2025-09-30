package cl.biblioteca.dominio;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad que representa un <strong>préstamo</strong> de un material de la biblioteca.
 *
 * <p>
 * El modelo sigue un esquema heredado en el que la referencia al material es
 * <em>polimórfica</em>: se almacena el identificador del material en
 * {@link #idMaterial} y su tipo lógico en {@link #tipoMaterial} (por ejemplo:
 * {@code "Libro"}, {@code "Revista"}, {@code "Video"}). Esto evita una FK
 * directa a una sola tabla de materiales y mantiene compatibilidad con el sistema legado.
 * </p>
 *
 * <h2>Persistencia</h2>
 * <ul>
 *   <li>La relación con {@link Socio} se mapea mediante {@code socio_id}.</li>
 *   <li>Los nombres de columna se fijan explícitamente para alinear con scripts de carga
 *       (p. ej., {@code seed.sql}).</li>
 *   <li>Las fechas se modelan con {@link LocalDate} (sin componente de tiempo).</li>
 * </ul>
 *
 * <h2>Reglas e invariantes</h2>
 * <ul>
 *   <li>{@link #socio} no puede ser {@code null}.</li>
 *   <li>{@link #idMaterial} y {@link #tipoMaterial} son obligatorios.</li>
 *   <li>{@link #fechaInicio} y {@link #fechaVencimiento} deberían ser coherentes
 *       (vencimiento ≥ inicio). {@link #fechaDevolucion} es opcional.</li>
 * </ul>
 */
@Entity
public class Prestamo {

    /** Identificador técnico autogenerado del préstamo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Socio que realiza el préstamo (FK: {@code socio_id}). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "socio_id")
    private Socio socio;

    /**
     * Identificador del material prestado. Es una FK polimórfica simplificada:
     * el valor apunta a la tabla concreta indicada por {@link #tipoMaterial}.
     */
    @Column(name = "idMaterial", nullable = false)
    private Long idMaterial;

    /**
     * Tipo del material prestado (por ejemplo, {@code "Libro"}, {@code "Revista"}, {@code "Video"}).
     * Se usa junto con {@link #idMaterial} para resolver el material real.
     */
    @Column(name = "tipoMaterial", nullable = false)
    private String tipoMaterial;

    /** Fecha en que inicia el préstamo. */
    @Column(name = "fechaInicio")
    private LocalDate fechaInicio;

    /** Fecha límite de devolución. */
    @Column(name = "fechaVencimiento")
    private LocalDate fechaVencimiento;

    /** Fecha en que se realizó la devolución efectiva; puede ser {@code null} si aún no se devuelve. */
    @Column(name = "fechaDevolucion")
    private LocalDate fechaDevolucion;

    /** @return id autogenerado del préstamo, o {@code null} si no se ha persistido. */
    public Long obtenerId() { return id; }

    /** @return socio que efectuó el préstamo (no nulo). */
    public Socio obtenerSocio() { return socio; }

    /** Define el socio que realiza el préstamo. @param socio socio no nulo. */
    public void definirSocio(Socio socio) { this.socio = socio; }

    /** @return identificador del material prestado. */
    public Long obtenerIdMaterial() { return idMaterial; }

    /** Define el identificador del material prestado. @param idMaterial id del material. */
    public void definirIdMaterial(Long idMaterial) { this.idMaterial = idMaterial; }

    /** @return tipo lógico del material ({@code Libro}, {@code Revista}, {@code Video}, etc.). */
    public String obtenerTipoMaterial() { return tipoMaterial; }

    /** Define el tipo lógico del material. @param tipoMaterial tipo del material. */
    public void definirTipoMaterial(String tipoMaterial) { this.tipoMaterial = tipoMaterial; }

    /** @return fecha de inicio del préstamo. */
    public LocalDate obtenerFechaInicio() { return fechaInicio; }

    /** Define la fecha de inicio del préstamo. @param fechaInicio fecha de inicio. */
    public void definirFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    /** @return fecha de vencimiento del préstamo. */
    public LocalDate obtenerFechaVencimiento() { return fechaVencimiento; }

    /** Define la fecha de vencimiento. @param fechaVencimiento fecha límite. */
    public void definirFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    /** @return fecha de devolución efectiva, o {@code null} si no se ha devuelto. */
    public LocalDate obtenerFechaDevolucion() { return fechaDevolucion; }

    /** Define la fecha de devolución efectiva. @param fechaDevolucion fecha de devolución. */
    public void definirFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
}
