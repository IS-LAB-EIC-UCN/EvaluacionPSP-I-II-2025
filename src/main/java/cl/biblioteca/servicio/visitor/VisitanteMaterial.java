package cl.biblioteca.servicio.visitor;

import cl.biblioteca.dominio.Libro;
import cl.biblioteca.dominio.Revista;
import cl.biblioteca.dominio.Video;

public interface VisitanteMaterial {

    void visitar(Libro libro);
    void visitar(Revista revista);
    void visitar(Video video);


}
