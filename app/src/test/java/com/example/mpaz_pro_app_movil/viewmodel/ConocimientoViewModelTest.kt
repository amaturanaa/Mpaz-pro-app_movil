package com.example.mpaz_pro_app_movil.viewmodel

import com.example.mpaz_pro_app_movil.red.ApiWikipedia
import com.example.mpaz_pro_app_movil.red.RepositorioWikipedia
import com.example.mpaz_pro_app_movil.red.ResumenWiki
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConocimientoViewModelTest {

    private val api = mockk<ApiWikipedia>()

    @Before
    fun preparar() {
        // viewModelScope usa el hilo principal, que no existe en una prueba local
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = ConocimientoViewModel(RepositorioWikipedia(api))

    private fun resumen(titulo: String) = ResumenWiki(
        title = titulo,
        extract = "Resumen de $titulo",
        thumbnail = null
    )

    @Test
    fun alAbrir_cargaElTemaDeLaPrimeraAsignatura() {
        coEvery { api.resumen(any()) } returns resumen("Cuento")

        val viewModel = crearViewModel()
        val estado = viewModel.estado.value

        assertEquals(0, estado.temaElegido)
        assertEquals("Cuento", estado.resumen?.title)
        assertFalse(estado.cargando)
        assertNull(estado.error)
    }

    @Test
    fun tieneUnBotonPorAsignatura() {
        coEvery { api.resumen(any()) } returns resumen("X")

        assertEquals(4, crearViewModel().temas.size)
    }

    @Test
    fun alElegirOtroBoton_cambiaElTemaYElResumen() {
        coEvery { api.resumen(any()) } returns resumen("Luna")
        val viewModel = crearViewModel()

        viewModel.elegir(2)

        assertEquals(2, viewModel.estado.value.temaElegido)
        assertEquals("Luna", viewModel.estado.value.resumen?.title)
    }

    @Test
    fun alElegirUnBotonQueNoExiste_noCambiaNada() {
        coEvery { api.resumen(any()) } returns resumen("Cuento")
        val viewModel = crearViewModel()

        viewModel.elegir(99)

        assertEquals(0, viewModel.estado.value.temaElegido)
    }

    @Test
    fun sinConexion_muestraElMensajeDeError() {
        coEvery { api.resumen(any()) } throws RuntimeException("sin internet")

        val viewModel = crearViewModel()
        val estado = viewModel.estado.value

        assertEquals(
            "No se pudo cargar el resumen. Revisa tu conexión a internet.",
            estado.error
        )
        assertFalse(estado.cargando)
        assertNull(estado.resumen)
    }

    @Test
    fun siFallaDespuesDeUnExito_conservaElResumenAnterior() {
        coEvery { api.resumen(any()) } returns resumen("Cuento")
        val viewModel = crearViewModel()

        coEvery { api.resumen(any()) } throws RuntimeException("sin internet")
        viewModel.elegir(1)

        val estado = viewModel.estado.value
        assertEquals(1, estado.temaElegido)
        assertEquals("Cuento", estado.resumen?.title)
        assertEquals(
            "No se pudo cargar el resumen. Revisa tu conexión a internet.",
            estado.error
        )
    }
}