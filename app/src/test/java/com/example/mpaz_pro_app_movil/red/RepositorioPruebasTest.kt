package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.model.Pregunta
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioPruebasTest {

    private val api = mockk<ApiMpaz>()
    private val repositorio = RepositorioPruebas(api)
    private val unidad = unidadDePrueba(id = 1)

    private fun preguntaNueva() = Pregunta(
        id = 0,
        unidadId = 1,
        texto = "¿Cuánto es 2 + 2?",
        alternativas = listOf("3", "4", "5", "6"),
        correcta = 1,
        esPruebaFinal = false
    )

    // ---------- pruebaDeUnidad ----------

    @Test
    fun pruebaDeUnidad_sinObjetivo_devuelveNull() = runTest {
        coEvery { api.objetivos(1L) } returns emptyList()

        assertNull(repositorio.pruebaDeUnidad(unidad))
    }

    @Test
    fun pruebaDeUnidad_conObjetivoSinPruebas_devuelveNull() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns emptyList()

        assertNull(repositorio.pruebaDeUnidad(unidad))
    }

    @Test
    fun pruebaDeUnidad_devuelveLaPrimeraPruebaDelObjetivo() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns listOf(
            PruebaApi(id = 100L, objetivoId = 10L),
            PruebaApi(id = 101L, objetivoId = 10L)
        )

        assertEquals(100L, repositorio.pruebaDeUnidad(unidad)?.id)
    }

    // ---------- preguntasDeUnidad ----------

    @Test
    fun preguntasDeUnidad_sinPrueba_devuelveListaVacia() = runTest {
        coEvery { api.objetivos(1L) } returns emptyList()

        assertTrue(repositorio.preguntasDeUnidad(unidad).isEmpty())
    }

    @Test
    fun preguntasDeUnidad_convierteLasPreguntasDelServidor() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns listOf(PruebaApi(id = 100L))
        coEvery { api.preguntas(100L) } returns listOf(
            PreguntaConAlternativasApi(
                pregunta = PreguntaApi(id = 5L, enunciado = "¿2 + 2?"),
                alternativas = listOf(
                    AlternativaApi(texto = "3", correcta = false),
                    AlternativaApi(texto = "4", correcta = true)
                )
            ),
            // Estas dos vienen incompletas y se descartan:
            PreguntaConAlternativasApi(pregunta = null, alternativas = emptyList()),
            PreguntaConAlternativasApi(PreguntaApi(id = null, enunciado = "sin id"), emptyList())
        )

        val preguntas = repositorio.preguntasDeUnidad(unidad)

        assertEquals(1, preguntas.size)
        assertEquals(
            Pregunta(
                id = 5,
                unidadId = 1,
                texto = "¿2 + 2?",
                alternativas = listOf("3", "4"),
                correcta = 1,
                esPruebaFinal = true
            ),
            preguntas[0]
        )
    }

    // ---------- agregarPregunta ----------

    @Test
    fun agregarPregunta_conPruebaExistente_laGuardaEnEsaPrueba() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns listOf(PruebaApi(id = 100L))
        val enviada = slot<PreguntaRequestApi>()
        coEvery { api.crearPregunta(capture(enviada)) } returns
                PreguntaConAlternativasApi(pregunta = PreguntaApi(id = 55L))

        val creada = repositorio.agregarPregunta(unidad, preguntaNueva(), orden = 2)

        assertEquals(55, creada.id)
        assertTrue(creada.esPruebaFinal)
        assertEquals(100L, enviada.captured.pruebaId)
        assertEquals("¿Cuánto es 2 + 2?", enviada.captured.enunciado)
        assertEquals(2, enviada.captured.orden)
        assertEquals(
            listOf(false, true, false, false),
            enviada.captured.alternativas.map { it.correcta }
        )
        coVerify(exactly = 0) { api.crearPrueba(any()) }
    }

    @Test
    fun agregarPregunta_sinPrueba_laCreaComoBorradorConTresIntentos() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns emptyList()
        val pruebaEnviada = slot<PruebaApi>()
        coEvery { api.crearPrueba(capture(pruebaEnviada)) } returns PruebaApi(id = 200L)
        val preguntaEnviada = slot<PreguntaRequestApi>()
        coEvery { api.crearPregunta(capture(preguntaEnviada)) } returns
                PreguntaConAlternativasApi(pregunta = PreguntaApi(id = 56L))

        repositorio.agregarPregunta(unidad, preguntaNueva(), orden = 1)

        assertEquals(10L, pruebaEnviada.captured.objetivoId)
        assertEquals("Prueba final: Unidad 1", pruebaEnviada.captured.titulo)
        assertEquals(3, pruebaEnviada.captured.limiteIntentos)
        assertEquals("Borrador", pruebaEnviada.captured.estado)
        assertEquals(200L, preguntaEnviada.captured.pruebaId)
    }

    @Test
    fun agregarPregunta_sinObjetivo_fallaConMensajeClaro() = runTest {
        coEvery { api.objetivos(1L) } returns emptyList()

        val error = fallaCon<IllegalStateException> {
            repositorio.agregarPregunta(unidad, preguntaNueva(), orden = 1)
        }

        assertEquals("La unidad no tiene objetivo en el servidor", error.message)
    }

    @Test
    fun agregarPregunta_siElServidorNoDevuelveIdDeLaPrueba_falla() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns listOf(PruebaApi(id = null))

        val error = fallaCon<IllegalStateException> {
            repositorio.agregarPregunta(unidad, preguntaNueva(), orden = 1)
        }

        assertEquals("El servidor no devolvió el id de la prueba", error.message)
    }

    @Test
    fun agregarPregunta_siElServidorNoDevuelveIdDeLaPregunta_falla() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns listOf(PruebaApi(id = 100L))
        coEvery { api.crearPregunta(any()) } returns
                PreguntaConAlternativasApi(pregunta = PreguntaApi(id = null))

        val error = fallaCon<IllegalStateException> {
            repositorio.agregarPregunta(unidad, preguntaNueva(), orden = 1)
        }

        assertEquals("El servidor no devolvió el id de la pregunta", error.message)
    }

    // ---------- eliminarPregunta ----------

    @Test
    fun eliminarPregunta_pideAlServidorBorrarEsaPregunta() = runTest {
        coEvery { api.eliminarPregunta(5L) } returns Unit

        repositorio.eliminarPregunta(5)

        coVerify(exactly = 1) { api.eliminarPregunta(5L) }
    }
}