package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.model.Asignacion
import com.example.mpaz_pro_app_movil.datos.model.Unidad

// Asignaciones de pruebas a cursos, guardadas en progreso-service.
class RepositorioAsignaciones(private val api: ApiMpaz = RedMpaz.api) {

    // Asigna la prueba de la unidad a un curso. Las fechas llegan en formato año-mes-día.
    suspend fun asignar(
        unidad: Unidad,
        curso: String,
        inicioIso: String,
        finIso: String,
        maxIntentos: Int,
        permiteRevisar: Boolean,
        avisarCurso: Boolean
    ): Asignacion {
        val objetivoId = api.objetivos(unidad.id.toLong()).firstOrNull()?.id
            ?: throw IllegalStateException("La unidad no tiene objetivo en el servidor")
        val prueba = RepositorioPruebas(api).pruebaDeUnidad(unidad)
            ?: throw IllegalStateException("La unidad todavía no tiene preguntas")
        val pruebaId = prueba.id
            ?: throw IllegalStateException("El servidor no devolvió el id de la prueba")

        val creada = api.crearAsignacion(
            AsignacionApi(
                pruebaId = pruebaId,
                objetivoId = objetivoId,
                curso = curso,
                fechaInicio = inicioIso,
                fechaFin = finIso,
                maxIntentos = maxIntentos,
                permiteRevisar = permiteRevisar,
                avisarCurso = avisarCurso
            )
        )
        return creada.aApp(unidad.id)
    }

    // Todas las asignaciones del servidor, en el modelo de la app.
    suspend fun todasLasAsignaciones(): List<Asignacion> {
        val unidades = RepositorioContenidos.cargarUnidades()
        val unidadPorObjetivo = unidades.mapNotNull { unidad ->
            api.objetivos(unidad.id.toLong()).firstOrNull()?.id?.let { objetivoId ->
                objetivoId to unidad.id
            }
        }.toMap()

        return api.asignaciones(null).mapNotNull { asignacion ->
            val unidadId = asignacion.objetivoId?.let { unidadPorObjetivo[it] } ?: return@mapNotNull null
            asignacion.aApp(unidadId)
        }
    }

    private fun AsignacionApi.aApp(unidadId: Int): Asignacion = Asignacion(
        id = (id ?: 0L).toInt(),
        unidadId = unidadId,
        curso = curso,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        maxIntentos = maxIntentos,
        notificarCurso = avisarCurso,
        permitirVerRespuestas = permiteRevisar
    )
}