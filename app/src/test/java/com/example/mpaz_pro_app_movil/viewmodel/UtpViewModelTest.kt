package com.example.mpaz_pro_app_movil.viewmodel

import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Pregunta
import com.example.mpaz_pro_app_movil.datos.repositorio.AlmacenMpaz
import com.example.mpaz_pro_app_movil.red.AsignaturaApi
import com.example.mpaz_pro_app_movil.red.CambioEstado
import com.example.mpaz_pro_app_movil.red.PreguntaApi
import com.example.mpaz_pro_app_movil.red.PreguntaConAlternativasApi
import com.example.mpaz_pro_app_movil.red.PreguntaRequestApi
import com.example.mpaz_pro_app_movil.red.PruebaApi
import com.example.mpaz_pro_app_movil.red.contenidoApi
import com.example.mpaz_pro_app_movil.red.objetivoApi
import com.example.mpaz_pro_app_movil.red.unidadApi
import com.example.mpaz_pro_app_movil.red.unidadDePrueba
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.slot
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class UtpViewModelTest {

    private val entorno = EntornoDePrueba(
        etiqueta = "UtpViewModel",
        mensajeErrorCarga = "no se pudieron cargar las unidades del backend"
    )
    private val api get() = entorno.api

    @Before
    fun preparar() = entorno.iniciar()

    @After
    fun limpiar() = entorno.terminar()

    // El servidor tiene una unidad (con o sin una pregunta en su prueba).
    private fun servidorConUnaUnidad(estado: String, conPregunta: Boolean = false) {
        coEvery { api.asignaturas() } returns listOf(AsignaturaApi(1L, "Matemática", "2.º Básico"))
        coEvery { api.unidades() } returns listOf(unidadApi(id = 1L, estado = estado))
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.contenidos(10L) } returns emptyList()
        coEvery { api.pruebas(10L) } returns listOf(PruebaApi(id = 100L, objetivoId = 10L))
        coEvery { api.preguntas(100L) } returns
                if (conPregunta) listOf(preguntaDelServidor(5L)) else emptyList()
    }

    private fun crearViewModel(): UtpViewModel {
        val viewModel = UtpViewModel(entorno.aplicacion)
        entorno.esperarCarga()
        return viewModel
    }

    private fun preguntaNueva() = Pregunta(
        id = 0,
        unidadId = 1,
        texto = "¿Cuánto es 2 + 2?",
        alternativas = listOf("3", "4", "5", "6"),
        correcta = 1,
        esPruebaFinal = false
    )

    // ---------- carga inicial ----------

    @Test
    fun alAbrir_cargaUnidadesYPreguntasDelServidor() {
        servidorConUnaUnidad("Borrador", conPregunta = true)

        val viewModel = crearViewModel()

        assertEquals(1, viewModel.unidades.size)
        assertEquals("Matemáticas", viewModel.unidades[0].asignatura)
        assertEquals(1, viewModel.preguntasDe(1).size)
        assertEquals(5, viewModel.preguntasDe(1)[0].id)
        assertTrue(viewModel.preguntasDe(99).isEmpty())
    }

    @Test
    fun sinConexion_seQuedaConLosDatosQueYaTenia() {
        AlmacenMpaz.unidades.add(unidadDePrueba(id = 7))
        coEvery { api.asignaturas() } throws IOException("sin conexión")

        val viewModel = crearViewModel()

        assertEquals(1, viewModel.unidades.size)
        assertEquals(7, viewModel.unidades[0].id)
    }

    // ---------- reglas simples ----------

    @Test
    fun puedeEditarse_soloSiLaUnidadNoEstaPublicada() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        assertTrue(viewModel.puedeEditarse(unidadDePrueba(estado = EstadoUnidad.BORRADOR)))
        assertTrue(viewModel.puedeEditarse(unidadDePrueba(estado = EstadoUnidad.DESHABILITADO)))
        assertFalse(viewModel.puedeEditarse(unidadDePrueba(estado = EstadoUnidad.PUBLICADO)))
    }

    @Test
    fun unidadPorId_encuentraLaUnidadODevuelveNull() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        assertEquals(1, viewModel.unidadPorId(1)?.id)
        assertNull(viewModel.unidadPorId(99))
    }

    @Test
    fun siguientesIds_sonElMayorMasUno() {
        servidorConUnaUnidad("Borrador", conPregunta = true)
        val viewModel = crearViewModel()

        assertEquals(2, viewModel.siguienteIdUnidad())
        assertEquals(6, viewModel.siguienteIdPregunta())
    }

    @Test
    fun siguientesIds_sinPreguntasEmpiezanEnUno() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        assertEquals(1, viewModel.siguienteIdPregunta())
    }

    // ---------- crearUnidad ----------

    private fun servidorAceptaCrearUnidad() {
        coEvery { api.crearUnidad(any()) } returns unidadApi(id = 20L)
        coEvery { api.crearObjetivo(any()) } returns objetivoApi(id = 30L)
        coEvery { api.crearContenido(any()) } returns contenidoApi(id = 40L)
    }

    @Test
    fun crearUnidad_siElServidorLaAcepta_apareceEnLaLista() {
        servidorConUnaUnidad("Borrador")
        servidorAceptaCrearUnidad()
        val viewModel = crearViewModel()

        val error = viewModel.crearUnidad(unidadDePrueba(id = 0, asignatura = "Matemáticas"))

        assertNull(error)
        assertEquals(2, viewModel.unidades.size)
        assertEquals(20, viewModel.unidades[1].id)
        assertEquals(EstadoUnidad.BORRADOR, viewModel.unidades[1].estado)
    }

    @Test
    fun crearUnidad_siElServidorFalla_noApareceYExplicaElMotivo() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        val casos = listOf(
            errorHttp(400) to "El servidor rechazó los datos al intentar crear la unidad.",
            errorHttp(403) to "No tienes permiso para crear la unidad.",
            errorHttp(404) to "La unidad ya no existe en el servidor.",
            errorHttp(409) to "El servidor no permite crear la unidad en este momento.",
            errorHttp(500) to "El servidor respondió con error 500.",
            IOException("sin conexión") to "No hay conexión con el servidor.",
            IllegalStateException("raro") to "No se pudo crear la unidad."
        )

        for ((falla, mensaje) in casos) {
            coEvery { api.crearUnidad(any()) } throws falla
            assertEquals(mensaje, viewModel.crearUnidad(unidadDePrueba(id = 0)))
        }
        assertEquals(1, viewModel.unidades.size)
    }

    @Test
    fun crearUnidad_siNoSePuedeGuardarEnElTelefono_avisa() {
        servidorConUnaUnidad("Borrador")
        servidorAceptaCrearUnidad()
        val viewModel = crearViewModel()
        every { AlmacenMpaz.guardarUnidades(any()) } returns false

        assertEquals(
            "No se pudo guardar en el teléfono.",
            viewModel.crearUnidad(unidadDePrueba(id = 0))
        )
    }

    // ---------- editarUnidad ----------

    private fun servidorAceptaEditarUnidad() {
        coEvery { api.actualizarUnidad(1L, any()) } returns unidadApi(id = 1L)
        coEvery { api.actualizarObjetivo(10L, any()) } returns objetivoApi(id = 10L)
        coEvery { api.crearContenido(any()) } returns contenidoApi(id = 1L)
    }

    @Test
    fun editarUnidad_siLaUnidadNoExiste_avisa() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        assertEquals("La unidad ya no existe.", viewModel.editarUnidad(unidadDePrueba(id = 99)))
    }

    @Test
    fun editarUnidad_siEstaPublicada_noSePermite() {
        servidorConUnaUnidad("Publicado")
        val viewModel = crearViewModel()

        assertEquals(
            "Una unidad publicada no se puede editar. Deshabilítala primero.",
            viewModel.editarUnidad(viewModel.unidadPorId(1)!!.copy(titulo = "Otro"))
        )
    }

    @Test
    fun editarUnidad_siElServidorLaAcepta_cambiaEnLaLista() {
        servidorConUnaUnidad("Borrador")
        servidorAceptaEditarUnidad()
        val viewModel = crearViewModel()

        val error = viewModel.editarUnidad(viewModel.unidadPorId(1)!!.copy(titulo = "Nuevo título"))

        assertNull(error)
        assertEquals("Nuevo título", viewModel.unidadPorId(1)?.titulo)
    }

    @Test
    fun editarUnidad_siElServidorLaRechaza_laListaNoCambia() {
        servidorConUnaUnidad("Borrador")
        coEvery { api.actualizarUnidad(1L, any()) } throws errorHttp(403)
        val viewModel = crearViewModel()
        val tituloOriginal = viewModel.unidadPorId(1)!!.titulo

        val error = viewModel.editarUnidad(viewModel.unidadPorId(1)!!.copy(titulo = "Nuevo título"))

        assertEquals("No tienes permiso para editar la unidad.", error)
        assertEquals(tituloOriginal, viewModel.unidadPorId(1)?.titulo)
    }

    // ---------- agregarPregunta ----------

    @Test
    fun agregarPregunta_siLaUnidadNoExiste_avisa() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        assertEquals(
            "La unidad ya no existe.",
            viewModel.agregarPregunta(preguntaNueva().copy(unidadId = 99))
        )
    }

    @Test
    fun agregarPregunta_siLaUnidadEstaPublicada_noSePermite() {
        servidorConUnaUnidad("Publicado")
        val viewModel = crearViewModel()

        assertEquals(
            "Una unidad publicada no admite cambios en sus preguntas.",
            viewModel.agregarPregunta(preguntaNueva())
        )
    }

    @Test
    fun agregarPregunta_siElServidorLaAcepta_quedaEnLaUnidadConSuOrden() {
        servidorConUnaUnidad("Borrador")
        val enviada = slot<PreguntaRequestApi>()
        coEvery { api.crearPregunta(capture(enviada)) } returns
                PreguntaConAlternativasApi(pregunta = PreguntaApi(id = 55L))
        val viewModel = crearViewModel()

        val error = viewModel.agregarPregunta(preguntaNueva())

        assertNull(error)
        assertEquals(1, enviada.captured.orden)
        assertEquals(1, viewModel.preguntasDe(1).size)
        assertEquals(55, viewModel.preguntasDe(1)[0].id)
    }

    @Test
    fun agregarPregunta_siElServidorLaRechaza_noQueda() {
        servidorConUnaUnidad("Borrador")
        coEvery { api.crearPregunta(any()) } throws errorHttp(400)
        val viewModel = crearViewModel()

        val error = viewModel.agregarPregunta(preguntaNueva())

        assertEquals("El servidor rechazó los datos al intentar agregar la pregunta.", error)
        assertTrue(viewModel.preguntasDe(1).isEmpty())
    }

    // ---------- eliminarPregunta ----------

    @Test
    fun eliminarPregunta_siNoExiste_avisa() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        assertEquals("La pregunta ya no existe.", viewModel.eliminarPregunta(99))
    }

    @Test
    fun eliminarPregunta_siLaUnidadEstaPublicada_noSePermite() {
        servidorConUnaUnidad("Publicado", conPregunta = true)
        val viewModel = crearViewModel()

        assertEquals(
            "Una unidad publicada no admite cambios en sus preguntas.",
            viewModel.eliminarPregunta(5)
        )
        assertEquals(1, viewModel.preguntasDe(1).size)
    }

    @Test
    fun eliminarPregunta_siElServidorLaBorra_desapareceDeLaLista() {
        servidorConUnaUnidad("Borrador", conPregunta = true)
        coEvery { api.eliminarPregunta(5L) } returns Unit
        val viewModel = crearViewModel()

        val error = viewModel.eliminarPregunta(5)

        assertNull(error)
        assertTrue(viewModel.preguntasDe(1).isEmpty())
        coVerify(exactly = 1) { api.eliminarPregunta(5L) }
    }

    @Test
    fun eliminarPregunta_siElServidorFalla_laPreguntaSigueAhi() {
        servidorConUnaUnidad("Borrador", conPregunta = true)
        coEvery { api.eliminarPregunta(5L) } throws errorHttp(404)
        val viewModel = crearViewModel()

        val error = viewModel.eliminarPregunta(5)

        assertEquals("La unidad ya no existe en el servidor.", error)
        assertEquals(1, viewModel.preguntasDe(1).size)
    }

    // ---------- cambiarEstado ----------

    @Test
    fun cambiarEstado_siLaUnidadNoExiste_avisa() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        assertEquals(
            "La unidad ya no existe.",
            viewModel.cambiarEstado(99, EstadoUnidad.EN_REVISION)
        )
    }

    @Test
    fun cambiarEstado_noDejaSaltarseElFlujo() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        assertEquals(
            "No se puede pasar de Borrador a Publicado.",
            viewModel.cambiarEstado(1, EstadoUnidad.PUBLICADO)
        )
        assertEquals(EstadoUnidad.BORRADOR, viewModel.unidadPorId(1)?.estado)
    }

    @Test
    fun cambiarEstado_unaUnidadSinPreguntasNoSaleDeBorrador() {
        servidorConUnaUnidad("Borrador")
        val viewModel = crearViewModel()

        assertEquals(
            "Agrega al menos una pregunta antes de enviar la unidad a revisión.",
            viewModel.cambiarEstado(1, EstadoUnidad.EN_REVISION)
        )
    }

    @Test
    fun cambiarEstado_conPreguntasYServidorConforme_avanzaDeEstado() {
        servidorConUnaUnidad("Borrador", conPregunta = true)
        coEvery {
            api.cambiarEstadoUnidad(1L, CambioEstado("En revisión"))
        } returns unidadApi(id = 1L, estado = "En revisión")
        val viewModel = crearViewModel()

        val error = viewModel.cambiarEstado(1, EstadoUnidad.EN_REVISION)

        assertNull(error)
        assertEquals(EstadoUnidad.EN_REVISION, viewModel.unidadPorId(1)?.estado)
        coVerify(exactly = 1) { api.cambiarEstadoUnidad(1L, CambioEstado("En revisión")) }
    }

    @Test
    fun cambiarEstado_siElServidorLoRechaza_noCambiaEnLaPantalla() {
        servidorConUnaUnidad("Borrador", conPregunta = true)
        coEvery { api.cambiarEstadoUnidad(1L, any()) } throws errorHttp(409)
        val viewModel = crearViewModel()

        val error = viewModel.cambiarEstado(1, EstadoUnidad.EN_REVISION)

        assertEquals("El servidor no permite cambiar el estado en este momento.", error)
        assertEquals(EstadoUnidad.BORRADOR, viewModel.unidadPorId(1)?.estado)
    }

    @Test
    fun cambiarEstado_siNoSePuedeGuardarEnElTelefono_avisa() {
        servidorConUnaUnidad("Publicado")
        coEvery { api.cambiarEstadoUnidad(1L, any()) } returns unidadApi(id = 1L)
        val viewModel = crearViewModel()
        every { AlmacenMpaz.guardarUnidades(any()) } returns false

        assertEquals(
            "No se pudo guardar en el teléfono.",
            viewModel.cambiarEstado(1, EstadoUnidad.DESHABILITADO)
        )
    }
}