package com.example.mpaz_pro_app_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mpaz_pro_app_movil.datos.repositorio.AlmacenMpaz
import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Pregunta
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.datos.model.siguientesPermitidos

// =============================================
// VIEWMODEL DEL PERFIL UTP
// =============================================
// Las pantallas solo muestran y avisan lo que la persona tocó.
// Las reglas (qué se puede editar, qué cambio de estado es válido)
// y los guardados viven aquí.
//
// Casi todas las acciones devuelven:
//   - null          -> salió bien
//   - un texto      -> el motivo por el que no se pudo (la pantalla lo muestra)
class UtpViewModel(application: Application) : AndroidViewModel(application) {

    private val mensajeNoGuardo = "No se pudo guardar en el teléfono."

    init {
        AlmacenMpaz.iniciar(application)
    }

    val unidades: List<Unidad> get() = AlmacenMpaz.unidades

    fun unidadPorId(id: Int): Unidad? = AlmacenMpaz.unidades.firstOrNull { it.id == id }

    fun preguntasDe(unidadId: Int): List<Pregunta> =
        AlmacenMpaz.preguntas.filter { it.unidadId == unidadId }

    fun siguienteIdUnidad(): Int = (AlmacenMpaz.unidades.maxOfOrNull { it.id } ?: 0) + 1

    fun siguienteIdPregunta(): Int = (AlmacenMpaz.preguntas.maxOfOrNull { it.id } ?: 0) + 1

    // Una unidad publicada no se toca: primero hay que deshabilitarla.
    fun puedeEditarse(unidad: Unidad): Boolean = unidad.estado != EstadoUnidad.PUBLICADO

    fun crearUnidad(unidad: Unidad): String? {
        AlmacenMpaz.unidades.add(unidad)
        return if (AlmacenMpaz.guardarUnidades(getApplication())) null else mensajeNoGuardo
    }

    fun editarUnidad(unidad: Unidad): String? {
        val posicion = AlmacenMpaz.unidades.indexOfFirst { it.id == unidad.id }
        if (posicion == -1) return "La unidad ya no existe."
        if (!puedeEditarse(AlmacenMpaz.unidades[posicion])) {
            return "Una unidad publicada no se puede editar. Deshabilítala primero."
        }
        AlmacenMpaz.unidades[posicion] = unidad
        return if (AlmacenMpaz.guardarUnidades(getApplication())) null else mensajeNoGuardo
    }

    fun agregarPregunta(pregunta: Pregunta): String? {
        val unidad = unidadPorId(pregunta.unidadId) ?: return "La unidad ya no existe."
        if (!puedeEditarse(unidad)) {
            return "Una unidad publicada no admite cambios en sus preguntas."
        }
        AlmacenMpaz.preguntas.add(pregunta)
        return if (AlmacenMpaz.guardarPreguntas(getApplication())) null else mensajeNoGuardo
    }

    fun eliminarPregunta(preguntaId: Int): String? {
        val pregunta = AlmacenMpaz.preguntas.firstOrNull { it.id == preguntaId }
            ?: return "La pregunta ya no existe."
        val unidad = unidadPorId(pregunta.unidadId) ?: return "La unidad ya no existe."
        if (!puedeEditarse(unidad)) {
            return "Una unidad publicada no admite cambios en sus preguntas."
        }
        AlmacenMpaz.preguntas.remove(pregunta)
        return if (AlmacenMpaz.guardarPreguntas(getApplication())) null else mensajeNoGuardo
    }

    // Cambia el estado respetando el flujo y las reglas de cada paso.
    fun cambiarEstado(unidadId: Int, nuevo: EstadoUnidad): String? {
        val posicion = AlmacenMpaz.unidades.indexOfFirst { it.id == unidadId }
        if (posicion == -1) return "La unidad ya no existe."
        val unidad = AlmacenMpaz.unidades[posicion]

        // 1. Solo se puede avanzar (o retroceder) por el flujo permitido.
        if (nuevo !in unidad.estado.siguientesPermitidos()) {
            return "No se puede pasar de ${unidad.estado.texto} a ${nuevo.texto}."
        }

        // 2. Una unidad sin preguntas no puede salir de borrador:
        //    el docente no tendría con qué evaluar.
        if (nuevo == EstadoUnidad.EN_REVISION && preguntasDe(unidadId).isEmpty()) {
            return "Agrega al menos una pregunta antes de enviar la unidad a revisión."
        }

        AlmacenMpaz.unidades[posicion] = unidad.copy(estado = nuevo)
        return if (AlmacenMpaz.guardarUnidades(getApplication())) null else mensajeNoGuardo
    }
}