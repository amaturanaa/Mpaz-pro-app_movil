package com.example.mpaz_pro_app_movil.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Pregunta
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.red.DatosEstudianteMpaz
import com.example.mpaz_pro_app_movil.red.RedMpaz
import com.example.mpaz_pro_app_movil.red.RepositorioAsignaciones
import com.example.mpaz_pro_app_movil.red.RepositorioContenidos
import com.example.mpaz_pro_app_movil.red.RepositorioPruebas
import com.example.mpaz_pro_app_movil.validacion.Fechas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// UNA UNIDAD QUE EL DOCENTE ASIGNÓ
data class UnidadAsignada(
    val unidad: Unidad,
    val cursos: List<String>,
    val fechaHasta: String
)

// UNA PRUEBA QUE EL ALUMNO PUEDE RENDIR
data class PruebaUnidad(
    val unidad: Unidad,
    val preguntas: List<Pregunta>
)


// VIEWMODEL DEL PERFIL ALUMNO
// Los datos vienen del backend, filtrados por el curso del alumno.
// No se usa AlmacenMpaz: así lo que el alumno ve no depende de lo que haya en el teléfono.
class EstudianteViewModel(application: Application) : AndroidViewModel(application) {

    var unidadesAsignadas: List<UnidadAsignada> by mutableStateOf(emptyList())
        private set

    var pruebasAsignadas: List<PruebaUnidad> by mutableStateOf(emptyList())
        private set

    var cargando: Boolean by mutableStateOf(false)
        private set

    var errorCarga: String? by mutableStateOf(null)
        private set

    val asignaturas: List<String> = listOf(
        "Lenguaje y Comunicación",
        "Matemáticas",
        "Ciencias Naturales",
        "Historia, Geografía y Ciencias Sociales"
    )

    init {
        cargarDesdeBackend()
    }

    // Trae unidades, asignaciones del curso y preguntas de cada prueba.
    // Solo muestra unidades PUBLICADAS que tengan asignación al curso del alumno.
    fun cargarDesdeBackend() {
        viewModelScope.launch {
            cargando = true
            errorCarga = null
            try {
                val (unidades, asignaciones) = withContext(Dispatchers.IO) {
                    val unidades = RepositorioContenidos.cargarUnidades()
                    val asignaciones = RepositorioAsignaciones(RedMpaz.api)
                        .todasLasAsignaciones(DatosEstudianteMpaz.CURSO)
                    unidades to asignaciones
                }

                val asignadas = unidades
                    .filter { it.estado == EstadoUnidad.PUBLICADO }
                    .mapNotNull { unidad ->
                        val deLaUnidad = asignaciones.filter { it.unidadId == unidad.id }
                        if (deLaUnidad.isEmpty()) {
                            null
                        } else {
                            UnidadAsignada(
                                unidad = unidad,
                                cursos = deLaUnidad.map { it.curso }.distinct(),
                                fechaHasta = Fechas.aVisible(deLaUnidad.maxOf { it.fechaFin })
                            )
                        }
                    }

                val pruebas = withContext(Dispatchers.IO) {
                    val repo = RepositorioPruebas(RedMpaz.api)
                    asignadas.mapNotNull { asignada ->
                        val preguntas = repo.preguntasDeUnidad(asignada.unidad)
                        if (preguntas.isEmpty()) null else PruebaUnidad(asignada.unidad, preguntas)
                    }
                }

                unidadesAsignadas = asignadas
                pruebasAsignadas = pruebas
                Log.d("EstudianteViewModel", "asignadas: ${asignadas.size}, pruebas: ${pruebas.size}")
            } catch (error: Exception) {
                Log.e("EstudianteViewModel", "no se pudieron cargar las unidades del curso", error)
                errorCarga = "No se pudieron cargar tus unidades. Revisa la conexión."
            } finally {
                cargando = false
            }
        }
    }
}