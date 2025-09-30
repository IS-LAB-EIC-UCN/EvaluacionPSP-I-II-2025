-- Order matters: Socio, Materiales, then Prestamo
INSERT INTO Socio (id, nombre, premium) VALUES (1,'Ana Perez',1);
INSERT INTO Socio (id, nombre, premium) VALUES (2,'Juan Diaz',0);

INSERT INTO Libro (id, titulo, autorOEditor, isbn, paginas) VALUES (1,'Clean Architecture','R. Martin','978-0134494166',450);
INSERT INTO Libro (id, titulo, autorOEditor, isbn, paginas) VALUES (2,'Design Patterns','GoF','978-0201633610',395);
INSERT INTO Libro (id, titulo, autorOEditor, isbn, paginas) VALUES (3,'Refactoring','M. Fowler','978-0201485677',448);

INSERT INTO Revista (id, titulo, autorOEditor, numeroEdicion) VALUES (1,'ACM Queue','ACM',182);
INSERT INTO Revista (id, titulo, autorOEditor, numeroEdicion) VALUES (2,'IEEE Software','IEEE',210);

INSERT INTO Video (id, titulo, autorOEditor, duracionMinutos, formato) VALUES (1,'Agile Conference Talk','J. Doe',75,'DVD');

INSERT INTO Prestamo (socio_id, idMaterial, tipoMaterial, fechaInicio, fechaVencimiento, fechaDevolucion) VALUES (1,1,'Libro','2025-09-20','2025-09-25','2025-09-27');
INSERT INTO Prestamo (socio_id, idMaterial, tipoMaterial, fechaInicio, fechaVencimiento, fechaDevolucion) VALUES (2,2,'Revista','2025-09-23','2025-09-28',NULL);
INSERT INTO Prestamo (socio_id, idMaterial, tipoMaterial, fechaInicio, fechaVencimiento, fechaDevolucion) VALUES (2,3,'Libro','2025-09-15','2025-09-21','2025-09-26');
INSERT INTO Prestamo (socio_id, idMaterial, tipoMaterial, fechaInicio, fechaVencimiento, fechaDevolucion) VALUES (1,1,'Video','2025-09-20','2025-09-28','2025-09-29');
