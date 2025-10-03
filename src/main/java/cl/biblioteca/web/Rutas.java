package cl.biblioteca.web;

import cl.biblioteca.dominio.*;
import cl.biblioteca.persistencia.JpaUtil;
import cl.biblioteca.servicio.ServicioReportes;
import cl.biblioteca.servicio.decorator.*;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.*;

/**
 * Define y registra las <strong>rutas HTTP</strong> de la aplicación (capa web).
 *
 * <h2>Resumen de endpoints</h2>
 * <ul>
 *   <li><strong>GET /</strong> &rarr; redirige a <code>/public/index.html</code>
 *       (recursos estáticos servidos desde el classpath).</li>
 *   <li><strong>GET /api/inventory</strong> &rarr; devuelve un JSON con el inventario
 *       resumido: cada fila posee las claves <code>type</code>, <code>title</code>, <code>meta</code>.
 *       La estructura coincide con el sistema legado (útil para el refactor con Visitor).</li>
 *   <li><strong>POST /api/seed</strong> &rarr; inserta datos mínimos de ejemplo
 *       (libro, revista, video, dos socios). Útil en entornos sin script de carga.</li>
 *   <li><strong>GET /api/fee-demo</strong> &rarr; endpoint de demostración del cálculo
 *       de multas con banderas (legado), a refactorizar luego con Decorator.</li>
 * </ul>
 *
 * <h2>Notas</h2>
 * <ul>
 *   <li>Esta clase no contiene lógica de negocio: delega en servicios y entidades.</li>
 *   <li>Si se usa <code>hibernate.hbm2ddl.auto=create-drop</code> + <code>seed.sql</code>, el
 *       endpoint <code>/api/seed</code> es opcional.</li>
 * </ul>
 */
public class Rutas {

    /**
     * Registra todas las rutas sobre una instancia de {@link Javalin}.
     *
     * @param app instancia de servidor Javalin ya creada y configurada para archivos estáticos.
     */
    public static void registrar(Javalin app) {
        // Redirección a la página principal (servida como estático bajo /public)
        app.get("/", ctx -> ctx.redirect("/public/index.html"));

        // Inventario resumido (JSON) — mantiene el formato del sistema legado
        app.get("/api/inventory", ctx -> {
            List<Map<String, Object>> inv = new ServicioReportes().resumenInventario();
            ctx.json(inv);
        });

        // Semilla de datos de ejemplo (opcional si tienes seed.sql en el arranque)
        app.post("/api/seed", ctx -> {
            EntityManager em = JpaUtil.em();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                // Datos de ejemplo mínimos
                Libro l = new Libro();
                l.definirTitulo("Clean Architecture");
                l.definirAutorOEditor("R. Martin");
                l.definirIsbn("978-0134494166");
                l.definirPaginas(450);

                Revista r = new Revista();
                r.definirTitulo("ACM Queue");
                r.definirAutorOEditor("ACM");
                r.definirNumeroEdicion(182);

                Video v = new Video();
                v.definirTitulo("Agile Conference Talk");
                v.definirAutorOEditor("J. Doe");
                v.definirDuracionMinutos(75);
                v.definirFormato("DVD");

                Socio s = new Socio();
                s.definirNombre("Ana Perez");
                s.definirPremium(true);

                Socio t = new Socio();
                t.definirNombre("Juan Diaz");
                t.definirPremium(false);

                em.persist(l);
                em.persist(r);
                em.persist(v);
                em.persist(s);
                em.persist(t);

                tx.commit();
                ctx.status(201);
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            } finally {
                em.close();
            }
        });

        app.get("/api/fee-demo", ctx -> ctx.redirect("/public/multas.html"));

        app.get("/api/inventory/stats", ctx -> {
            String json = new ServicioReportes().estadisticasInventarioComoJson();
            ctx.contentType("application/json; charset=utf-8");
            ctx.result(json);
        });

        // En cl.biblioteca.web.Rutas#registrar(Javalin app)
        app.get("/api/fees/{prestamoId}", ctx -> {
            // Lee y valida el path param como Long (Javalin devuelve 400 si no convierte)
            long id = ctx.pathParamAsClass("prestamoId", Long.class).get();

            // Flags por query param (0/1, true/false, si/sí)
            boolean exencionFeriado       = parseBool(ctx.queryParam("exencionFeriado"));
            boolean descuentoPremium      = parseBool(ctx.queryParam("descuentoPremium"));
            boolean sobrecargoAltaDemanda = parseBool(ctx.queryParam("sobrecargoAltaDemanda"));

            var em = JpaUtil.em();
            try {
                Prestamo p = em.find(Prestamo.class, id);
                if (p == null) {
                    ctx.status(404).json(Map.of("error", "Prestamo no encontrado: " + id));
                    return;
                }

                // Construcción de cadena Decorator (orden legado): Base -> Exención -> Descuento -> Sobrecargo
                var base = new MultaBase();
                var c1 = exencionFeriado       ? new ExencionFeriado(base) : base;
                var c2 = descuentoPremium      ? new DescuentoPremium(c1) : c1;
                var c3 = sobrecargoAltaDemanda ? new SobrecargoAltaDemanda(c2) : c2;

                double m0 = base.calcular(p);
                double m1 = c1.calcular(p);
                double m2 = c2.calcular(p);
                double m3 = c3.calcular(p);

                long diasAtraso = calcularDiasAtraso(p);

                ctx.contentType("application/json; charset=utf-8");
                ctx.json(Map.of(
                        "prestamoId", id,
                        "reglas", Map.of(
                                "exencionFeriado", exencionFeriado,
                                "descuentoPremium", descuentoPremium,
                                "sobrecargoAltaDemanda", sobrecargoAltaDemanda
                        ),
                        "diasAtraso", diasAtraso,
                        "desglose", Map.of(
                                "base", m0,
                                "despuesExencion", m1,
                                "despuesDescuento", m2,
                                "final", m3
                        ),
                        "monto", m3
                ));
            } finally {
                em.close();
            }
        });
    }
    private static boolean parseBool(String v) {
        if (v == null) return false;
        return "1".equals(v) || "true".equalsIgnoreCase(v) || "si".equalsIgnoreCase(v) || "sí".equalsIgnoreCase(v);
    }

    private static long calcularDiasAtraso(Prestamo p) {
        if (p.obtenerFechaDevolucion() == null || p.obtenerFechaVencimiento() == null) return 0;
        return Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(
                p.obtenerFechaVencimiento(), p.obtenerFechaDevolucion()));
    }
}