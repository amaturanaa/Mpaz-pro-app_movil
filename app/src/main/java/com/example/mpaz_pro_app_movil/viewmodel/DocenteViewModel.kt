package com.example.mpaz_pro_app_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mpaz_pro_app_movil.datos.model.Asignacion
import com.example.mpaz_pro_app_movil.datos.repositorio.AlmacenMpaz
import com.example.mpaz_pro_app_movil.datos.ConfigPruebas
import com.example.mpaz_pro_app_movil.datos.DatosPrueba
import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Observacion
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.validacion.Fechas
import com.example.mpaz_pro_app_movil.validacion.Validador
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.example.mpaz_pro_app_movil.red.RepositorioAsignaciones
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mpaz_pro_app_movil.red.RepositorioObservaciones
import com.example.mpaz_pro_app_movil.red.RepositorioContenidos
import com.example.mpaz_pro_app_movil.red.RepositorioInformes

// Una fila del informe: un estudiante en una asignación (curso).
data class FilaInforme(
    val estudianteCodigo: String,
    val curso: String,
    val intentosUsados: Int,
    val maxIntentos: Int,
    val mejorNota: Double?
)

// Resultados reales de una unidad: lo que hicieron los estudiantes en su prueba.
data class InformeUnidad(
    val unidad: Unidad,
    val asignaciones: List<Asignacion>,
    val filas: List<FilaInforme>
)


// VIEWMODEL DEL PERFIL DOCENTE
// Igual que en UTP: las pantallas muestran, y aquí viven las reglas.
// Las acciones devuelven null si salió bien, o el motivo del error.
class DocenteViewModel(application: Application) : AndroidViewModel(application) {

    private val mensajeNoGuardo = "No se pudo guardar en el teléfono."

    init {
        AlmacenMpaz.iniciar(application)
        cargarDesdeBackend()
        cargarObservaciones()
    }

    // El docente solo ve las unidades que la UTP ya publicó.
    val unidadesPublicadas: List<Unidad>
        get() = AlmacenMpaz.unidades.filter { it.estado == EstadoUnidad.PUBLICADO }

    val asignaciones: List<Asignacion> get() = AlmacenMpaz.asignaciones

    var observaciones: List<Observacion> by mutableStateOf(emptyList())
        private set

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
        maxIntentos: Int,
        notificarCurso: Boolean = true,
        permitirVerRespuestas: Boolean = true
    ): String? {
        val unidad = unidadPorId(unidadId) ?: return "La unidad ya no existe."

        if (unidad.estado != EstadoUnidad.PUBLICADO) {
            return "Solo se pueden asignar unidades publicadas."
        }
        if (AlmacenMpaz.asignaciones.any { it.unidadId == unidadId && it.curso == curso }) {
            return "Esta unidad ya está asignada a $curso."
        }

        val inicio = Fechas.aIso(inicioVisible) ?: return "La fecha de inicio no es válida."
        val fin = Fechas.aIso(finVisible) ?: return "La fecha de término no es válida."
        Validador.rangoFechasIso(inicio, fin)?.let { return it }
        Validador.intentos(maxIntentos)?.let { return it }

        val creada = try {
            runBlocking(Dispatchers.IO) {
                RepositorioAsignaciones().asignar(
                    unidad, curso, inicio, fin, maxIntentos, permitirVerRespuestas, notificarCurso
                )
            }
        } catch (error: Exception) {
            Log.e("DocenteViewModel", "el backend no aceptó la asignación", error)
            return when {
                error is HttpException && error.code() == 409 ->
                    "Esta prueba ya fue asignada a $curso."
                error is HttpException && error.code() == 400 ->
                    "El servidor rechazó la asignación. Revisa las fechas o que la unidad tenga preguntas."
                else -> "No se pudo asignar en el servidor. Revisa la conexión."
            }
        }

        AlmacenMpaz.asignaciones.add(creada)

        // Lo que todavía usa la prueba local (se quita en el siguiente paso).
        ConfigPruebas.guardarMaxIntentos(getApplication(), unidad.id, maxIntentos)
        ConfigPruebas.guardarPermitirVerRespuestas(getApplication(), unidad.id, permitirVerRespuestas)
        if (AlmacenMpaz.asignaciones.count { it.unidadId == unidadId } == 1) {
            ConfigPruebas.reiniciarResultados(getApplication(), unidad.id)
        }

        return if (AlmacenMpaz.guardarAsignaciones(getApplication())) null else mensajeNoGuardo
    }

    // Informes de las unidades asignadas, leídos desde el backend.
    var informes: List<InformeUnidad> by mutableStateOf(emptyList())
        private set

    fun cargarInformes() {
        viewModelScope.launch {
            try {
                val asignaciones = AlmacenMpaz.asignaciones.toList()
                informes = withContext(Dispatchers.IO) {
                    val unidades = RepositorioContenidos.cargarUnidades()
                    val repo = RepositorioInformes()
                    asignaciones
                        .groupBy { it.unidadId }
                        .mapNotNull { (unidadId, lista) ->
                            val unidad = unidades.firstOrNull { it.id == unidadId }
                                ?: return@mapNotNull null
                            val filas = lista.flatMap { asignacion ->
                                repo.informeDeAsignacion(asignacion.id).map { remota ->
                                    FilaInforme(
                                        estudianteCodigo = remota.estudianteCodigo.orEmpty(),
                                        curso = asignacion.curso,
                                        intentosUsados = remota.intentosUsados ?: 0,
                                        maxIntentos = asignacion.maxIntentos,
                                        mejorNota = remota.mejorNota
                                    )
                                }
                            }
                            InformeUnidad(unidad, lista, filas)
                        }
                }
            } catch (error: Exception) {
                Log.e("DocenteViewModel", "no se pudieron cargar los informes", error)
            }
        }
    }

    fun agregarObservacion(estudianteId: Int, unidadId: Int, texto: String): String? {
        val estudiante = DatosPrueba.estudiantes.firstOrNull { it.id == estudianteId }
            ?: return "El estudiante no existe."

        // Bloqueo: solo se comenta una unidad que fue asignada.
        if (AlmacenMpaz.asignaciones.none { it.unidadId == unidadId }) {
            return "Solo se puede comentar una unidad que ya fue asignada."
        }

        try {
            runBlocking(Dispatchers.IO) {
                RepositorioObservaciones().guardar(estudiante.codigo, unidadId, texto.trim())
            }
        } catch (error: Exception) {
            Log.e("DocenteViewModel", "el backend no aceptó la observación", error)
            return if (error is HttpException && error.code() == 400) {
                "El servidor no aceptó la observación. Revisa que la unidad esté asignada."
            } else {
                "No se pudo guardar la observación. Revisa la conexión."
            }
        }

        cargarObservaciones()
        return null
    }

    fun cargarDesdeBackend() {
        viewModelScope.launch {
            try {
                val lista = withContext(Dispatchers.IO) { RepositorioAsignaciones().todasLasAsignaciones() }
                AlmacenMpaz.asignaciones.clear()
                AlmacenMpaz.asignaciones.addAll(lista)
                Log.d("DocenteViewModel", "asignaciones cargadas: ${lista.size}")
            } catch (error: Exception) {
                Log.e("DocenteViewModel", "no se pudieron cargar las asignaciones", error)
            }
        }
    }

    fun cargarObservaciones() {
        viewModelScope.launch {
            try {
                observaciones = withContext(Dispatchers.IO) {
                    RepositorioObservaciones().listar(RepositorioContenidos.cargarUnidades())
                }
            } catch (error: Exception) {
                Log.e("DocenteViewModel", "no se pudieron cargar las observaciones", error)
            }
        }
    }
}