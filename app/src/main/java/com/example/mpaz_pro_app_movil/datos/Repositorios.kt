package com.example.mpaz_pro_app_movil.datos

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

// =============================================
// ARCHIVOS LOCALES (guardado en el teléfono)
// =============================================
// Cada tipo de dato vive en su propio archivo de texto JSON dentro de la
// carpeta privada de la app. Este ayudante lee y escribe esos archivos.
// Las unidades usan el mismo sistema (ver RepositorioUnidades).
internal class ArchivoLocal(private val nombre: String) {

    private fun archivo(context: Context): File =
        File(context.applicationContext.filesDir, nombre)

    // Devuelve el contenido, o null si todavía no existe o no se pudo leer.
    fun leer(context: Context): JSONArray? {
        val archivo = archivo(context)
        if (!archivo.exists()) {
            Log.d("Repositorios", "$nombre: no hay archivo, se usan los datos de prueba")
            return null
        }
        return try {
            JSONArray(archivo.readText())
        } catch (error: Exception) {
            Log.e("Repositorios", "$nombre: no se pudo leer", error)
            null
        }
    }

    // Escribe de inmediato y obliga al disco a guardar. true si salió bien.
    fun escribir(context: Context, arreglo: JSONArray): Boolean {
        return try {
            FileOutputStream(archivo(context)).use { salida ->
                salida.write(arreglo.toString().toByteArray(Charsets.UTF_8))
                salida.fd.sync()
            }
            Log.d("Repositorios", "$nombre: guardado")
            true
        } catch (error: Exception) {
            Log.e("Repositorios", "$nombre: no se pudo guardar", error)
            false
        }
    }
}

// =============================================
// PREGUNTAS
// =============================================
object RepositorioPreguntas {

    private val archivo = ArchivoLocal("preguntas_utp.json")

    // La primera vez devuelve las preguntas de prueba.
    fun cargar(context: Context): List<Pregunta> {
        val arreglo = archivo.leer(context) ?: return DatosPrueba.preguntas
        return try {
            (0 until arreglo.length()).map { posicion ->
                val objeto = arreglo.getJSONObject(posicion)
                val alternativas = objeto.getJSONArray("alternativas")
                Pregunta(
                    id = objeto.getInt("id"),
                    unidadId = objeto.getInt("unidadId"),
                    texto = objeto.getString("texto"),
                    alternativas = (0 until alternativas.length()).map { alternativas.getString(it) },
                    correcta = objeto.getInt("correcta"),
                    esPruebaFinal = objeto.getBoolean("esPruebaFinal")
                )
            }
        } catch (error: Exception) {
            Log.e("Repositorios", "preguntas: datos dañados, se usan los de prueba", error)
            DatosPrueba.preguntas
        }
    }

    fun guardar(context: Context, preguntas: List<Pregunta>): Boolean {
        val arreglo = JSONArray()
        preguntas.forEach { pregunta ->
            arreglo.put(JSONObject().apply {
                put("id", pregunta.id)
                put("unidadId", pregunta.unidadId)
                put("texto", pregunta.texto)
                put("alternativas", JSONArray(pregunta.alternativas))
                put("correcta", pregunta.correcta)
                put("esPruebaFinal", pregunta.esPruebaFinal)
            })
        }
        return archivo.escribir(context, arreglo)
    }
}

// =============================================
// ASIGNACIONES (lo que el docente asigna a cada curso)
// =============================================
object RepositorioAsignaciones {

    private val archivo = ArchivoLocal("asignaciones_docente.json")

    fun cargar(context: Context): List<Asignacion> {
        val arreglo = archivo.leer(context) ?: return DatosPrueba.asignaciones
        return try {
            (0 until arreglo.length()).map { posicion ->
                val objeto = arreglo.getJSONObject(posicion)
                Asignacion(
                    id = objeto.getInt("id"),
                    unidadId = objeto.getInt("unidadId"),
                    curso = objeto.getString("curso"),
                    fechaInicio = objeto.getString("fechaInicio"),
                    fechaFin = objeto.getString("fechaFin"),
                    maxIntentos = objeto.getInt("maxIntentos")
                )
            }
        } catch (error: Exception) {
            Log.e("Repositorios", "asignaciones: datos dañados, se usan los de prueba", error)
            DatosPrueba.asignaciones
        }
    }

    fun guardar(context: Context, asignaciones: List<Asignacion>): Boolean {
        val arreglo = JSONArray()
        asignaciones.forEach { asignacion ->
            arreglo.put(JSONObject().apply {
                put("id", asignacion.id)
                put("unidadId", asignacion.unidadId)
                put("curso", asignacion.curso)
                put("fechaInicio", asignacion.fechaInicio)
                put("fechaFin", asignacion.fechaFin)
                put("maxIntentos", asignacion.maxIntentos)
            })
        }
        return archivo.escribir(context, arreglo)
    }
}

// =============================================
// OBSERVACIONES DEL DOCENTE
// =============================================
object RepositorioObservaciones {

    private val archivo = ArchivoLocal("observaciones_docente.json")

    // La primera vez no hay ninguna observación.
    fun cargar(context: Context): List<Observacion> {
        val arreglo = archivo.leer(context) ?: return emptyList()
        return try {
            (0 until arreglo.length()).map { posicion ->
                val objeto = arreglo.getJSONObject(posicion)
                Observacion(
                    id = objeto.getInt("id"),
                    estudianteId = objeto.getInt("estudianteId"),
                    unidadId = objeto.getInt("unidadId"),
                    texto = objeto.getString("texto"),
                    fecha = objeto.getString("fecha")
                )
            }
        } catch (error: Exception) {
            Log.e("Repositorios", "observaciones: datos dañados", error)
            emptyList()
        }
    }

    fun guardar(context: Context, observaciones: List<Observacion>): Boolean {
        val arreglo = JSONArray()
        observaciones.forEach { observacion ->
            arreglo.put(JSONObject().apply {
                put("id", observacion.id)
                put("estudianteId", observacion.estudianteId)
                put("unidadId", observacion.unidadId)
                put("texto", observacion.texto)
                put("fecha", observacion.fecha)
            })
        }
        return archivo.escribir(context, arreglo)
    }
}