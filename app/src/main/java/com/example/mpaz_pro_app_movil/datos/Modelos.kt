package com.example.mpaz_pro_app_movil.datos

// Los cinco estados por los que pasa una unidad, en orden.
enum class EstadoUnidad(val texto: String) {
    BORRADOR("Borrador"),
    EN_REVISION("En revisión"),
    AUTORIZADO("Autorizado"),
    PUBLICADO("Publicado"),
    DESHABILITADO("Deshabilitado")
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
data class Asignacion(
    val id: Int,
    val unidadId: Int,
    val curso: String,
    val fechaInicio: String,
    val fechaFin: String,
    val maxIntentos: Int
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