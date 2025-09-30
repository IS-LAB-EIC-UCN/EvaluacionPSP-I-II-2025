package cl.biblioteca.persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utilidad para gestionar el ciclo de vida de JPA en la aplicación.
 *
 * <p>
 * Crea un único {@link EntityManagerFactory} para la unidad de persistencia
 * <em>"libraryPU"</em> (definida en <code>META-INF/persistence.xml</code>) y
 * expone un método de conveniencia para abrir {@link EntityManager}s.
 * </p>
 *
 * <h2>Uso</h2>
 * <pre>{@code
 * EntityManager em = JpaUtil.em();
 * try {
 *     // ... operaciones JPA ...
 * } finally {
 *     em.close(); // ¡Importante!
 * }
 * }</pre>
 *
 * <h2>Notas</h2>
 * <ul>
 *   <li>El {@link EntityManagerFactory} es <strong>thread-safe</strong> y debe
 *       crearse una sola vez por aplicación.</li>
 *   <li>Cada {@link EntityManager} es <strong>no</strong> thread-safe y debe cerrarse
 *       siempre tras su uso.</li>
 *   <li>Si usas <code>hibernate.hbm2ddl.auto=create-drop</code>, recuerda invocar
 *       {@link #close()} al apagar la app para que Hibernate ejecute el <em>drop</em>
 *       del esquema.</li>
 * </ul>
 *
 * @since 1.0.0
 */
public class JpaUtil {

    /** Fábrica única asociada a la unidad de persistencia "libraryPU". */
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("libraryPU");

    /**
     * Abre un nuevo {@link EntityManager}.
     * El llamador es responsable de cerrarlo con {@link EntityManager#close()}.
     *
     * @return un {@code EntityManager} listo para usar.
     */
    public static EntityManager em() {
        return emf.createEntityManager();
    }

    /**
     * Cierra el {@link EntityManagerFactory} global y libera recursos.
     * <p>Invocar este método al finalizar la aplicación (por ejemplo, en un
     * <em>shutdown hook</em> o al detener el servidor) garantiza el cierre limpio
     * y, en su caso, que se ejecute el <em>drop</em> de esquema.</p>
     */
    public static void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
