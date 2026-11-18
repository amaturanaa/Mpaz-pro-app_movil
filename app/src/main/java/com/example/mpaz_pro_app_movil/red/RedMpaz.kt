package com.example.mpaz_pro_app_movil.red

import android.util.Base64
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query

// ---- Lo que se envía y se recibe en el login ----
data class LoginRequest(val email: String, val password: String)
data class LoginRespuesta(val status: String?, val token: String?)

// ---- Lo que devuelve contenidos-service ----
data class AsignaturaApi(
    val id: Long?,
    val nombre: String?,
    val nivel: String?
)

data class UnidadApi(
    val id: Long?,
    val asignaturaId: Long?,
    val titulo: String?,
    val orden: Int?,
    val estado: String?,
    val responsableUtp: String?,
    val fechaActualizacion: String?
)

data class ObjetivoApi(
    val id: Long?,
    val unidadId: Long?,
    val codigo: String?,
    val descripcion: String?,
    val ejeCurricular: String?,
    val textoReferencia: String?,
    val estado: String?,
    val responsableUtp: String?,
    val fechaActualizacion: String?
)

data class ContenidoApi(
    val id: Long?,
    val objetivoId: Long?,
    val titulo: String?,
    val explicacionEscrita: String?,
    val archivoAudio: String?,
    val imagenes: String?,
    val ejemplosGuiados: String?,
    val instrucciones: String?,
    val orden: Int?,
    val responsableOrigen: String?
)

// Cuerpo del PATCH que cambia el estado: {"estado": "En revisión"}
data class CambioEstado(val estado: String)

// ---- Sesión: guarda el token mientras la app está abierta ----
object Sesion {
    var token: String? = null

    // El rol y el correo vienen dentro del token (UTP, DOCENTE, ESTUDIANTE)
    val rol: String? get() = token?.let { leerCampo(it, "role") }
    val email: String? get() = token?.let { leerCampo(it, "sub") }

    fun cerrar() {
        token = null
    }

    private fun leerCampo(token: String, campo: String): String? {
        return try {
            val contenido = token.split(".")[1]
            val json = String(
                Base64.decode(contenido, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            )
            Regex("\"$campo\"\\s*:\\s*\"([^\"]+)\"").find(json)?.groupValues?.get(1)
        } catch (e: Exception) {
            null
        }
    }
}

// ---- Llamadas al backend ----
interface ApiMpaz {

    @POST("auth/login")
    suspend fun login(@Body datos: LoginRequest): LoginRespuesta

    @GET("asignaturas")
    suspend fun asignaturas(): List<AsignaturaApi>

    // Unidades
    @GET("unidades")
    suspend fun unidades(): List<UnidadApi>

    @POST("unidades")
    suspend fun crearUnidad(@Body unidad: UnidadApi): UnidadApi

    @PUT("unidades/{id}")
    suspend fun actualizarUnidad(@Path("id") id: Long, @Body unidad: UnidadApi): UnidadApi

    @PATCH("unidades/{id}/estado")
    suspend fun cambiarEstadoUnidad(
        @Path("id") id: Long,
        @Body cuerpo: CambioEstado
    ): UnidadApi

    // Objetivos de aprendizaje
    @GET("objetivos")
    suspend fun objetivos(@Query("unidadId") unidadId: Long): List<ObjetivoApi>

    @POST("objetivos")
    suspend fun crearObjetivo(@Body objetivo: ObjetivoApi): ObjetivoApi

    @PUT("objetivos/{id}")
    suspend fun actualizarObjetivo(@Path("id") id: Long, @Body objetivo: ObjetivoApi): ObjetivoApi

    // Contenidos didácticos
    @GET("contenidos")
    suspend fun contenidos(@Query("objetivoId") objetivoId: Long): List<ContenidoApi>

    @POST("contenidos")
    suspend fun crearContenido(@Body contenido: ContenidoApi): ContenidoApi

    @PUT("contenidos/{id}")
    suspend fun actualizarContenido(@Path("id") id: Long, @Body contenido: ContenidoApi): ContenidoApi

    // ---- Evaluaciones (actividades-service y progreso-service) ----
    @GET("pruebas")
    suspend fun pruebas(@Query("objetivoId") objetivoId: Long): List<PruebaApi>

    @POST("pruebas")
    suspend fun crearPrueba(@Body prueba: PruebaApi): PruebaApi

    @GET("preguntas")
    suspend fun preguntas(@Query("pruebaId") pruebaId: Long): List<PreguntaConAlternativasApi>

    @POST("preguntas")
    suspend fun crearPregunta(@Body pregunta: PreguntaRequestApi): PreguntaConAlternativasApi

    @PUT("preguntas/{id}")
    suspend fun actualizarPregunta(
        @Path("id") id: Long,
        @Body pregunta: PreguntaRequestApi
    ): PreguntaConAlternativasApi

    @DELETE("preguntas/{id}")
    suspend fun eliminarPregunta(@Path("id") id: Long)

    @GET("asignaciones")
    suspend fun asignaciones(@Query("curso") curso: String?): List<AsignacionApi>

    @POST("asignaciones")
    suspend fun crearAsignacion(@Body asignacion: AsignacionApi): AsignacionApi

    @POST("asignaciones/{id}/intentos")
    suspend fun rendirIntento(@Path("id") id: Long, @Body cuerpo: IntentoRequestApi): IntentoApi

    @GET("intentos/{id}")
    suspend fun revisarIntento(
        @Path("id") id: Long,
        @Query("estudianteCodigo") codigo: String
    ): IntentoRevisionApi

    @GET("progreso/estudiantes/{codigo}/avance")
    suspend fun avance(@Path("codigo") codigo: String): List<AvanceApi>

    @GET("informes/asignaciones/{id}")
    suspend fun informe(@Path("id") id: Long): List<InformeApi>

    @GET("observaciones")
    suspend fun observaciones(@Query("estudianteCodigo") codigo: String): List<ObservacionApi>

    @POST("observaciones")
    suspend fun crearObservacion(@Body observacion: ObservacionApi): ObservacionApi
}

// ---- Conexión al gateway ----
object RedMpaz {

    // 10.0.2.2 es tu PC visto desde el emulador de Android Studio
    const val URL_BASE = "http://10.0.2.2:9090/"

    private val cliente = OkHttpClient.Builder()
        .addInterceptor { cadena ->
            val pedido = cadena.request()
            // /auth/** no lleva token: uno vencido da 401 aunque la ruta sea pública
            val esAuth = pedido.url.encodedPath.startsWith("/auth/")
            val token = Sesion.token
            val nuevo = if (token != null && !esAuth) {
                pedido.newBuilder().addHeader("Authorization", "Bearer $token").build()
            } else {
                pedido
            }
            cadena.proceed(nuevo)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val api: ApiMpaz by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiMpaz::class.java)
    }
}