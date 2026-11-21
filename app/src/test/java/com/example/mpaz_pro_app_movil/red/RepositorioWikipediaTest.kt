package com.example.mpaz_pro_app_movil.red

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioWikipediaTest {

    private val api = mockk<ApiWikipedia>()
    private val repositorio = RepositorioWikipedia(api)
    private val tema = TemaWiki("Lenguaje", listOf("A", "B", "C"))

    private fun resumen(titulo: String) = ResumenWiki(
        title = titulo,
        extract = "Resumen de $titulo",
        thumbnail = null
    )

    // ---------- TemaWiki y TemasWiki ----------

    @Test
    fun indiceDelDia_avanzaDeAUnoYDaLaVueltaAlFinal() {
        assertEquals(0, tema.indiceDelDia(0))
        assertEquals(1, tema.indiceDelDia(1))
        assertEquals(2, tema.indiceDelDia(2))
        assertEquals(0, tema.indiceDelDia(3))
        assertEquals(1, tema.indiceDelDia(4))
    }

    @Test
    fun indiceDelDia_conDiasNegativos_sigueDentroDeLaLista() {
        assertEquals(2, tema.indiceDelDia(-1))
    }

    @Test
    fun temasWiki_tieneUnTemaPorAsignaturaConVariosTitulos() {
        assertEquals(4, TemasWiki.lista.size)
        assertTrue(TemasWiki.lista.all { it.titulos.size >= 3 })
    }

    @Test
    fun diaActual_esUnNumeroDeDiasDesde1970() {
        // Cualquier fecha actual está muy por encima de 20.000 días desde 1970
        assertTrue(TemasWiki.diaActual() > 20_000L)
    }

    // ---------- resumenDelDia ----------

    @Test
    fun resumenDelDia_pideElTemaQueTocaHoy() = runTest {
        coEvery { api.resumen("B") } returns resumen("B")

        val resultado = repositorio.resumenDelDia(tema, dia = 1)

        assertTrue(resultado.isSuccess)
        assertEquals("B", resultado.getOrNull()?.title)
        coVerify(exactly = 1) { api.resumen("B") }
    }

    @Test
    fun resumenDelDia_siElPrimeroFalla_pruebaConElSiguiente() = runTest {
        coEvery { api.resumen("A") } throws RuntimeException("no existe A")
        coEvery { api.resumen("B") } returns resumen("B")

        val resultado = repositorio.resumenDelDia(tema, dia = 0)

        assertEquals("B", resultado.getOrNull()?.title)
        coVerify(exactly = 1) { api.resumen("A") }
        coVerify(exactly = 1) { api.resumen("B") }
    }

    @Test
    fun resumenDelDia_alFinalDeLaListaVuelveAlPrincipio() = runTest {
        coEvery { api.resumen("C") } throws RuntimeException("no existe C")
        coEvery { api.resumen("A") } returns resumen("A")

        val resultado = repositorio.resumenDelDia(tema, dia = 2)

        assertEquals("A", resultado.getOrNull()?.title)
    }

    @Test
    fun resumenDelDia_siFallanLosTresIntentos_devuelveElUltimoError() = runTest {
        coEvery { api.resumen("A") } throws RuntimeException("falló A")
        coEvery { api.resumen("B") } throws RuntimeException("falló B")
        coEvery { api.resumen("C") } throws RuntimeException("falló C")

        val resultado = repositorio.resumenDelDia(tema, dia = 0)

        assertTrue(resultado.isFailure)
        assertEquals("falló C", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun resumenDelDia_noPruebaMasDeTresTitulos() = runTest {
        val temaLargo = TemaWiki("Ciencias", listOf("A", "B", "C", "D", "E"))
        coEvery { api.resumen(any()) } throws RuntimeException("sin conexión")

        val resultado = repositorio.resumenDelDia(temaLargo, dia = 0)

        assertTrue(resultado.isFailure)
        coVerify(exactly = 3) { api.resumen(any()) }
    }

    @Test
    fun resumenDelDia_conUnSoloTitulo_haceUnSoloIntento() = runTest {
        val temaCorto = TemaWiki("Historia", listOf("Chile"))
        coEvery { api.resumen("Chile") } throws RuntimeException("sin conexión")

        val resultado = repositorio.resumenDelDia(temaCorto, dia = 5)

        assertTrue(resultado.isFailure)
        coVerify(exactly = 1) { api.resumen("Chile") }
    }

    @Test
    fun resumenDelDia_laFotoPuedeVenirVacia() = runTest {
        coEvery { api.resumen("A") } returns resumen("A")

        val resultado = repositorio.resumenDelDia(tema, dia = 0)

        assertNull(resultado.getOrNull()?.thumbnail)
    }
}