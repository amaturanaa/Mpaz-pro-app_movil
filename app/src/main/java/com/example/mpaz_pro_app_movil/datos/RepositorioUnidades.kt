package com.example.mpaz_pro_app_movil.datos

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

// =============================================
// REPOSITORIO DE UNIDADES (guardado en el teléfono)
// =============================================
// Guarda y recupera la lista de unidades de la UTP.
// Usa SharedPreferences: un archivo pequeño privado de la app donde
// las unidades se escriben como texto JSON. Sobrevive al cerrar la app.
object RepositorioUnidades {

    // Nombre del archivo y de la clave donde se guarda la lista.
    private const val ARCHIVO = "unidades_utp"
    private const val CLAVE_LISTA = "lista_unidades"

    // Etiqueta para buscar estos mensajes en Logcat.
    private const val ETIQUETA = "RepoUnidades"

    // Devuelve las unidades guardadas.
    // La primera vez (cuando todavía no se guardó nada) devuelve las
    // unidades de prueba. Si el texto guardado estuviera dañado,
    // también devuelve las de prueba en vez de cerrar la app.
    fun cargar(context: Context): List<Unidad> {
        val preferencias = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        val texto = preferencias.getString(CLAVE_LISTA, null)

        if (texto == null) {
            Log.d(ETIQUETA, "cargar: no hay nada guardado, uso las unidades de prueba")
            return DatosPrueba.unidades
        }

        return try {
            val arreglo = JSONArray(texto)
            val lista = (0 until arreglo.length()).map { posicion ->
                aUnidad(arreglo.getJSONObject(posicion))
            }
            Log.d(ETIQUETA, "cargar: leí ${lista.size} unidades guardadas")
            lista
        } catch (error: Exception) {
            Log.e(ETIQUETA, "cargar: el texto guardado no se pudo leer", error)
            DatosPrueba.unidades
        }
    }

    // Guarda la lista completa de unidades.
    // apply() escribe en segundo plano sin congelar la pantalla.
    fun guardar(context: Context, unidades: List<Unidad>) {
        val arreglo = JSONArray()
        unidades.forEach { unidad -> arreglo.put(aJson(unidad)) }

        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
            .edit()
            .putString(CLAVE_LISTA, arreglo.toString())
            .apply()

        Log.d(ETIQUETA, "guardar: guardé ${unidades.size} unidades")
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