package com.example.mpaz_pro_app_movil.datos

// Los cinco estados por los que pasa una unidad, en orden.
enum class EstadoUnidad(val texto: String) {
    BORRADOR("Borrador"),
    EN_REVISION("En revisión"),
    AUTORIZADO("Autorizado"),
    PUBLICADO("Publicado"),
    DESHABILITADO("Deshabilitado")
}

// A qué estados puede pasar una unidad desde el estado en que está.
// Es la regla del flujo: nadie puede saltarse pasos.
//   Borrador -> En revisión -> Autorizado -> Publicado -> Deshabilitado
// Una unidad en revisión puede devolverse a borrador, y una
// deshabilitada puede volver a publicarse.
fun EstadoUnidad.siguientesPermitidos(): List<EstadoUnidad> = when (this) {
    EstadoUnidad.BORRADOR -> listOf(EstadoUnidad.EN_REVISION)
    EstadoUnidad.EN_REVISION -> listOf(EstadoUnidad.AUTORIZADO, EstadoUnidad.BORRADOR)
    EstadoUnidad.AUTORIZADO -> listOf(EstadoUnidad.PUBLICADO)
    EstadoUnidad.PUBLICADO -> listOf(EstadoUnidad.DESHABILITADO)
    EstadoUnidad.DESHABILITADO -> listOf(EstadoUnidad.PUBLICADO)
}

// Una unidad didáctica: lo que crea la UTP para una asignatura.
data class Unidad(
    val id: Int,
    val asignatura: String,
    val titulo: String,
    val codigoOA: String,
    val objetivo: String,
    val explicacion: String,
    val ejemplo: String,
    val estado: EstadoUnidad
)

// Una pregunta de una unidad.
// un texto, cuatro alternativas y la posición de la correcta (0 a 3).
data class Pregunta(
    val id: Int,
    val unidadId: Int,
    val texto: String,
    val alternativas: List<String>,
    val correcta: Int,
    val esPruebaFinal: Boolean
)

// Estudiante ficticio. Solo código y curso: nunca nombres ni RUT reales.
data class Estudiante(
    val id: Int,
    val codigo: String,
    val curso: String
)

// Lo que define el docente al asignar una unidad a un curso.
// Las fechas se guardan como texto ISO: "2026-12-15".
data class Asignacion(
    val id: Int,
    val unidadId: Int,
    val curso: String,
    val fechaInicio: String,
    val fechaFin: String,
    val maxIntentos: Int,
    // Si el docente quiere avisar al curso con una notificación.
    val notificarCurso: Boolean = true,
    // Si el estudiante puede revisar sus respuestas después de la prueba.
    val permitirVerRespuestas: Boolean = true
)

// El resultado de un intento de la prueba final.
data class Resultado(
    val id: Int,
    val estudianteId: Int,
    val unidadId: Int,
    val intento: Int,
    val puntaje: Int,
    val totalPreguntas: Int,
    val respuestas: List<Int>,
    val fecha: String
)

// Comentario que el docente deja sobre un estudiante en una unidad.
data class Observacion(
    val id: Int,
    val estudianteId: Int,
    val unidadId: Int,
    val texto: String,
    val fecha: String
)