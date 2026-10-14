package com.example.mpaz_pro_app_movil.datos

import android.content.Context
import androidx.compose.runtime.mutableStateListOf

// =============================================
// ALMACÉN COMPARTIDO DE LA APP
// =============================================
// Aquí viven las listas que usan la UTP y el docente. Son listas de Compose:
// cuando cambian, las pantallas que las leen se redibujan solas.
// Así, si la UTP publica una unidad, el docente la ve sin reiniciar la app.
// Cada cambio se guarda en el teléfono mediante los repositorios.
object AlmacenMpaz {

    val unidades = mutableStateListOf<Unidad>()
    val preguntas = mutableStateListOf<Pregunta>()
    val asignaciones = mutableStateListOf<Asignacion>()
    val observaciones = mutableStateListOf<Observacion>()

    private var iniciado = false

    // Carga todo desde el teléfono. Solo trabaja la primera vez;
    // las siguientes llamadas no hacen nada.
    fun iniciar(context: Context) {
        if (iniciado) return
        val aplicacion = context.applicationContext
        unidades.addAll(RepositorioUnidades.cargar(aplicacion))
        preguntas.addAll(RepositorioPreguntas.cargar(aplicacion))
        asignaciones.addAll(RepositorioAsignaciones.cargar(aplicacion))
        observaciones.addAll(RepositorioObservaciones.cargar(aplicacion))
        iniciado = true
    }

    // Cada función guarda la lista completa y dice si salió bien.
    fun guardarUnidades(context: Context): Boolean =
        RepositorioUnidades.guardar(context.applicationContext, unidades)

    fun guardarPreguntas(context: Context): Boolean =
        RepositorioPreguntas.guardar(context.applicationContext, preguntas)

    fun guardarAsignaciones(context: Context): Boolean =
        RepositorioAsignaciones.guardar(context.applicationContext, asignaciones)

    fun guardarObservaciones(context: Context): Boolean =
        RepositorioObservaciones.guardar(context.applicationContext, observaciones)
}