package com.example.mpaz_pro_app_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mpaz_pro_app_movil.datos.Asignacion
import com.example.mpaz_pro_app_movil.datos.AlmacenMpaz
import com.example.mpaz_pro_app_movil.datos.ConfigPruebas
import com.example.mpaz_pro_app_movil.datos.DatosPrueba
import com.example.mpaz_pro_app_movil.datos.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.Observacion
import com.example.mpaz_pro_app_movil.datos.Unidad
import com.example.mpaz_pro_app_movil.validacion.Fechas
import com.example.mpaz_pro_app_movil.validacion.Validador

// Resultados reales de una unidad: lo que el estudiante hizo en su prueba.
data class InformeUnidad(
    val unidad: Unidad,
    val asignaciones: List<Asignacion>,
    val intentosUsados: Int,
    val maxIntentos: Int,
    val notas: List<Double>
)

// =============================================
// VIEWMODEL DEL PERFIL DOCENTE
// =============================================
// Igual que en UTP: las pantallas muestran, y aquí viven las reglas.
// Las acciones devuelven null si salió bien, o el motivo del error.
class DocenteViewModel(application: Application) : AndroidViewModel(application) {

    private val mensajeNoGuardo = "No se pudo guardar en el teléfono."

    init {
        AlmacenMpaz.iniciar(application)
    }

    // El docente solo ve las unidades que la UTP ya publicó.
    val unidadesPublicadas: List<Unidad>
        get() = AlmacenMpaz.unidades.filter { it.estado == EstadoUnidad.PUBLICADO }

    val asignaciones: List<Asignacion> get() = AlmacenMpaz.asignaciones

    val observaciones: List<Observacion> get() = AlmacenMpaz.observaciones

    fun unidadPorId(id: Int): Unidad? = AlmacenMpaz.unidades.firstOrNull { it.id == id }

    fun asignacionesDe(unidadId: Int): List<Asignacion> =
        AlmacenMpaz.asignaciones.filter { it.unidadId == unidadId }

    // Asigna una unidad a un curso. Las fechas llegan como las escribió la
    // persona (dd/mm/aaaa) y se guardan en formato año-mes-día.
    fun asignar(
        unidadId: Int,
        curso: String,
        inicioVisible: String,
        finVisible: String,
        maxIntentos: Int
    ): String? {
        val unidad = unidadPorId(unidadId) ?: return "La unidad ya no existe."

        // Bloqueo 1: solo unidades publicadas.
        if (unidad.estado != EstadoUnidad.PUBLICADO) {
            return "Solo se pueden asignar unidades publicadas."
        }

        // Bloqueo 2: no repetir la misma unidad en el mismo curso.
        if (AlmacenMpaz.asignaciones.any { it.unidadId == unidadId && it.curso == curso }) {
            return "Esta unidad ya está asignada a $curso."
        }

        // Bloqueo 3: fechas e intentos válidos (se revisan otra vez aquí
        // por seguridad, aunque el formulario ya los revisó).
        val inicio = Fechas.aIso(inicioVisible) ?: return "La fecha de inicio no es válida."
        val fin = Fechas.aIso(finVisible) ?: return "La fecha de término no es válida."
        Validador.rangoFechasIso(inicio, fin)?.let { return it }
        Validador.intentos(maxIntentos)?.let { return it }

        val nueva = Asignacion(
            id = (AlmacenMpaz.asignaciones.maxOfOrNull { it.id } ?: 0) + 1,
            unidadId = unidadId,
            curso = curso,
            fechaInicio = inicio,
            fechaFin = fin,
            maxIntentos = maxIntentos
        )
        AlmacenMpaz.asignaciones.add(nueva)

        // La prueba del estudiante consulta este mismo límite.
        ConfigPruebas.guardarMaxIntentos(getApplication(), unidad.asignatura, maxIntentos)

        return if (AlmacenMpaz.guardarAsignaciones(getApplication())) null else mensajeNoGuardo
    }

    // Informe protegido: solo existe para unidades que ya fueron asignadas.
    fun informes(): List<InformeUnidad> {
        val aplicacion = getApplication<Application>()
        return AlmacenMpaz.asignaciones
            .groupBy { it.unidadId }
            .mapNotNull { (unidadId, lista) ->
                val unidad = unidadPorId(unidadId) ?: return@mapNotNull null
                InformeUnidad(
                    unidad = unidad,
                    asignaciones = lista,
                    intentosUsados = ConfigPruebas.intentosUsados(aplicacion, unidad.asignatura),
                    maxIntentos = lista.maxOf { it.maxIntentos },
                    notas = ConfigPruebas.notas(aplicacion, unidad.asignatura)
                )
            }
    }

    fun agregarObservacion(estudianteId: Int, unidadId: Int, texto: String): String? {
        if (DatosPrueba.estudiantes.none { it.id == estudianteId }) {
            return "El estudiante no existe."
        }
        // Bloqueo: solo se comenta una unidad que fue asignada.
        if (AlmacenMpaz.asignaciones.none { it.unidadId == unidadId }) {
            return "Solo se puede comentar una unidad que ya fue asignada."
        }

        AlmacenMpaz.observaciones.add(
            Observacion(
                id = (AlmacenMpaz.observaciones.maxOfOrNull { it.id } ?: 0) + 1,
                estudianteId = estudianteId,
                unidadId = unidadId,
                texto = texto.trim(),
                fecha = Fechas.hoyIso()
            )
        )
        return if (AlmacenMpaz.guardarObservaciones(getApplication())) null else mensajeNoGuardo
    }
}