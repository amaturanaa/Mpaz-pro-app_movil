package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.model.Asignacion
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositorioAsignacionesTest {

    private val api = mockk<ApiMpaz>()
    private val repositorio = RepositorioAsignaciones(api)
    private val unidad = unidadDePrueba(id = 1)

    @Before
    fun preparar() {
        // todasLasAsignaciones() carga las unidades con RedMpaz.api
        mockkObject(RedMpaz)
        every { RedMpaz.api } returns api
    }

    @After
    fun limpiar() {
        unmockkAll()
    }

    // ---------- asignar ----------

    @Test
    fun asignar_enviaLaPruebaYLasFechasYDevuelveLaAsignacionDeLaApp() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns listOf(PruebaApi(id = 100L))
        val enviada = slot<AsignacionApi>()
        coEvery { api.crearAsignacion(capture(enviada)) } returns
                asignacionApi(id = 7L, maxIntentos = 2, permiteRevisar = false, avisarCurso = true)

        val resultado = repositorio.asignar(
            unidad = unidad,
            curso = "2° Básico B",
            inicioIso = "2026-12-01",
            finIso = "2026-12-15",
            maxIntentos = 2,
            permiteRevisar = false,
            avisarCurso = true
        )

        assertEquals(100L, enviada.captured.pruebaId)
        assertEquals(10L, enviada.captured.objetivoId)
        assertEquals("2° Básico B", enviada.captured.curso)
        assertEquals("2026-12-01", enviada.captured.fechaInicio)
        assertEquals("2026-12-15", enviada.captured.fechaFin)
        assertEquals(2, enviada.captured.maxIntentos)

        assertEquals(
            Asignacion(
                id = 7,
                unidadId = 1,
                curso = "2° Básico B",
                fechaInicio = "2026-12-01",
                fechaFin = "2026-12-15",
                maxIntentos = 2,
                notificarCurso = true,
                permitirVerRespuestas = false
            ),
            resultado
        )
    }

    @Test
    fun asignar_sinObjetivo_fallaConMensajeClaro() = runTest {
        coEvery { api.objetivos(1L) } returns emptyList()

        val error = fallaCon<IllegalStateException> {
            repositorio.asignar(unidad, "2° Básico B", "2026-12-01", "2026-12-15", 3, true, true)
        }

        assertEquals("La unidad no tiene objetivo en el servidor", error.message)
    }

    @Test
    fun asignar_sinPreguntas_fallaConMensajeClaro() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns emptyList()

        val error = fallaCon<IllegalStateException> {
            repositorio.asignar(unidad, "2° Básico B", "2026-12-01", "2026-12-15", 3, true, true)
        }

        assertEquals("La unidad todavía no tiene preguntas", error.message)
    }

    @Test
    fun asignar_siLaPruebaNoTieneId_falla() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.pruebas(10L) } returns listOf(PruebaApi(id = null))

        val error = fallaCon<IllegalStateException> {
            repositorio.asignar(unidad, "2° Básico B", "2026-12-01", "2026-12-15", 3, true, true)
        }

        assertEquals("El servidor no devolvió el id de la prueba", error.message)
    }

    // ---------- todasLasAsignaciones ----------

    private fun prepararUnidadesDelServidor() {
        coEvery { api.asignaturas() } returns listOf(AsignaturaApi(1L, "Matemática", "2.º Básico"))
        coEvery { api.unidades() } returns listOf(unidadApi(id = 1L, asignaturaId = 1L))
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        coEvery { api.contenidos(10L) } returns emptyList()
    }

    @Test
    fun todasLasAsignaciones_traduceCadaAsignacionALaUnidadDeSuObjetivo() = runTest {
        prepararUnidadesDelServidor()
        coEvery { api.asignaciones(null) } returns listOf(
            asignacionApi(id = 5L, objetivoId = 10L, maxIntentos = 3),
            // Esta apunta a un objetivo que ninguna unidad tiene, así que se descarta
            asignacionApi(id = 6L, objetivoId = 99L)
        )

        val resultado = repositorio.todasLasAsignaciones()

        assertEquals(1, resultado.size)
        assertEquals(5, resultado[0].id)
        assertEquals(1, resultado[0].unidadId)
        assertEquals("2° Básico B", resultado[0].curso)
        assertEquals(3, resultado[0].maxIntentos)
    }

    @Test
    fun todasLasAsignaciones_conCurso_loPideAlServidor() = runTest {
        prepararUnidadesDelServidor()
        coEvery { api.asignaciones("2° Básico B") } returns listOf(asignacionApi(id = 5L))

        val resultado = repositorio.todasLasAsignaciones("2° Básico B")

        assertEquals(1, resultado.size)
    }

    @Test
    fun todasLasAsignaciones_sinAsignaciones_devuelveListaVacia() = runTest {
        prepararUnidadesDelServidor()
        coEvery { api.asignaciones(null) } returns emptyList()

        assertTrue(repositorio.todasLasAsignaciones().isEmpty())
    }
}