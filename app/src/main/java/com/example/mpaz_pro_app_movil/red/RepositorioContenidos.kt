package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Unidad


// REPOSITORIO DE CONTENIDOS (backend)
// Pide al backend las unidades con su objetivo y su contenido,
// y las convierte al formato Unidad que ya usan las pantallas.
object RepositorioContenidos {

    suspend fun cargarUnidades(): List<Unidad> {
        val api = RedMpaz.api
        val asignaturas = api.asignaturas().associateBy { it.id }

        return api.unidades().map { u ->
            // Cada unidad tiene un objetivo y cada objetivo un contenido didáctico.
            val objetivo = u.id?.let { api.objetivos(it).firstOrNull() }
            val contenido = objetivo?.id?.let { api.contenidos(it).firstOrNull() }

            Unidad(
                id = (u.id ?: 0L).toInt(),
                asignatura = asignaturas[u.asignaturaId]?.nombre ?: "Sin asignatura",
                titulo = u.titulo.orEmpty(),
                codigoOA = objetivo?.codigo.orEmpty(),
                objetivo = objetivo?.descripcion.orEmpty(),
                explicacion = contenido?.explicacionEscrita.orEmpty(),
                ejemplo = contenido?.ejemplosGuiados.orEmpty(),
                estado = estadoDesdeTexto(u.estado)
            )
        }
    }

    // El backend manda el estado como texto ("En revisión", "Publicado"...).
    fun estadoDesdeTexto(texto: String?): EstadoUnidad =
        EstadoUnidad.entries.firstOrNull { it.texto == texto } ?: EstadoUnidad.BORRADOR

    // Pide al backend el cambio de estado (PATCH /unidades/{id}/estado).
    // Si el servidor lo rechaza, lanza un error.
    suspend fun cambiarEstado(unidadId: Int, nuevo: EstadoUnidad) {
        RedMpaz.api.cambiarEstadoUnidad(unidadId.toLong(), CambioEstado(nuevo.texto))
    }
}