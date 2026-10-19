package com.example.mpaz_pro_app_movil.datos

import android.content.Context

// PUENTE CON LAS PRUEBAS DEL ESTUDIANTE
// Cada unidad asignada tiene su propia prueba. Los intentos y las notas de
// cada una se guardan en un archivo de preferencias distinto:
// "prueba_unidad_<id de la unidad>".
// Este objeto es el único lugar que conoce esos nombres:
//   - el docente escribe aquí el máximo de intentos al asignar una unidad
//   - el informe y el avance leen de aquí los intentos usados y las notas
object ConfigPruebas {

    // Cursos disponibles para asignar.
    val cursos = listOf("2° Básico A", "2° Básico B")

    // Nombre del archivo de preferencias de la prueba de una unidad.
    fun archivoDeUnidad(unidadId: Int): String = "prueba_unidad_$unidadId"

    private fun preferencias(context: Context, archivo: String) =
        context.applicationContext.getSharedPreferences(archivo, Context.MODE_PRIVATE)

    // Deja el máximo de intentos donde la prueba del estudiante lo consulta.
    fun guardarMaxIntentos(context: Context, unidadId: Int, maximo: Int) {
        preferencias(context, archivoDeUnidad(unidadId))
            .edit()
            .putInt("max_intentos", maximo.coerceIn(1, 3))
            .apply()
    }

    // Deja guardado si el estudiante puede revisar sus respuestas
    // después de rendir la prueba.
    fun guardarPermitirVerRespuestas(context: Context, unidadId: Int, permitir: Boolean) {
        preferencias(context, archivoDeUnidad(unidadId))
            .edit()
            .putBoolean("permitir_ver_respuestas", permitir)
            .apply()
    }

    // true si el estudiante puede revisar sus respuestas (por defecto, sí).
    // Recibe el nombre del archivo porque PruebaGenerica ya lo conoce.
    fun verRespuestasPermitido(context: Context, archivo: String): Boolean =
        preferencias(context, archivo).getBoolean("permitir_ver_respuestas", true)

    // Deja la prueba de esa unidad como nueva: 0 intentos usados y sin
    // notas. No toca el máximo de intentos. Se usa al asignar una unidad
    // por primera vez, para que el conteo parta desde cero.
    fun reiniciarResultados(context: Context, unidadId: Int) {
        preferencias(context, archivoDeUnidad(unidadId))
            .edit()
            .putInt("intentos_usados", 0)
            .putString("historial_notas", "")
            .apply()
    }

    // Intentos que el estudiante ya terminó en la prueba de esa unidad.
    fun intentosUsados(context: Context, unidadId: Int): Int =
        preferencias(context, archivoDeUnidad(unidadId)).getInt("intentos_usados", 0)

    // Notas obtenidas, de la primera a la última. En el archivo están
    // separadas por punto y coma, por ejemplo "5.5;4.0;6.1".
    fun notas(context: Context, unidadId: Int): List<Double> {
        val texto = preferencias(context, archivoDeUnidad(unidadId))
            .getString("historial_notas", "")
            .orEmpty()
        return texto.split(";").mapNotNull { it.trim().toDoubleOrNull() }
    }

    // Máximo de intentos de la prueba de esa unidad.
    fun maxIntentos(context: Context, unidadId: Int): Int =
        preferencias(context, archivoDeUnidad(unidadId))
            .getInt("max_intentos", 3)
            .coerceIn(1, 3)
}