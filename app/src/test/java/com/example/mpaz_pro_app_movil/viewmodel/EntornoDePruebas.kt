package com.example.mpaz_pro_app_movil.viewmodel

import android.app.Application
import android.util.Log
import com.example.mpaz_pro_app_movil.datos.repositorio.AlmacenMpaz
import com.example.mpaz_pro_app_movil.red.AlternativaApi
import com.example.mpaz_pro_app_movil.red.ApiMpaz
import com.example.mpaz_pro_app_movil.red.PreguntaApi
import com.example.mpaz_pro_app_movil.red.PreguntaConAlternativasApi
import com.example.mpaz_pro_app_movil.red.RedMpaz
import com.example.mpaz_pro_app_movil.red.Sesion
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

// =============================================
// ENTORNO COMÚN PARA PROBAR LOS VIEWMODELS
// =============================================
// Los ViewModels usan cosas que en una prueba local no existen:
//   - el hilo principal      -> se reemplaza por uno de pruebas
//   - el servidor            -> se reemplaza por una API falsa (MockK)
//   - la base de datos Room  -> AlmacenMpaz se deja sin guardar de verdad
//   - Log (de Android)       -> se usa para saber cuándo terminó la carga inicial
//
// etiqueta         : el nombre con que el ViewModel escribe en el Log
// mensajeErrorCarga: el texto del Log.e cuando falla la carga inicial
@OptIn(ExperimentalCoroutinesApi::class)
internal class EntornoDePrueba(
    private val etiqueta: String,
    private val mensajeErrorCarga: String
) {
    val api = mockk<ApiMpaz>()
    val aplicacion = mockk<Application>(relaxed = true)

    // Se abre cuando el ViewModel termina de cargar (bien o mal) desde el servidor.
    private val cargaTerminada = CountDownLatch(1)

    fun iniciar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        mockkObject(RedMpaz)
        every { RedMpaz.api } returns api

        mockkObject(AlmacenMpaz)
        every { AlmacenMpaz.iniciar(any()) } just Runs
        every { AlmacenMpaz.guardarUnidades(any()) } returns true
        every { AlmacenMpaz.guardarPreguntas(any()) } returns true
        every { AlmacenMpaz.guardarAsignaciones(any()) } returns true
        AlmacenMpaz.unidades.clear()
        AlmacenMpaz.preguntas.clear()
        AlmacenMpaz.asignaciones.clear()

        Sesion.token = null

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } answers {
            if (firstArg<String>() == etiqueta) cargaTerminada.countDown()
            0
        }
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } answers {
            if (firstArg<String>() == etiqueta && secondArg<String>() == mensajeErrorCarga) {
                cargaTerminada.countDown()
            }
            0
        }
    }

    fun terminar() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    // Espera (hasta 5 segundos) a que el ViewModel termine su carga inicial.
    fun esperarCarga() {
        assertTrue(
            "El ViewModel no terminó de cargar desde el servidor",
            cargaTerminada.await(5, TimeUnit.SECONDS)
        )
    }
}

// Espera (hasta 5 segundos) a que se cumpla algo que se calcula en segundo plano.
internal fun esperarHasta(condicion: () -> Boolean) {
    val limite = System.currentTimeMillis() + 5_000
    while (!condicion()) {
        if (System.currentTimeMillis() > limite) {
            throw AssertionError("No se cumplió la condición esperada a tiempo")
        }
        Thread.sleep(10)
    }
}

// Un error como el que devuelve el servidor (400, 403, 404, 409, 500...).
internal fun errorHttp(codigo: Int) = HttpException(
    Response.error<Any>(codigo, "".toResponseBody("application/json".toMediaType()))
)

// Una pregunta como la entrega el servidor.
internal fun preguntaDelServidor(id: Long) = PreguntaConAlternativasApi(
    pregunta = PreguntaApi(id = id, enunciado = "¿Cuánto es 2 + 2?"),
    alternativas = listOf(
        AlternativaApi(texto = "3", correcta = false),
        AlternativaApi(texto = "4", correcta = true)
    )
)