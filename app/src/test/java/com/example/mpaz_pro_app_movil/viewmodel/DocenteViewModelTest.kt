package com.example.mpaz_pro_app_movil.viewmodel

import com.example.mpaz_pro_app_movil.datos.ConfigPruebas
import com.example.mpaz_pro_app_movil.datos.repositorio.AlmacenMpaz
import com.example.mpaz_pro_app_movil.red.AsignaturaApi
import com.example.mpaz_pro_app_movil.red.InformeApi
import com.example.mpaz_pro_app_movil.red.ObservacionApi
import com.example.mpaz_pro_app_movil.red.PruebaApi
import com.example.mpaz_pro_app_movil.red.asignacionApi
import com.example.mpaz_pro_app_movil.red.objetivoApi
import com.example.mpaz_pro_app_movil.red.unidadApi
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class DocenteViewModelTest {

    private val entorno = EntornoDePrueba(
        etiqueta = "DocenteViewModel",
        mensajeErrorCarga = "no se pudieron cargar unidades y asignaciones"
    )
    private val api get() = entorno.api

    @Before
    fun preparar() {
        entorno.iniciar()
        // Lo que el docente deja en las preferencias de la prueba del estudiante
        mockkObject(ConfigPruebas)
        every { ConfigPruebas.guardarMaxIntentos(any(), any(), any()) } just Runs
        every { ConfigPruebas.guardarPermitirVerRespuestas(any(), any(), any()) } just Runs
        every { ConfigPruebas.reiniciarResultados(any(), any()) } just Runs
    }

    @After
    fun limpiar() = entorno.terminar()

    // El servidor tiene la unidad 1 publicada (con prueba) y la unidad 2 en borrador.
    private fun servidor(conAsignacion: Boolean = false) {
        coEvery { api.asignaturas() } returns listOf(AsignaturaApi(1L, "Matemática", "2.º Básico"))
        coEvery { api.unidades() } returns listOf(
            unidadApi(id = 1L, titulo = "Unidad 1", estado = "Publicado"),
            unidadApi(id = 2L, titulo = "Unidad 2", estado = "Borrador")
        )
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L, unidadId = 1L))
        coEvery { api.objetivos(2L) } returns
                listOf(objetivoApi(id = 20L, unidadId = 2L, codigo = "OA-MAT-02"))
        coEvery { api.contenidos(any()) } returns emptyList()
        coEvery { api.pruebas(10L) } returns listOf(PruebaApi(id = 100L, objetivoId = 10L))
        coEvery { api.asignaciones(null) } returns
                if (conAsignacion) {
                    listOf(asignacionApi(id = 5L, objetivoId = 10L, curso = "2° Básico B"))
                } else {
                    emptyList()
                }
        coEvery { api.observaciones(any()) } returns emptyList()
    }

    private fun crearViewModel(): DocenteViewModel {
        val viewModel = DocenteViewModel(entorno.aplicacion)
        entorno.esperarCarga()
        return viewModel
    }

    // ---------- carga inicial ----------

    @Test
    fun alAbrir_cargaLasUnidadesYLasAsignacionesDelServidor() {
        servidor(conAsignacion = true)

        val viewModel = crearViewModel()

        assertEquals(2, AlmacenMpaz.unidades.size)
        assertEquals(1, viewModel.asignaciones.size)
        assertEquals(1, viewModel.asignacionesDe(1).size)
        assertTrue(viewModel.asignacionesDe(2).isEmpty())
    }

    @Test
    fun elDocenteSoloVeLasUnidadesPublicadas() {
        servidor()

        val viewModel = crearViewModel()

        assertEquals(listOf(1), viewModel.unidadesPublicadas.map { it.id })
    }

    @Test
    fun unidadPorId_encuentraLaUnidadODevuelveNull() {
        servidor()
        val viewModel = crearViewModel()

        assertEquals(1, viewModel.unidadPorId(1)?.id)
        assertNull(viewModel.unidadPorId(99))
    }

    // ---------- asignar: reglas antes de llamar al servidor ----------

    @Test
    fun asignar_siLaUnidadNoExiste_avisa() {
        servidor()
        val viewModel = crearViewModel()

        assertEquals(
            "La unidad ya no existe.",
            viewModel.asignar(99, "2° Básico B", "01/01/2099", "31/12/2099", 3)
        )
    }

    @Test
    fun asignar_siLaUnidadNoEstaPublicada_noSePermite() {
        servidor()
        val viewModel = crearViewModel()

        assertEquals(
            "Solo se pueden asignar unidades publicadas.",
            viewModel.asignar(2, "2° Básico B", "01/01/2099", "31/12/2099", 3)
        )
    }

    @Test
    fun asignar_alMismoCursoDosVeces_noSePermite() {
        servidor(conAsignacion = true)
        val viewModel = crearViewModel()

        assertEquals(
            "Esta unidad ya está asignada a 2° Básico B.",
            viewModel.asignar(1, "2° Básico B", "01/01/2099", "31/12/2099", 3)
        )
    }

    @Test
    fun asignar_conFechasInvalidas_avisaCualEstaMal() {
        servidor()
        val viewModel = crearViewModel()

        assertEquals(
            "La fecha de inicio no es válida.",
            viewModel.asignar(1, "2° Básico B", "31/02/2099", "31/12/2099", 3)
        )
        assertEquals(
            "La fecha de término no es válida.",
            viewModel.asignar(1, "2° Básico B", "01/01/2099", "hola", 3)
        )
    }

    @Test
    fun asignar_conTerminoAntesDelInicio_noSePermite() {
        servidor()
        val viewModel = crearViewModel()

        assertEquals(
            "La fecha de término no puede ser anterior a la de inicio.",
            viewModel.asignar(1, "2° Básico B", "02/01/2099", "01/01/2099", 3)
        )
    }

    @Test
    fun asignar_conIntentosFueraDeRango_noSePermite() {
        servidor()
        val viewModel = crearViewModel()

        assertEquals(
            "Los intentos deben ser entre 1 y 3.",
            viewModel.asignar(1, "2° Básico B", "01/01/2099", "31/12/2099", 5)
        )
    }

    // ---------- asignar: con el servidor ----------

    @Test
    fun asignar_siElServidorLaAcepta_quedaGuardadaYReiniciaLosResultados() {
        servidor()
        val enviada = slot<com.example.mpaz_pro_app_movil.red.AsignacionApi>()
        coEvery { api.crearAsignacion(capture(enviada)) } returns
                asignacionApi(id = 7L, maxIntentos = 2)
        val viewModel = crearViewModel()

        val error = viewModel.asignar(1, "2° Básico B", "01/01/2099", "31/12/2099", 2)

        assertNull(error)
        assertEquals("2099-01-01", enviada.captured.fechaInicio)
        assertEquals("2099-12-31", enviada.captured.fechaFin)
        assertEquals(1, viewModel.asignacionesDe(1).size)
        assertEquals(7, viewModel.asignacionesDe(1)[0].id)
        verify(exactly = 1) { ConfigPruebas.guardarMaxIntentos(any(), 1, 2) }
        verify(exactly = 1) { ConfigPruebas.reiniciarResultados(any(), 1) }
    }

    @Test
    fun asignar_aUnSegundoCurso_noReiniciaLosResultados() {
        servidor(conAsignacion = true)
        coEvery { api.crearAsignacion(any()) } returns
                asignacionApi(id = 8L, curso = "2° Básico A")
        val viewModel = crearViewModel()

        val error = viewModel.asignar(1, "2° Básico A", "01/01/2099", "31/12/2099", 3)

        assertNull(error)
        assertEquals(2, viewModel.asignacionesDe(1).size)
        verify(exactly = 0) { ConfigPruebas.reiniciarResultados(any(), any()) }
    }

    @Test
    fun asignar_siElServidorFalla_explicaElMotivoYNoGuarda() {
        servidor()
        val viewModel = crearViewModel()

        val casos = listOf(
            errorHttp(409) to "Esta prueba ya fue asignada a 2° Básico B.",
            errorHttp(400) to
                    "El servidor rechazó la asignación. Revisa las fechas o que la unidad tenga preguntas.",
            errorHttp(500) to "No se pudo asignar en el servidor. Revisa la conexión.",
            IOException("sin conexión") to "No se pudo asignar en el servidor. Revisa la conexión."
        )

        for ((falla, mensaje) in casos) {
            coEvery { api.crearAsignacion(any()) } throws falla
            assertEquals(
                mensaje,
                viewModel.asignar(1, "2° Básico B", "01/01/2099", "31/12/2099", 3)
            )
        }
        assertTrue(viewModel.asignaciones.isEmpty())
    }

    @Test
    fun asignar_sinPreguntasEnLaUnidad_noSePuede() {
        servidor()
        coEvery { api.pruebas(10L) } returns emptyList()
        val viewModel = crearViewModel()

        assertEquals(
            "No se pudo asignar en el servidor. Revisa la conexión.",
            viewModel.asignar(1, "2° Básico B", "01/01/2099", "31/12/2099", 3)
        )
        assertTrue(viewModel.asignaciones.isEmpty())
    }

    @Test
    fun asignar_siNoSePuedeGuardarEnElTelefono_avisa() {
        servidor()
        coEvery { api.crearAsignacion(any()) } returns asignacionApi(id = 7L)
        val viewModel = crearViewModel()
        every { AlmacenMpaz.guardarAsignaciones(any()) } returns false

        assertEquals(
            "No se pudo guardar en el teléfono.",
            viewModel.asignar(1, "2° Básico B", "01/01/2099", "31/12/2099", 3)
        )
    }

    // ---------- informes ----------

    @Test
    fun cargarInformes_juntaLosResultadosDeCadaAsignacion() {
        servidor(conAsignacion = true)
        coEvery { api.informe(5L) } returns listOf(
            InformeApi(estudianteCodigo = "EST-2B-014", intentosUsados = 2, mejorNota = 6.0, promedio = 5.5),
            // Un registro incompleto no debe romper el informe
            InformeApi(estudianteCodigo = null, intentosUsados = null, mejorNota = null)
        )
        val viewModel = crearViewModel()

        viewModel.cargarInformes()
        esperarHasta { viewModel.informes.isNotEmpty() }

        val informe = viewModel.informes[0]
        assertEquals(1, informe.unidad.id)
        assertEquals(1, informe.asignaciones.size)
        assertEquals(2, informe.filas.size)

        val fila = informe.filas[0]
        assertEquals("EST-2B-014", fila.estudianteCodigo)
        assertEquals("2° Básico B", fila.curso)
        assertEquals(2, fila.intentosUsados)
        assertEquals(3, fila.maxIntentos)
        assertEquals(6.0, fila.mejorNota!!, 0.0)

        val incompleta = informe.filas[1]
        assertEquals("", incompleta.estudianteCodigo)
        assertEquals(0, incompleta.intentosUsados)
        assertNull(incompleta.mejorNota)
    }

    // ---------- observaciones ----------

    @Test
    fun alAbrir_cargaLasObservacionesDelServidor() {
        servidor(conAsignacion = true)
        coEvery { api.observaciones("EST-2B-014") } returns listOf(
            ObservacionApi(id = 3L, objetivoId = 10L, texto = "Participa mucho", fecha = "2026-11-01")
        )

        val viewModel = crearViewModel()
        esperarHasta { viewModel.observaciones.isNotEmpty() }

        assertEquals(1, viewModel.observaciones.size)
        assertEquals("Participa mucho", viewModel.observaciones[0].texto)
        assertEquals(1, viewModel.observaciones[0].unidadId)
    }

    @Test
    fun agregarObservacion_siElEstudianteNoExiste_avisa() {
        servidor(conAsignacion = true)
        val viewModel = crearViewModel()

        assertEquals("El estudiante no existe.", viewModel.agregarObservacion(99, 1, "Hola"))
    }

    @Test
    fun agregarObservacion_deUnaUnidadSinAsignar_noSePermite() {
        servidor()
        val viewModel = crearViewModel()

        assertEquals(
            "Solo se puede comentar una unidad que ya fue asignada.",
            viewModel.agregarObservacion(1, 1, "Hola")
        )
    }

    @Test
    fun agregarObservacion_siElServidorLaAcepta_laEnviaSinEspaciosSobrantes() {
        servidor(conAsignacion = true)
        val enviada = slot<ObservacionApi>()
        coEvery { api.crearObservacion(capture(enviada)) } returns ObservacionApi(id = 1L)
        val viewModel = crearViewModel()

        val error = viewModel.agregarObservacion(1, 1, "  Muy buen trabajo  ")

        assertNull(error)
        assertEquals("EST-2B-014", enviada.captured.estudianteCodigo)
        assertEquals(10L, enviada.captured.objetivoId)
        assertEquals("Muy buen trabajo", enviada.captured.texto)
    }

    @Test
    fun agregarObservacion_siElServidorFalla_explicaElMotivo() {
        servidor(conAsignacion = true)
        val viewModel = crearViewModel()

        coEvery { api.crearObservacion(any()) } throws errorHttp(400)
        assertEquals(
            "El servidor no aceptó la observación. Revisa que la unidad esté asignada.",
            viewModel.agregarObservacion(1, 1, "Hola")
        )

        coEvery { api.crearObservacion(any()) } throws IOException("sin conexión")
        assertEquals(
            "No se pudo guardar la observación. Revisa la conexión.",
            viewModel.agregarObservacion(1, 1, "Hola")
        )
    }
}