package com.example.mpaz_pro_app_movil.red

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.TimeZone


// API EXTERNA: WIKIPEDIA (resúmenes cortos)
// No usa el backend propio ni el token de la sesión.

// Foto del artículo (puede venir vacía).
data class MiniaturaWiki(
    val source: String?
)

// Lo que nos devuelve Wikipedia para un tema.
data class ResumenWiki(
    val title: String?,
    val extract: String?,
    val thumbnail: MiniaturaWiki?
)

interface ApiWikipedia {
    @GET("api/rest_v1/page/summary/{titulo}")
    suspend fun resumen(@Path("titulo") titulo: String): ResumenWiki
}

// Un botón de la tarjeta: una asignatura con su lista de temas.
// Cada día se muestra uno distinto de la lista.
data class TemaWiki(
    val asignatura: String,
    val titulos: List<String>
) {
    // Qué tema toca hoy. "dia" es el número de días desde 1970:
    // sube de a 1 cada día, así que el tema avanza de a uno.
    fun indiceDelDia(dia: Long): Int =
        Math.floorMod(dia, titulos.size.toLong()).toInt()
}

object TemasWiki {

    // Temas pensados para enseñanza básica: los primeros de cada lista son
    // más simples y entre medio hay algunos un poco más desafiantes.
    val lista = listOf(
        TemaWiki(
            "Lenguaje",
            listOf(
                "Cuento", "Fábula", "Poema", "Sustantivo", "Adjetivo", "Verbo",
                "Sílaba", "Alfabeto", "Leyenda", "Historieta", "Refrán",
                "Gabriela_Mistral", "Pablo_Neruda", "Mito", "Novela", "Metáfora"
            )
        ),
        TemaWiki(
            "Matemática",
            listOf(
                "Número_natural", "Suma", "Multiplicación", "Cuadrado", "Triángulo",
                "Círculo", "Esfera", "Ábaco", "Simetría", "Perímetro",
                "Geometría", "Número_primo", "Número_entero", "Teorema_de_Pitágoras"
            )
        ),
        TemaWiki(
            "Ciencias",
            listOf(
                "Abeja", "Mariposa", "Pingüino", "Luna", "Agua", "Planta",
                "Arcoíris", "Volcán", "Dinosaurio", "Cóndor_andino", "Corazón",
                "Fotosíntesis", "Sistema_solar", "Ciclo_hidrológico", "Terremoto",
                "Reciclaje", "Átomo"
            )
        ),
        TemaWiki(
            "Historia",
            listOf(
                "Chile", "Santiago_de_Chile", "Cordillera_de_los_Andes", "Mapuche",
                "Isla_de_Pascua", "Moái", "Desierto_de_Atacama", "Bernardo_O'Higgins",
                "Arturo_Prat", "Antiguo_Egipto", "Antigua_Grecia", "Imperio_romano",
                "Imperio_incaico", "Cristóbal_Colón", "Edad_Media", "Revolución_industrial"
            )
        )
    )

    // Número del día de hoy, según la hora del teléfono.
    fun diaActual(): Long {
        val ahora = System.currentTimeMillis()
        val desfase = TimeZone.getDefault().getOffset(ahora)
        return (ahora + desfase) / 86_400_000L
    }
}

object RedWikipedia {
    private const val URL_BASE = "https://es.wikipedia.org/"

    // Wikipedia pide que cada petición lleve un User-Agent.
    private val cliente = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            val peticion = cadena.request().newBuilder()
                .header("User-Agent", "MpazPro/1.0 (proyecto DuocUC)")
                .header("Accept", "application/json")
                .build()
            cadena.proceed(peticion)
        }
        .build()

    val api: ApiWikipedia by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiWikipedia::class.java)
    }
}

// Repositorio: recibe la API por parámetro (así se podrá probar con tests).
class RepositorioWikipedia(private val api: ApiWikipedia) {

    // Pide el tema que toca hoy. Si ese título falla (por ejemplo no existe),
    // prueba con el siguiente de la lista, hasta 3 intentos.
    suspend fun resumenDelDia(
        tema: TemaWiki,
        dia: Long = TemasWiki.diaActual()
    ): Result<ResumenWiki> {
        val inicio = tema.indiceDelDia(dia)
        val intentos = minOf(3, tema.titulos.size)
        var ultimoError: Throwable? = null

        for (i in 0 until intentos) {
            val titulo = tema.titulos[(inicio + i) % tema.titulos.size]
            val resultado = runCatching { api.resumen(titulo) }
            if (resultado.isSuccess) return resultado
            ultimoError = resultado.exceptionOrNull()
        }
        return Result.failure(ultimoError ?: IllegalStateException("Sin temas"))
    }
}