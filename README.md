# Primera Evaluación PSP II-2025

## Puntaje: 100pts.

---

Una pequeña biblioteca universitaria mantiene desde hace años un sistema para registrar **préstamos**, **devoluciones** 
y **multas**. Con el crecimiento del catálogo, surgieron nuevos tipos de materiales (**libros**, **revistas** y **audiovisuales**), 
y el personal necesita **emitir reportes** (inventario resumido, reportes contables de multas, exportaciones a CSV/HTML, etc.) 
sin tocar continuamente las clases de datos existentes.

Además, la política de multas cambió: ahora pueden aplicarse **recargos combinables** según reglas que aparecen y desaparecen 
durante el semestre (por ejemplo: *recargo por día de atraso*, *descuento por socio*, *exención en feriados*, *sobrecargo 
por alta demanda*). El sistema actual resolvió esto con banderas y condicionales en cadena, y se ha vuelto difícil **agregar o quitar reglas** 
sin reescribir código. A la encargada le gustaría poder **componer** estos recargos “como capas” al vuelo.

> **Nota:** No hay problemática arquitectural de capas en este ejercicio; concéntrese exclusivamente en el **refactor del comportamiento** con dos mecanismos: uno para **recorrer** materiales al construir reportes y otro para **envolver** el cálculo de multas con reglas combinables.

## Objetivo

Refactorice el sistema para que:

1. Sea posible **agregar nuevas operaciones sobre los materiales** (reportes/exportaciones) **sin modificar** las clases 
concretas `Libro`, `Revista`, `Video`.
2. Las **reglas de multa** puedan **activarse, desactivarse y componerse** en tiempo de ejecución, como si se **apilaran
capas**.
3. El **comportamiento final** (datos de salida y montos) permanezca **idéntico** al del legado para las operaciones 
solicitadas.

## Operaciones de reporte/exportación requeridas

Implemente las siguientes operaciones sobre el **inventario** (aplican a `Libro`, `Revista`, `Video`):

1. **Estadísticas de inventario**: genere un objeto JSON con contadores y agregados por tipo calculados a partir de los 
campos existentes en las entidades: 

   1. Libros (Libro): total, paginas_totales y paginas_promedio, usando Libro.obtenerPaginas().
   2. Revistas (Revista): total (con Revista.obtenerNumeroEdicion() puedes añadir métricas adicionales si lo deseas, 
      pero no es obligatorio).
   3. Videos (Video): total y duracion_total_min, usando Video.obtenerDuracionMinutos().

La construcción debe hacerse recorriendo los materiales, que acumule valores sin usar instanceof. El resultado se expone
como un JSON sin cambiar el comportamiento del legado ni la estructura de datos existente.

**Ejemplo**:
```json
{
    "libros": {
    "total": 3,
    "paginas_totales": 1293,
    "paginas_promedio": 431.0
    },
    "revistas": {
    "total": 2
    },
    "videos": {
    "total": 1,
    "duracion_total_min": 75
    },
    "totales": {
    "materiales": 6
    }
}
```
Método que crea un json:
```java
/** Devuelve el JSON final como String (bonito y sin dependencias extra). */
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
   return json.toString();
}
```

> Sugerencia: piense en un mecanismo que permita **agregar nuevas operaciones** (CSV, HTML, estadísticas adicionales) 
sin tocar las clases de los materiales.

## Reglas de multa requeridas

Base y reglas a conservar (idénticas al legado):

1. **Base**: $100 por día de atraso. Si no hay devolución (**`fechaDevolucion == null`**), multa 0.
2. **Descuento por socio premium**: 20% de descuento (se aplica sobre el valor vigente hasta ese momento).
3. **Sobrecargo por alta demanda**: si hay más de 3 días de atraso, sumar $200 fijos.
4. **Exención por feriado**: si el **vencimiento cae en domingo**, la multa queda en 0.

> Sugerencia: considere “**adicionar**” un cálculo base con piezas como `ExencionFeriado`, `DescuentoPremium`, 
`SobrecargoAltaDemanda`, que se puedan **componer** dinámicamente.

## Pistas de refactorización

1. **Operaciones sobre materiales**: actualmente `ServicioReportes` arma filas por tipo. Considere una forma de **agregar
operaciones** (CSV, HTML, estadísticas, futuras exportaciones) **sin modificar** las clases concretas cada vez.
2. **Cálculo de multas**: `CalculadoraMultas` usa **banderas** y condicionales. Piense en **envolver** el cálculo base 
con piezas independientes (`ExencionFeriado`, `DescuentoPremium`, `SobrecargoAltaDemanda`) que puedan **apilarse**.
3. **Mantener el comportamiento**: agregue pruebas o *asserts* que comparen las salidas del legado y del refactor para 
los mismos datos de entrada.

## Considerandos

### Lo que usted debe hacer:

1. Cree un archivo Explicacion.txt que explique cuales son los patrones más adecuados para solucionar la problemática
que enfrenta el sistema. Use los conceptos de propiedades y principios de diseño para justificar su respuesta. 25pts
2. Implemente **solo un patrón** para solucionar la problemática escogida, ya sea para Operaciones de reporte/exportación requeridas
o para Reglas de multa requeridas. 75pts.

### Ejecución:

```bash
mvn -DskipTests=true clean compile exec:java
```
Para detener la ejecución del sistema presionar las teclas **Ctrl+C**.

### Documentación:

[Documentación del Proyecto](documentacion/apidocs/index.html)

