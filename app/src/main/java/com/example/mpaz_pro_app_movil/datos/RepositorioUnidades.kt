package com.example.mpaz_pro_app_movil.datos

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

// =============================================
// REPOSITORIO DE UNIDADES (guardado en el teléfono)
// =============================================
// Guarda y recupera la lista de unidades de la UTP.
// Las unidades se escriben como texto JSON en un archivo privado de la app
// (carpeta "files" del teléfono). Sobrevive al cerrar la app del todo.
object RepositorioUnidades {

    private const val NOMBRE_ARCHIVO = "unidades_utp.json"

    // Etiqueta para buscar estos mensajes en Logcat.
    private const val ETIQUETA = "RepoUnidades"

    // El archivo donde viven las unidades.
    private fun archivo(context: Context): File {
        return File(context.applicationContext.filesDir, NOMBRE_ARCHIVO)
    }

    // true si ya se guardó algo alguna vez en el teléfono.
    fun hayGuardado(context: Context): Boolean {
        return archivo(context).exists()
    }

    // Devuelve las unidades guardadas.
    // La primera vez (cuando todavía no se guardó nada) devuelve las
    // unidades de prueba. Si el archivo estuviera dañado,
    // también devuelve las de prueba en vez de cerrar la app.
    fun cargar(context: Context): List<Unidad> {
        val archivo = archivo(context)

        if (!archivo.exists()) {
            Log.d(ETIQUETA, "cargar: no hay archivo, uso las unidades de prueba")
            return DatosPrueba.unidades
        }

        return try {
            val arreglo = JSONArray(archivo.readText())
            val lista = (0 until arreglo.length()).map { posicion ->
                aUnidad(arreglo.getJSONObject(posicion))
            }
            Log.d(ETIQUETA, "cargar: leí ${lista.size} unidades de ${archivo.absolutePath}")
            lista
        } catch (error: Exception) {
            Log.e(ETIQUETA, "cargar: el archivo no se pudo leer", error)
            DatosPrueba.unidades
        }
    }

    // Guarda la lista completa de unidades.
    // Devuelve true si se escribió bien, false si hubo algún problema.
    // La escritura es inmediata (no en segundo plano): cuando esta función
    // termina, los datos ya están en el disco del teléfono.
    fun guardar(context: Context, unidades: List<Unidad>): Boolean {
        return try {
            val arreglo = JSONArray()
            unidades.forEach { unidad -> arreglo.put(aJson(unidad)) }

            val archivo = archivo(context)
            FileOutputStream(archivo).use { salida ->
                salida.write(arreglo.toString().toByteArray(Charsets.UTF_8))
                salida.fd.sync() // obliga a escribir en el disco ahora
            }

            Log.d(ETIQUETA, "guardar: guardé ${unidades.size} unidades en ${archivo.absolutePath}")
            true
        } catch (error: Exception) {
            Log.e(ETIQUETA, "guardar: no se pudo guardar", error)
            false
        }
    }

    // Convierte una Unidad en un objeto JSON (campo por campo).
    // El estado se guarda con su nombre, por ejemplo "BORRADOR".
    private fun aJson(unidad: Unidad): JSONObject {
        return JSONObject().apply {
            put("id", unidad.id)
            put("asignatura", unidad.asignatura)
            put("titulo", unidad.titulo)
            put("codigoOA", unidad.codigoOA)
            put("objetivo", unidad.objetivo)
            put("explicacion", unidad.explicacion)
            put("ejemplo", unidad.ejemplo)
            put("estado", unidad.estado.name)
        }
    }

    // Convierte un objeto JSON de vuelta en una Unidad.
    private fun aUnidad(objeto: JSONObject): Unidad {
        return Unidad(
            id = objeto.getInt("id"),
            asignatura = objeto.getString("asignatura"),
            titulo = objeto.getString("titulo"),
            codigoOA = objeto.getString("codigoOA"),
            objetivo = objeto.getString("objetivo"),
            explicacion = objeto.getString("explicacion"),
            ejemplo = objeto.getString("ejemplo"),
            estado = EstadoUnidad.valueOf(objeto.getString("estado"))
        )
    }
}