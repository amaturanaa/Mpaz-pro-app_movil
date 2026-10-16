package com.example.mpaz_pro_app_movil.datos.repositorio

import android.content.Context
import android.util.Log
import com.example.mpaz_pro_app_movil.datos.BaseDatosMpaz
import com.example.mpaz_pro_app_movil.datos.DatosPrueba
import com.example.mpaz_pro_app_movil.datos.entidad.aEntidad
import com.example.mpaz_pro_app_movil.datos.entidad.aModelo
import com.example.mpaz_pro_app_movil.datos.model.Asignacion
import com.example.mpaz_pro_app_movil.datos.model.Observacion
import com.example.mpaz_pro_app_movil.datos.model.Pregunta
import com.example.mpaz_pro_app_movil.datos.model.Unidad

// Lo que se lee de la base de datos al abrir la app.
class DatosGuardados(
    val unidades: List<Unidad>,
    val preguntas: List<Pregunta>,
    val asignaciones: List<Asignacion>,
    val observaciones: List<Observacion>
)

// =============================================
// REPOSITORIO: el intermediario entre la app y la base de datos
// =============================================
// AlmacenMpaz le pide leer o guardar, y este usa los DAOs de Room.
// Así las pantallas no saben nada de tablas ni consultas.
object RepositorioMpaz {

    private const val ETIQUETA = "RepositorioMpaz"

    // Aquí se recuerda si ya se cargaron los datos de prueba.
    // No basta con mirar si las tablas están vacías: la UTP podría
    // borrar todo a propósito y no queremos que reaparezca.
    private const val ARCHIVO_AJUSTES = "mpaz_bd"
    private const val CLAVE_SEMBRADA = "datos_de_prueba_cargados"

    private fun bd(context: Context) = BaseDatosMpaz.obtener(context)

    // Lee todo. La primera vez deja antes los datos de prueba en la base.
    suspend fun cargarTodo(context: Context): DatosGuardados {
        val bd = bd(context)
        cargarDatosDePruebaSiHaceFalta(context, bd)

        val datos = DatosGuardados(
            unidades = bd.unidadDao().todas().map { it.aModelo() },
            preguntas = bd.preguntaDao().todas().map { it.aModelo() },
            asignaciones = bd.asignacionDao().todas().map { it.aModelo() },
            observaciones = bd.observacionDao().todas().map { it.aModelo() }
        )
        Log.d(
            ETIQUETA,
            "cargar: ${datos.unidades.size} unidades, ${datos.preguntas.size} preguntas, " +
                    "${datos.asignaciones.size} asignaciones, ${datos.observaciones.size} observaciones"
        )
        return datos
    }

    private suspend fun cargarDatosDePruebaSiHaceFalta(context: Context, bd: BaseDatosMpaz) {
        val ajustes = context.applicationContext
            .getSharedPreferences(ARCHIVO_AJUSTES, Context.MODE_PRIVATE)
        if (ajustes.getBoolean(CLAVE_SEMBRADA, false)) return

        Log.d(ETIQUETA, "primera vez: cargo los datos de prueba")
        bd.unidadDao().reemplazar(DatosPrueba.unidades.map { it.aEntidad() })
        bd.preguntaDao().reemplazar(DatosPrueba.preguntas.map { it.aEntidad() })
        bd.asignacionDao().reemplazar(DatosPrueba.asignaciones.map { it.aEntidad() })
        ajustes.edit().putBoolean(CLAVE_SEMBRADA, true).commit()
    }

    // Cada función guarda la lista completa de su tabla (en una transacción).
    suspend fun guardarUnidades(context: Context, unidades: List<Unidad>) =
        bd(context).unidadDao().reemplazar(unidades.map { it.aEntidad() })

    suspend fun guardarPreguntas(context: Context, preguntas: List<Pregunta>) =
        bd(context).preguntaDao().reemplazar(preguntas.map { it.aEntidad() })

    suspend fun guardarAsignaciones(context: Context, asignaciones: List<Asignacion>) =
        bd(context).asignacionDao().reemplazar(asignaciones.map { it.aEntidad() })

    suspend fun guardarObservaciones(context: Context, observaciones: List<Observacion>) =
        bd(context).observacionDao().reemplazar(observaciones.map { it.aEntidad() })
}