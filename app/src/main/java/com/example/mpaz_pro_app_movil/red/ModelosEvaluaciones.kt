package com.example.mpaz_pro_app_movil.red

// ---- Lo que devuelve actividades-service ----
data class PruebaApi(
    val id: Long? = null,
    val objetivoId: Long? = null,
    val titulo: String? = null,
    val limiteIntentos: Int? = null,
    val fechaHabilitacion: String? = null,
    val fechaRendicionHasta: String? = null,
    val criteriosPuntuacion: String? = null,
    val solucionario: String? = null,
    val estado: String? = null
)

data class PreguntaApi(
    val id: Long? = null,
    val desafioId: Long? = null,
    val pruebaId: Long? = null,
    val enunciado: String? = null,
    val orden: Int? = null
)

data class AlternativaApi(
    val id: Long? = null,
    val preguntaId: Long? = null,
    val texto: String? = null,
    val correcta: Boolean? = null
)

// GET /preguntas?pruebaId=X devuelve esto. Para el estudiante no trae "correcta".
data class PreguntaConAlternativasApi(
    val pregunta: PreguntaApi? = null,
    val alternativas: List<AlternativaApi>? = null
)

// ---- Lo que se envía a actividades-service ----
data class AlternativaRequestApi(val texto: String, val correcta: Boolean)

data class PreguntaRequestApi(
    val pruebaId: Long? = null,
    val enunciado: String,
    val orden: Int,
    val alternativas: List<AlternativaRequestApi>
)

// ---- progreso-service ----
data class AsignacionApi(
    val id: Long? = null,
    val pruebaId: Long,
    val objetivoId: Long,
    val curso: String,
    val fechaInicio: String,
    val fechaFin: String,
    val maxIntentos: Int,
    val permiteRevisar: Boolean,
    val avisarCurso: Boolean
)

data class RespuestaRequestApi(val preguntaId: Long, val alternativaId: Long)

data class IntentoRequestApi(
    val estudianteCodigo: String,
    val respuestas: List<RespuestaRequestApi>
)

data class IntentoApi(
    val id: Long? = null,
    val asignacionId: Long? = null,
    val estudianteCodigo: String? = null,
    val numero: Int? = null,
    val nota: Double? = null,
    val fecha: String? = null
)

data class RespuestaRevisionApi(
    val preguntaId: Long? = null,
    val alternativaId: Long? = null,
    val correcta: Boolean? = null
)

data class IntentoRevisionApi(
    val id: Long? = null,
    val asignacionId: Long? = null,
    val numero: Int? = null,
    val nota: Double? = null,
    val fecha: String? = null,
    val respuestas: List<RespuestaRevisionApi>? = null
)

data class AvanceApi(
    val asignacionId: Long? = null,
    val pruebaId: Long? = null,
    val objetivoId: Long? = null,
    val intentosUsados: Int? = null,
    val maxIntentos: Int? = null,
    val mejorNota: Double? = null,
    val promedio: Double? = null
)

data class InformeApi(
    val estudianteCodigo: String? = null,
    val intentosUsados: Int? = null,
    val mejorNota: Double? = null,
    val promedio: Double? = null
)

data class ObservacionApi(
    val id: Long? = null,
    val estudianteCodigo: String? = null,
    val objetivoId: Long? = null,
    val texto: String? = null,
    val fecha: String? = null
)

