# Primera Evaluación PSP II-2025

---

Una pequeña biblioteca universitaria mantiene desde hace años un sistema casero para registrar préstamos, devoluciones 
y multas. Con el crecimiento del catálogo, surgieron nuevos tipos de materiales (libros, revistas y audiovisuales), y el 
personal necesita emitir distintos reportes (inventario resumido, reportes contables de multas, exportaciones a CSV/HTML, 
etc.) sin tocar continuamente las clases de datos existentes.

Además, la política de multas cambió: ahora pueden aplicarse recargos combinables según reglas que aparecen y desaparecen 
durante el semestre (por ejemplo: recargo por día de atraso, descuento por socio, exención en feriados, sobrecargo por 
alta demanda). El sistema actual resolvió esto con banderas y condicionales en cadena, y se ha vuelto difícil agregar o 
quitar reglas sin reescribir código. A la encargada le gustaría poder componer estos recargos “como capas” al vuelo.

## Objetivo

Refactorice el sistema para que:

1. Sea posible agregar nuevas operaciones sobre los materiales (reportes/exportaciones) sin modificar las clases 
concretas Libro, Revista, Video.
2. Las reglas de multa puedan activarse, desactivarse y componerse en tiempo de ejecución, como si se apilaran capas.
3. El comportamiento final (datos de salida y montos) permanezca idéntico al del legado para las operaciones solicitadas.

## Operaciones de reporte/exportación requeridas 

Implemente las siguientes operaciones sobre el inventario (aplican a Book, Magazine, Video):

1. Inventario resumido (JSON): lista de filas con type, title y un campo meta (ya existe en el legado y debe preservarse).
2. Estadísticas de inventario: objeto con contadores y agregados por tipo (p. ej., número de libros, suma de páginas, duración total de videos).

## Reglas de multa requeridas (para el mecanismo de envolturas)

Base y reglas a conservar (idénticas al legado):

1. Base: $100 por día de atraso. Si no hay devolución (returnedDate == null), multa 0.
2. Descuento por socio premium: 20% de descuento (se aplica sobre el valor vigente hasta ese momento).
3. Sobrecarga por alta demanda: si hay más de 3 días de atraso, sumar $200 fijos.
4. Exención por feriado: si el vencimiento cae en domingo (política actual de ejemplo), la multa queda en 0.

## Pistas de refactorización 

1. Operaciones sobre materiales: actualmente ReportService arma filas por tipo. Considere una forma de agregar 
operaciones (CSV, HTML, estadísticas, futuras exportaciones) sin modificar las clases concretas cada vez.

2. Cálculo de multas: FeeCalculator usa banderas y condicionales. Piense en envolver el cálculo base con piezas 
independientes (ExenciónFeriado, DescuentoPremium, SobrecargoDemanda) que puedan apilarse.

3. Mantener el comportamiento: agregue pruebas o asserts que comparen las salidas del legado y del refactor para los 
mismos datos de entrada.


# Ejecución

    mvn -DskipTests=true clean compile exec:java

    
