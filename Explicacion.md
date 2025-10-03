# Explicación de la Solución (Visitor + Decorator)

## 1) Contexto del legado y problema a resolver

- **Reportes**: el servicio `ServicioReportes.resumenInventario()` consulta por cada tipo concreto (`Libro`, `Revista`, `Video`) y arma manualmente filas `{type, title, meta}`. Cada nueva operación de reporte (p. ej., CSV, HTML, estadísticas) obliga a **modificar** esa clase (y usualmente duplicar condicionales por tipo).
- **Multas**: la clase `CalculadoraMultas` usa **banderas** y condicionales encadenados (exención, descuento, sobrecargo). Agregar/quitar reglas implica **tocar** la misma clase, mezclar responsabilidades y elevar el riesgo de regresiones.

---

## 2) Objetivos de la refactorización

1. **Nuevas operaciones de reporte sin modificar entidades** (`Libro`, `Revista`, `Video`) ni reescribir la lógica por tipo cada vez.
2. **Reglas de multa componibles en tiempo de ejecución** (activar/desactivar/ordenar sin ifs gigantes).
3. **Comportamiento idéntico** al legado en las operaciones exigidas (mismos datos y montos).

---

## 3) Patrón Visitor para reportes

### 3.1. Qué cambia
Se introduce el despacho doble:
- **Contrato** `VisitanteMaterial` con `visitar(Libro)`, `visitar(Revista)`, `visitar(Video)`.
- Las entidades agregan un único método: `aceptar(VisitanteMaterial v)`.
- Cada operación de reporte se implementa como **un visitante nuevo** (p. ej., *Estadísticas*, *CSV*), **sin if/instanceof**.

```java
// Contrato
public interface VisitanteMaterial {
  void visitar(Libro l); void visitar(Revista r); void visitar(Video v);
}

// En cada entidad concreta
@Override public void aceptar(VisitanteMaterial v) { v.visitar(this); }
```

### 3.2. Ejemplos implementados
- **Estadísticas de inventario → JSON** (`VisitanteEstadisticasInventarioJson`): acumula
  - Libros: `total`, `paginas_totales`, `paginas_promedio` (usa `obtenerPaginas()`).
  - Revistas: `total`.
  - Videos: `total`, `duracion_total_min` (usa `obtenerDuracionMinutos()`).

  El visitante devuelve un `String` JSON usando **Jakarta JSON-P** (sin añadir librerías pesadas).

- **Inventario resumido → CSV** (`VisitanteInventarioResumidoCsv`): produce `type,title,meta` sin `instanceof`, manteniendo la compatibilidad con el endpoint legado.

> **Integración**: 
> - Opción A (recomendada): nuevo endpoint **`GET /api/inventory/stats`** que llama a `ServicioReportes.estadisticasInventarioComoJson()`.
> - Opción B (sin tocar rutas): usar el método de servicio que retorna `String` JSON directamente (tests/CLI) y, si se desea, sustituir internamente la generación de `/api/inventory`.

### 3.3. Principios y propiedades de diseño atendidos

- **OCP – Open/Closed**: para **nuevas operaciones** (CSV, HTML, auditoría) creamos **nuevos visitantes** sin modificar `Libro/Revista/Video` ni el servicio que orquesta.  
- **SRP – Single Responsibility**: cada visitante **solo** gestiona una operación (p. ej., *calcular estadísticas*), y `ServicioReportes` **solo** orquesta consultas JPA y el recorrido.  
- **Bajo acoplamiento / Alta cohesión**: la operación se aísla (visitante), las entidades no conocen formatos de salida.


---

## 4) Patrón Decorator para multas

### 4.1. Qué cambia
Se reemplaza la clase monolítica con banderas por una **cadena de decoradores**:

```
MultaBase → ExencionFeriado → DescuentoPremium → SobrecargoAltaDemanda
```

Cada regla es **un objeto** que “envuelve” al anterior. Así, la política se arma/combine **en tiempo de ejecución**.

```java
public interface CalculadoraDeMulta { double calcular(Prestamo p); }

public class MultaBase implements CalculadoraDeMulta { /* $100 x día */ }

public abstract class MultaDecorador implements CalculadoraDeMulta {
  protected final CalculadoraDeMulta siguiente;
  /* helpers: días de atraso, domingo, etc. */
}

public class ExencionFeriado extends MultaDecorador { /* si domingo => 0 */ }
public class DescuentoPremium extends MultaDecorador { /* *0.8 */ }
public class SobrecargoAltaDemanda extends MultaDecorador { /* +200 si atraso>3 */ }
```

**Fábrica** para construir la cadena en el **orden legado**:

```java
public static CalculadoraDeMulta crear(boolean exencion, boolean descuento, boolean sobrecargo) {
  CalculadoraDeMulta calc = new MultaBase();
  if (exencion)  calc = new ExencionFeriado(calc);
  if (descuento) calc = new DescuentoPremium(calc);
  if (sobrecargo) calc = new SobrecargoAltaDemanda(calc);
  return calc;
}
```

> **Integración web**: endpoint **`GET /api/fees/{prestamoId}`** con `?exencionFeriado=&descuentoPremium=&sobrecargoAltaDemanda=` que devuelve el **monto** y un **desglose** paso a paso.  
> (Recuerda que en **Javalin 4** los path params van como `{prestamoId}`.)

### 4.2. Principios y propiedades de diseño atendidos

- **OCP – Open/Closed**: agregar una **nueva regla** es crear **otro decorador** e insertarlo en la cadena; no se toca código existente.  
- **SRP – Single Responsibility**: cada clase encapsula **una** regla (exención, descuento, sobrecargo).  
- **Composición sobre herencia**: se combinan objetos en tiempo de ejecución, evitando jerarquías rígidas y/o combinatorias de subclases.  


---

### Conclusión

- **Visitor** resolvió la **extensibilidad de operaciones** sobre los materiales con **bajo acoplamiento** y **SRP**, cumpliendo **OCP** para nuevas operaciones.  
- **Decorator** permitió **componer reglas** de multas en tiempo de ejecución, manteniendo el **orden y comportamiento** del legado, cumpliendo **OCP**, **SRP** y “**composición sobre herencia**”.  
- La solución es **modular, testeable** y lista para **extender** sin reescribir código existente.
