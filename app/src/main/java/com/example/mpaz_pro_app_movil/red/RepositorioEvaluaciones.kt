package com.example.mpaz_pro_app_movil.red

// Datos fijos del estudiante de prueba: el mismo código y curso en toda la app.
object DatosEstudianteMpaz {
    const val CODIGO = "EST-2B-001"
    const val CURSO = "2° Básico B"
}

// Lo que el estudiante ve al rendir: sin la alternativa correcta.
data class PreguntaRendible(val id: Long, val enunciado: String, val alternativas: List<AlternativaRendible>)
data class AlternativaRendible(val id: Long, val texto: String)

// Lo que el estudiante necesita para rendir una asignación.
data class PruebaRemota(
    val asignacionId: Long,
    val intentosUsados: Int,
    val maxIntentos: Int,
    val mejorNota: Double?,
    val permiteRevisar: Boolean,
    val preguntas: List<PreguntaRendible>
)

class RepositorioEvaluaciones(private val api: ApiMpaz = RedMpaz.api) {

    suspend fun preguntasParaRendir(pruebaId: Long): List<PreguntaRendible> =
        api.preguntas(pruebaId).mapNotNull { p ->
            val pregunta = p.pregunta ?: return@mapNotNull null
            PreguntaRendible(
                id = pregunta.id ?: return@mapNotNull null,
                enunciado = pregunta.enunciado.orEmpty(),
                alternativas = p.alternativas.orEmpty()
                    .map { AlternativaRendible(it.id ?: 0L, it.texto.orEmpty()) }
            )
        }

    // respuestas: preguntaId -> alternativaId elegida
    suspend fun rendirIntento(asignacionId: Long, respuestas: Map<Long, Long>): IntentoApi =
        api.rendirIntento(
            asignacionId,
            IntentoRequestApi(
                estudianteCodigo = DatosEstudianteMpaz.CODIGO,
                respuestas = respuestas.map { (preguntaId, alternativaId) ->
                    RespuestaRequestApi(preguntaId, alternativaId)
                }
            )
        )

    suspend fun revisarIntento(intentoId: Long): IntentoRevisionApi =
        api.revisarIntento(intentoId, DatosEstudianteMpaz.CODIGO)

    suspend fun miAvance(): List<AvanceApi> = api.avance(DatosEstudianteMpaz.CODIGO)

    // Datos de una asignación para el estudiante: intentos, mejor nota y preguntas.
    suspend fun pruebaDeAsignacion(asignacionId: Long): PruebaRemota {
        val asignacion = api.asignaciones(DatosEstudianteMpaz.CURSO)
            .firstOrNull { it.id == asignacionId }
            ?: throw IllegalStateException("La asignación ya no existe")
        val avance = api.avance(DatosEstudianteMpaz.CODIGO)
            .firstOrNull { it.asignacionId == asignacionId }

        return PruebaRemota(
            asignacionId = asignacionId,
            intentosUsados = avance?.intentosUsados ?: 0,
            maxIntentos = asignacion.maxIntentos,
            mejorNota = avance?.mejorNota,
            permiteRevisar = asignacion.permiteRevisar,
            preguntas = preguntasParaRendir(asignacion.pruebaId)
        )
    }
}

