package com.example.mpaz_pro_app_movil.datos

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

// =============================================
// ALMACÉN COMPARTIDO DE LA APP
// =============================================
// Aquí viven las listas que usan la UTP y el docente. Son listas de Compose:
// cuando cambian, las pantallas que las leen se redibujan solas.
// Así, si la UTP publica una unidad, el docente la ve sin reiniciar la app.
// Cada cambio se guarda en la base de datos Room mediante RepositorioMpaz.
object AlmacenMpaz {

    private const val ETIQUETA = "AlmacenMpaz"

    val unidades = mutableStateListOf<Unidad>()
    val preguntas = mutableStateListOf<Pregunta>()
    val asignaciones = mutableStateListOf<Asignacion>()
    val observaciones = mutableStateListOf<Observacion>()

    private var iniciado = false

    // Carga todo desde la base de datos. Solo trabaja la primera vez;
    // las siguientes llamadas no hacen nada.
    fun iniciar(context: Context) {
        if (iniciado) return
        val aplicacion = context.applicationContext

        val datos = try {
            runBlocking(Dispatchers.IO) { RepositorioMpaz.cargarTodo(aplicacion) }
        } catch (error: Exception) {
            // Si la base no se pudiera leer, la app abre con los datos de prueba
            // en vez de cerrarse.
            Log.e(ETIQUETA, "iniciar: no se pudo leer la base de datos", error)
            DatosGuardados(
                unidades = DatosPrueba.unidades,
                preguntas = DatosPrueba.preguntas,
                asignaciones = DatosPrueba.asignaciones,
                observaciones = emptyList()
            )
        }

        unidades.addAll(datos.unidades)
        preguntas.addAll(datos.preguntas)
        asignaciones.addAll(datos.asignaciones)
        observaciones.addAll(datos.observaciones)
        iniciado = true
    }

    // Cada función guarda la lista completa y dice si salió bien.
    // Primero se copia la lista (aquí, en el hilo de la pantalla) y luego se
    // escribe en la base. La función no termina hasta que los datos están
    // guardados, así que si se cierra la app justo después no se pierde nada.
    fun guardarUnidades(context: Context): Boolean {
        val copia = unidades.toList()
        return escribir("unidades") {
            RepositorioMpaz.guardarUnidades(context.applicationContext, copia)
        }
    }

    fun guardarPreguntas(context: Context): Boolean {
        val copia = preguntas.toList()
        return escribir("preguntas") {
            RepositorioMpaz.guardarPreguntas(context.applicationContext, copia)
        }
    }

    fun guardarAsignaciones(context: Context): Boolean {
        val copia = asignaciones.toList()
        return escribir("asignaciones") {
            RepositorioMpaz.guardarAsignaciones(context.applicationContext, copia)
        }
    }

    fun guardarObservaciones(context: Context): Boolean {
        val copia = observaciones.toList()
        return escribir("observaciones") {
            RepositorioMpaz.guardarObservaciones(context.applicationContext, copia)
        }
    }

    // Ejecuta la escritura y devuelve true si salió bien, false si falló.
    private fun escribir(nombre: String, escritura: suspend () -> Unit): Boolean {
        return try {
            runBlocking(Dispatchers.IO) { escritura() }
            Log.d(ETIQUETA, "$nombre: guardado")
            true
        } catch (error: Exception) {
            Log.e(ETIQUETA, "$nombre: no se pudo guardar", error)
            false
        }
    }
}