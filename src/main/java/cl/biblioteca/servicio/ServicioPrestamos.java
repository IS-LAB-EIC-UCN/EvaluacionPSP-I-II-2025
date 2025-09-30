package cl.biblioteca.servicio;

import cl.biblioteca.dominio.*;
import cl.biblioteca.persistencia.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;

/**
 * Servicio de aplicación para gestionar operaciones de <strong>préstamo</strong>.
 *
 * <p>
 * Esta clase orquesta la creación y persistencia de entidades {@link Prestamo}
 * utilizando JPA. Encapsula la apertura/cierre de {@link EntityManager} y el
 * manejo de la transacción asociada, de modo que el resto de la aplicación no
 * tenga que lidiar con esos detalles.
 * </p>
 *
 * <h2>Alcance</h2>
 * <ul>
 *   <li>Crear préstamos con fechas de inicio y vencimiento calculadas desde la fecha actual.</li>
 *   <li>Persistir el préstamo y devolver la entidad administrada al llamador.</li>
 * </ul>
 *
 * <h2>Consideraciones</h2>
 * <ul>
 *   <li>El método establece <em>solo</em> una referencia polimórfica al material
 *       vía {@code idMaterial}/{@code tipoMaterial}; no valida existencia ni disponibilidad.</li>
 *   <li>El orden y la duración del préstamo se calculan con {@link LocalDate}, sin componente horario.</li>
 *   <li>En caso de excepción, la transacción se revierte (rollback) y el {@link EntityManager} se cierra siempre.</li>
 * </ul>
 *
 * @since 1.0.0
 */
public class ServicioPrestamos {

    /**
     * Crea y persiste un nuevo {@link Prestamo}.
     *
     * <p><strong>Flujo:</strong></p>
     * <ol>
     *   <li>Abre un {@link EntityManager} y comienza una transacción.</li>
     *   <li>Construye el préstamo con: socio, id/tipo de material, fecha de inicio = hoy,
     *       fecha de vencimiento = hoy + {@code dias}.</li>
     *   <li>Persiste el préstamo y confirma (commit).</li>
     *   <li>Devuelve la entidad persistida.</li>
     * </ol>
     *
     * @param socio        socio que realiza el préstamo (no nulo).
     * @param idMaterial   identificador del material prestado (no nulo).
     * @param tipoMaterial tipo lógico del material (por ejemplo, {@code "Libro"}, {@code "Revista"}, {@code "Video"}).
     * @param dias         cantidad de días del préstamo; se suma a la fecha actual para calcular el vencimiento.
     * @return el {@link Prestamo} persistido.
     * @throws RuntimeException si ocurre algún error de persistencia; la transacción se revierte.
     */
    public Prestamo prestar(Socio socio, Long idMaterial, String tipoMaterial, int dias) {
        EntityManager em = JpaUtil.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Prestamo p = new Prestamo();
            p.definirSocio(socio);
            p.definirIdMaterial(idMaterial);
            p.definirTipoMaterial(tipoMaterial);
            p.definirFechaInicio(LocalDate.now());
            p.definirFechaVencimiento(LocalDate.now().plusDays(dias));

            em.persist(p);
            tx.commit();
            return p;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
