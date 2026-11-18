package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.model.Pregunta
import com.example.mpaz_pro_app_movil.datos.model.Unidad

// Preguntas de la prueba final de cada unidad, guardadas en actividades-service.
class RepositorioPruebas(private val api: ApiMpaz = RedMpaz.api) {

    // La prueba de una unidad es la del primer objetivo de esa unidad.
    private suspend fun pruebaDeUnidad(unidad: Unidad): PruebaApi? {
        val objetivoId = api.objetivos(unidad.id.toLong()).firstOrNull()?.id ?: return null
        return api.pruebas(objetivoId).firstOrNull()
    }

    // Igual que pruebaDeUnidad, pero crea la prueba si todavía no existe.
    private suspend fun asegurarPrueba(unidad: Unidad): PruebaApi {
        pruebaDeUnidad(unidad)?.let { return it }
        val objetivoId = api.objetivos(unidad.id.toLong()).firstOrNull()?.id
            ?: throw IllegalStateException("La unidad no tiene objetivo en el servidor")
        return api.crearPrueba(
            PruebaApi(
                objetivoId = objetivoId,
                titulo = "Prueba final: ${unidad.titulo}",
                limiteIntentos = 3,
                estado = "Borrador"
            )
        )
    }

    // Las preguntas de la prueba de la unidad, con el id del backend como id de la app.
    suspend fun preguntasDeUnidad(unidad: Unidad): List<Pregunta> {
        val pruebaId = pruebaDeUnidad(unidad)?.id ?: return emptyList()
        return api.preguntas(pruebaId).mapNotNull { p ->
            val pregunta = p.pregunta ?: return@mapNotNull null
            val id = pregunta.id ?: return@mapNotNull null
            val alternativas = p.alternativas.orEmpty()
            Pregunta(
                id = id.toInt(),
                unidadId = unidad.id,
                texto = pregunta.enunciado.orEmpty(),
                alternativas = alternativas.map { it.texto.orEmpty() },
                correcta = alternativas.indexOfFirst { it.correcta == true }.coerceAtLeast(0),
                esPruebaFinal = true
            )
        }
    }

    // Guarda la pregunta en la prueba de la unidad y devuelve la pregunta con el id del servidor.
    suspend fun agregarPregunta(unidad: Unidad, pregunta: Pregunta, orden: Int): Pregunta {
        val pruebaId = asegurarPrueba(unidad).id
            ?: throw IllegalStateException("El servidor no devolvió el id de la prueba")
        val creada = api.crearPregunta(
            PreguntaRequestApi(
                pruebaId = pruebaId,
                enunciado = pregunta.texto,
                orden = orden,
                alternativas = pregunta.alternativas.mapIndexed { indice, texto ->
                    AlternativaRequestApi(texto = texto, correcta = indice == pregunta.correcta)
                }
            )
        )
        val id = creada.pregunta?.id
            ?: throw IllegalStateException("El servidor no devolvió el id de la pregunta")
        return pregunta.copy(id = id.toInt(), esPruebaFinal = true)
    }

    suspend fun eliminarPregunta(preguntaId: Int) {
        api.eliminarPregunta(preguntaId.toLong())
    }
}