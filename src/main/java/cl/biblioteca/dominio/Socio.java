package cl.biblioteca.dominio;

import jakarta.persistence.*;

/**
 * Entidad que representa a un <strong>Socio</strong> de la biblioteca.
 * <p>
 * Un socio puede mantener uno o más préstamos activos (ver {@link Prestamo}).
 * La clase modela los datos mínimos para identificación y beneficios.
 * </p>
 *
 * <h2>Persistencia</h2>
 * <ul>
 *   <li>Anotada con {@link Entity} para su mapeo por JPA/Hibernate.</li>
 *   <li>El identificador {@link #id} se genera con estrategia {@link GenerationType#IDENTITY}.</li>
 *   <li>El campo {@link #nombre} es obligatorio a nivel de base de datos.</li>
 * </ul>
 *
 * <h2>Invariantes esperados</h2>
 * <ul>
 *   <li>{@code nombre} no debe ser {@code null} ni vacío.</li>
 *   <li>{@code premium} indica si el socio posee beneficios (p. ej., descuentos en multas).</li>
 * </ul>
 */
@Entity
public class Socio {

    /** Identificador técnico autogenerado del socio. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del socio (obligatorio). */
    @Column(nullable = false)
    private String nombre;

    /** Indica si el socio posee beneficios premium (descuentos, exenciones, etc.). */
    private boolean premium; // socios con beneficios

    /**
     * Obtiene el identificador del socio.
     * @return id autogenerado, o {@code null} si aún no se ha persistido.
     */
    public Long obtenerId() { return id; }

    /**
     * Obtiene el nombre del socio.
     * @return nombre no nulo.
     */
    public String obtenerNombre() { return nombre; }

    /**
     * Define o actualiza el nombre del socio.
     * @param nombre nombre del socio (no debe ser {@code null}).
     */
    public void definirNombre(String nombre) { this.nombre = nombre; }

    /**
     * Indica si el socio es de tipo premium.
     * @return {@code true} si es premium; {@code false} en caso contrario.
     */
    public boolean esPremium() { return premium; }

    /**
     * Define el estado premium del socio.
     * @param premium {@code true} si posee beneficios; {@code false} en caso contrario.
     */
    public void definirPremium(boolean premium) { this.premium = premium; }
}
