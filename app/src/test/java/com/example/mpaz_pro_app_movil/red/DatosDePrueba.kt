package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Unidad

// Datos y ayudas que comparten las pruebas de los repositorios.

internal fun unidadDePrueba(
    id: Int = 1,
    asignatura: String = "Matemáticas",
    titulo: String = "Unidad 1",
    estado: EstadoUnidad = EstadoUnidad.PUBLICADO
) = Unidad(
    id = id,
    asignatura = asignatura,
    titulo = titulo,
    codigoOA = "OA-MAT-01",
    objetivo = "Contar hasta 100",
    explicacion = "Contamos de 10 en 10",
    ejemplo = "10, 20, 30",
    estado = estado
)

internal fun objetivoApi(
    id: Long?,
    unidadId: Long = 1L,
    codigo: String? = "OA-MAT-01",
    descripcion: String? = "Contar hasta 100"
) = ObjetivoApi(
    id = id,
    unidadId = unidadId,
    codigo = codigo,
    descripcion = descripcion,
    ejeCurricular = null,
    textoReferencia = null,
    estado = "Publicado",
    responsableUtp = null,
    fechaActualizacion = null
)

internal fun unidadApi(
    id: Long?,
    asignaturaId: Long? = 1L,
    titulo: String? = "Unidad 1",
    estado: String? = "Publicado"
) = UnidadApi(
    id = id,
    asignaturaId = asignaturaId,
    titulo = titulo,
    orden = 1,
    estado = estado,
    responsableUtp = null,
    fechaActualizacion = null
)

internal fun contenidoApi(
    id: Long?,
    objetivoId: Long? = 10L,
    explicacion: String? = "Explicación",
    ejemplo: String? = "Ejemplo"
) = ContenidoApi(
    id = id,
    objetivoId = objetivoId,
    titulo = "Contenido",
    explicacionEscrita = explicacion,
    archivoAudio = null,
    imagenes = null,
    ejemplosGuiados = ejemplo,
    instrucciones = null,
    orden = 1,
    responsableOrigen = null
)

internal fun asignacionApi(
    id: Long?,
    pruebaId: Long = 100L,
    objetivoId: Long = 10L,
    curso: String = "2° Básico B",
    maxIntentos: Int = 3,
    permiteRevisar: Boolean = true,
    avisarCurso: Boolean = true
) = AsignacionApi(
    id = id,
    pruebaId = pruebaId,
    objetivoId = objetivoId,
    curso = curso,
    fechaInicio = "2026-12-01",
    fechaFin = "2026-12-15",
    maxIntentos = maxIntentos,
    permiteRevisar = permiteRevisar,
    avisarCurso = avisarCurso
)

// Ejecuta el bloque y devuelve el error que lanzó. Si no falla, la prueba falla.
internal suspend inline fun <reified T : Throwable> fallaCon(bloque: suspend () -> Unit): T {
    try {
        bloque()
    } catch (error: Throwable) {
        if (error is T) return error
        throw error
    }
    throw AssertionError("Se esperaba un error ${T::class.simpleName} y no falló")
}