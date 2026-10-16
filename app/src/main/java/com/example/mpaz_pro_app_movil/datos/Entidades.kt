package com.example.mpaz_pro_app_movil.datos

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import androidx.room.ColumnInfo

// =============================================
// TABLAS DE LA BASE DE DATOS (Room)
// =============================================
// Cada clase con @Entity es una tabla. Los modelos de la app
// (Unidad, Pregunta, ...) no cambian: estas clases solo sirven para guardar,
// y las funciones aEntidad() / aModelo() convierten de un lado al otro.
// Los id los asigna la app (no autogenerados), igual que antes.

// ---------- UNIDADES ----------
@Entity(tableName = "unidades")
data class UnidadEntidad(
    @PrimaryKey val id: Int,
    val asignatura: String,
    val titulo: String,
    val codigoOA: String,
    val objetivo: String,
    val explicacion: String,
    val ejemplo: String,
    // El estado se guarda con su nombre, por ejemplo "BORRADOR".
    val estado: String
)

fun Unidad.aEntidad() = UnidadEntidad(
    id = id,
    asignatura = asignatura,
    titulo = titulo,
    codigoOA = codigoOA,
    objetivo = objetivo,
    explicacion = explicacion,
    ejemplo = ejemplo,
    estado = estado.name
)

fun UnidadEntidad.aModelo() = Unidad(
    id = id,
    asignatura = asignatura,
    titulo = titulo,
    codigoOA = codigoOA,
    objetivo = objetivo,
    explicacion = explicacion,
    ejemplo = ejemplo,
    // Si el texto no coincide con ningún estado, vuelve a Borrador.
    estado = runCatching { EstadoUnidad.valueOf(estado) }
        .getOrDefault(EstadoUnidad.BORRADOR)
)

// ---------- PREGUNTAS ----------
@Entity(tableName = "preguntas")
data class PreguntaEntidad(
    @PrimaryKey val id: Int,
    val unidadId: Int,
    val texto: String,
    // Las cuatro alternativas se guardan juntas como texto JSON: ["a","b","c","d"]
    val alternativas: String,
    val correcta: Int,
    val esPruebaFinal: Boolean
)

fun Pregunta.aEntidad() = PreguntaEntidad(
    id = id,
    unidadId = unidadId,
    texto = texto,
    alternativas = JSONArray(alternativas).toString(),
    correcta = correcta,
    esPruebaFinal = esPruebaFinal
)

fun PreguntaEntidad.aModelo(): Pregunta {
    val arreglo = JSONArray(alternativas)
    return Pregunta(
        id = id,
        unidadId = unidadId,
        texto = texto,
        alternativas = (0 until arreglo.length()).map { arreglo.getString(it) },
        correcta = correcta,
        esPruebaFinal = esPruebaFinal
    )
}

// ---------- ASIGNACIONES ----------
@Entity(tableName = "asignaciones")
data class AsignacionEntidad(
    @PrimaryKey val id: Int,
    val unidadId: Int,
    val curso: String,
    // Fechas en texto ISO: "2026-12-15"
    val fechaInicio: String,
    val fechaFin: String,
    val maxIntentos: Int,
    // Room guarda los Boolean como 1 (sí) o 0 (no).
    @ColumnInfo(defaultValue = "1") val notificarCurso: Boolean,
    @ColumnInfo(defaultValue = "1") val permitirVerRespuestas: Boolean
)

fun Asignacion.aEntidad() = AsignacionEntidad(
    id = id,
    unidadId = unidadId,
    curso = curso,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    maxIntentos = maxIntentos,
    notificarCurso = notificarCurso,
    permitirVerRespuestas = permitirVerRespuestas
)

fun AsignacionEntidad.aModelo() = Asignacion(
    id = id,
    unidadId = unidadId,
    curso = curso,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    maxIntentos = maxIntentos,
    notificarCurso = notificarCurso,
    permitirVerRespuestas = permitirVerRespuestas
)

// ---------- OBSERVACIONES DEL DOCENTE ----------
@Entity(tableName = "observaciones")
data class ObservacionEntidad(
    @PrimaryKey val id: Int,
    val estudianteId: Int,
    val unidadId: Int,
    val texto: String,
    val fecha: String
)

fun Observacion.aEntidad() = ObservacionEntidad(
    id = id,
    estudianteId = estudianteId,
    unidadId = unidadId,
    texto = texto,
    fecha = fecha
)

fun ObservacionEntidad.aModelo() = Observacion(
    id = id,
    estudianteId = estudianteId,
    unidadId = unidadId,
    texto = texto,
    fecha = fecha
)