package cl.biblioteca.app;

import cl.biblioteca.web.Rutas;
import io.javalin.Javalin;

/**
 * Punto de entrada de la aplicación de Biblioteca (sistema legado).
 *
 * <p>Esta clase inicializa un servidor {@link Javalin} que:
 * <ul>
 *   <li>Publica recursos estáticos del classpath ubicados en <code>/public</code>
 *       y los expone bajo la ruta hospedada <code>/public</code>
 *       (por ejemplo, <code>/public/index.html</code>).</li>
 *   <li>Registra las rutas HTTP definidas en {@link cl.biblioteca.web.Rutas}.</li>
 * </ul>
 * </p>
 *
 * <h2>Recursos estáticos</h2>
 * <p>
 * Se configura <code>hostedPath = "/public"</code> para que los archivos de
 * <code>src/main/resources/public</code> queden accesibles vía URL con ese prefijo.
 * </p>
 *
 * <h2>Rutas</h2>
 * <p>
 * La clase {@link cl.biblioteca.web.Rutas} define endpoints como
 * <code>/api/inventory</code> y la redirección inicial a <code>/public/index.html</code>.
 * </p>
 *
 * @since 1.0.0
 */
public class App {

    /**
     * Arranca el servidor HTTP, publica los archivos estáticos y registra las rutas de la aplicación.
     *
     * <p>El servidor queda escuchando en <code>http://localhost:7070</code>.
     * Para detenerlo de forma programática, puede invocarse {@code app.stop()} o integrar
     * un shutdown hook si se requiere liberación de recursos adicional.</p>
     *
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        Javalin app = Javalin.create(cfg -> {
            // Publica archivos estáticos empaquetados en src/main/resources/public
            cfg.staticFiles.add(s -> {
                s.directory = "/public";
                s.location = io.javalin.http.staticfiles.Location.CLASSPATH;
                s.hostedPath = "/public"; // expone /public/index.html
            });
        }).start(7070);

        // Registra endpoints de la API y la redirección inicial
        Rutas.registrar(app);

        System.out.println("Legacy Library running at http://localhost:7070");
    }
}
