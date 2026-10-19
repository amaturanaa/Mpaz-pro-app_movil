package com.example.mpaz_pro_app_movil.datos

import android.content.Context

// PUENTE CON LAS PRUEBAS DEL ESTUDIANTE
// Las pruebas del estudiante (PruebaLenguaje, PruebaMatematicas...) guardan
// sus intentos y sus notas en un archivo de preferencias por asignatura.
// Este objeto es el único lugar que conoce esos nombres:
//   - el docente escribe aquí el máximo de intentos al asignar una unidad
//   - el informe lee de aquí los intentos usados y las notas obtenidas
object ConfigPruebas {

    // Cursos disponibles para asignar.
    val cursos = listOf("2° Básico A", "2° Básico B")

    // Nombre del archivo de preferencias de la prueba de cada asignatura.
    // Debe coincidir exactamente con el que usa cada Prueba*.kt.
    fun archivoDe(asignatura: String): String? = when {
        asignatura.startsWith("Lenguaje") -> "prueba_lenguaje"
        asignatura.startsWith("Matem") -> "prueba_matematicas"
        asignatura.startsWith("Ciencias") -> "prueba_ciencias"
        asignatura.startsWith("Historia") -> "prueba_historia"
        else -> null
    }

    // Deja el máximo de intentos donde la prueba del estudiante lo consulta.
    fun guardarMaxIntentos(context: Context, asignatura: String, maximo: Int) {
        val archivo = archivoDe(asignatura) ?: return
        context.applicationContext
            .getSharedPreferences(archivo, Context.MODE_PRIVATE)
            .edit()
            .putInt("max_intentos", maximo.coerceIn(1, 3))
            .apply()
    }

    // Deja guardado si el estudiante puede revisar sus respuestas
    // después de rendir la prueba.
    fun guardarPermitirVerRespuestas(context: Context, asignatura: String, permitir: Boolean) {
        val archivo = archivoDe(asignatura) ?: return
        context.applicationContext
            .getSharedPreferences(archivo, Context.MODE_PRIVATE)
            .edit()
            .putBoolean("permitir_ver_respuestas", permitir)
            .apply()
    }

    // true si el estudiante puede revisar sus respuestas (por defecto, sí).
    fun verRespuestasPermitido(context: Context, asignatura: String): Boolean {
        val archivo = archivoDe(asignatura) ?: return true
        return context.applicationContext
            .getSharedPreferences(archivo, Context.MODE_PRIVATE)
            .getBoolean("permitir_ver_respuestas", true)
    }

    // Deja la prueba de esa asignatura como nueva: 0 intentos usados y sin
    // notas. No toca el máximo de intentos. Se usa al asignar una unidad
    // por primera vez, para que el conteo parta desde cero.
    fun reiniciarResultados(context: Context, asignatura: String) {
        val archivo = archivoDe(asignatura) ?: return
        context.applicationContext
            .getSharedPreferences(archivo, Context.MODE_PRIVATE)
            .edit()
            .putInt("intentos_usados", 0)
            .putString("historial_notas", "")
            .apply()
    }

    // Intentos que el estudiante ya terminó en esa asignatura.
    fun intentosUsados(context: Context, asignatura: String): Int {
        val archivo = archivoDe(asignatura) ?: return 0
        return context.applicationContext
            .getSharedPreferences(archivo, Context.MODE_PRIVATE)
            .getInt("intentos_usados", 0)
    }

    // Notas obtenidas, de la primera a la última. En el archivo están
    // separadas por punto y coma, por ejemplo "5.5;4.0;6.1".
    fun notas(context: Context, asignatura: String): List<Double> {
        val archivo = archivoDe(asignatura) ?: return emptyList()
        val texto = context.applicationContext
            .getSharedPreferences(archivo, Context.MODE_PRIVATE)
            .getString("historial_notas", "")
            .orEmpty()
        return texto.split(";").mapNotNull { it.trim().toDoubleOrNull() }
    }

    // Máximo de intentos de esa asignatura
    fun maxIntentos(context: Context, asignatura: String): Int {
        val archivo = archivoDe(asignatura) ?: return 3
        return context.applicationContext
            .getSharedPreferences(archivo, Context.MODE_PRIVATE)
            .getInt("max_intentos", 3)
            .coerceIn(1, 3)
    }
}

