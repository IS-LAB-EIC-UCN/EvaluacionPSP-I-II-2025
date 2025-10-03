package cl.biblioteca.servicio.visitor;

import cl.biblioteca.dominio.Libro;
import cl.biblioteca.dominio.Revista;
import cl.biblioteca.dominio.Video;
import jakarta.json.Json;
import jakarta.json.JsonObject;

public class VisitanteEstadisticasInventarioJson implements VisitanteMaterial {

    private int totalLibros, totalRevistas, totalVideos;
    private int paginasTotalesLibros, duracionTotalVideosMin;

    public void visitar(Libro l)   {
        totalLibros++;
        paginasTotalesLibros += Math.max(0, l.obtenerPaginas());
    }

    public void visitar(Revista r) { totalRevistas++; }

    public void visitar(Video v)   {
        totalVideos++;
        duracionTotalVideosMin += Math.max(0, v.obtenerDuracionMinutos());
    }

    /** Devuelve el JSON final como String. */
    public String comoJsonString() {
        double promedio = totalLibros == 0 ? 0.0 : (paginasTotalesLibros * 1.0 / totalLibros);

        JsonObject json = Json.createObjectBuilder()
                .add("libros", Json.createObjectBuilder()
                        .add("total", totalLibros)
                        .add("paginas_totales", paginasTotalesLibros)
                        .add("paginas_promedio", promedio))
                .add("revistas", Json.createObjectBuilder()
                        .add("total", totalRevistas))
                .add("videos", Json.createObjectBuilder()
                        .add("total", totalVideos)
                        .add("duracion_total_min", duracionTotalVideosMin))
                .add("totales", Json.createObjectBuilder()
                        .add("materiales", totalLibros + totalRevistas + totalVideos))
                .build();

        return json.toString(); // compacto
    }
}
