package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.DatosPrueba
import com.example.mpaz_pro_app_movil.datos.model.Observacion
import com.example.mpaz_pro_app_movil.datos.model.Unidad

// Observaciones del docente, guardadas en progreso-service.
// Se guardan por objetivo; la app trabaja con unidades, así que aquí se traduce.
class RepositorioObservaciones(private val api: ApiMpaz = RedMpaz.api) {

    private suspend fun objetivoDe(unidadId: Int): Long =
        api.objetivos(unidadId.toLong()).firstOrNull()?.id
            ?: throw IllegalStateException("La unidad no tiene objetivo en el servidor")

    // Observaciones de los estudiantes de prueba, en el modelo de la app.
    suspend fun listar(unidades: List<Unidad>): List<Observacion> {
        val unidadPorObjetivo = unidades.mapNotNull { unidad ->
            api.objetivos(unidad.id.toLong()).firstOrNull()?.id?.let { objetivoId ->
                objetivoId to unidad.id
            }
        }.toMap()

        return DatosPrueba.estudiantes.flatMap { estudiante ->
            api.observaciones(estudiante.codigo).mapNotNull { remota ->
                val unidadId = remota.objetivoId?.let { unidadPorObjetivo[it] }
                    ?: return@mapNotNull null
                Observacion(
                    id = (remota.id ?: 0L).toInt(),
                    estudianteId = estudiante.id,
                    unidadId = unidadId,
                    texto = remota.texto.orEmpty(),
                    fecha = remota.fecha.orEmpty()
                )
            }
        }
    }

    suspend fun guardar(estudianteCodigo: String, unidadId: Int, texto: String) {
        api.crearObservacion(
            ObservacionApi(
                estudianteCodigo = estudianteCodigo,
                objetivoId = objetivoDe(unidadId),
                texto = texto
            )
        )
    }
}