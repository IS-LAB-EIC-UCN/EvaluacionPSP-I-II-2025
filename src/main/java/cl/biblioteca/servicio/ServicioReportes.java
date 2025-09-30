package cl.biblioteca.servicio;

import cl.biblioteca.dominio.*;
import cl.biblioteca.persistencia.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.*;

/**
 * Servicio de generación de <strong>reportes de inventario</strong> en formato resumido.
 *
 * <p><strong>Contexto LEGADO:</strong> la implementación construye el reporte
 * consultando explícitamente cada tipo concreto de material
 * ({@link Libro}, {@link Revista}, {@link Video}) y armando filas manualmente.
 * </p>
 *
 * <h2>Salida esperada</h2>
 * <p>Una lista de mapas con las columnas:</p>
 * <ul>
 *   <li><code>"type"</code>: tipo de material (p. ej., <em>Libro</em>, <em>Revista</em>, <em>Video</em>).</li>
 *   <li><code>"title"</code>: título del material.</li>
 *   <li><code>"meta"</code>: metadato breve específico por tipo
 *       (ISBN para libros, issue para revistas, duration para videos).</li>
 * </ul>
 *
 * <h2>Consideraciones</h2>
 * <ul>
 *   <li>El método abre un {@link EntityManager} y lo cierra siempre en el bloque <em>finally</em>.</li>
 *   <li>Actualmente realiza tres consultas separadas (una por tipo), manteniendo la semántica del sistema legado.</li>
 * </ul>
 */
public class ServicioReportes {

    /**
     * Genera el inventario resumido en memoria.
     *
     * <p><strong>Campos de cada fila</strong>:
     * <code>type</code>, <code>title</code>, <code>meta</code>.
     * El contenido se alinea con la respuesta del endpoint JSON del sistema legado.
     * </p>
     *
     * @return lista de filas de inventario; nunca {@code null} (puede ser vacía).
     */
    public List<Map<String,Object>> resumenInventario() {
        EntityManager em = JpaUtil.em();
        try {
            List<Map<String,Object>> out = new ArrayList<>();

            // Consultas por tipo concreto (legado)
            List<Libro>   libros   = em.createQuery("from Libro",   Libro.class).getResultList();
            List<Revista> revistas = em.createQuery("from Revista", Revista.class).getResultList();
            List<Video>   videos   = em.createQuery("from Video",   Video.class).getResultList();

            // Transformación a filas homogéneas
            for (Libro l : libros) {
                Map<String,Object> row = new HashMap<>();
                row.put("type",  "Libro");
                row.put("title", l.obtenerTitulo());
                row.put("meta",  "ISBN=" + l.obtenerIsbn());
                out.add(row);
            }
            for (Revista r : revistas) {
                Map<String,Object> row = new HashMap<>();
                row.put("type",  "Revista");
                row.put("title", r.obtenerTitulo());
                row.put("meta",  "issue=" + r.obtenerNumeroEdicion());
                out.add(row);
            }
            for (Video v : videos) {
                Map<String,Object> row = new HashMap<>();
                row.put("type",  "Video");
                row.put("title", v.obtenerTitulo());
                row.put("meta",  "duration=" + v.obtenerDuracionMinutos());
                out.add(row);
            }
            return out;
        } finally {
            em.close();
        }
    }

    public String estadisticasInventarioComoJson() {
        EntityManager em = JpaUtil.em();
        try {
            var libros   = em.createQuery("from Libro",   Libro.class).getResultList();
            var revistas = em.createQuery("from Revista", Revista.class).getResultList();
            var videos   = em.createQuery("from Video",   Video.class).getResultList();


            // return "el string json"
            return null;

        } finally {
            em.close();
        }
    }
}
