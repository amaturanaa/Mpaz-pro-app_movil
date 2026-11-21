package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.model.Observacion
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositorioObservacionesTest {

    private val api = mockk<ApiMpaz>()
    private val repositorio = RepositorioObservaciones(api)

    // ---------- listar ----------

    @Test
    fun listar_traduceLasObservacionesALaUnidadYDescartaLasDeOtrosObjetivos() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        // Los dos estudiantes de prueba son EST-2B-014 (id 1) y EST-2B-015 (id 2)
        coEvery { api.observaciones("EST-2B-014") } returns listOf(
            ObservacionApi(id = 3L, objetivoId = 10L, texto = "Participa mucho", fecha = "2026-11-01"),
            ObservacionApi(id = 4L, objetivoId = 999L, texto = "De otra unidad", fecha = "2026-11-02")
        )
        coEvery { api.observaciones("EST-2B-015") } returns emptyList()

        val observaciones = repositorio.listar(listOf(unidadDePrueba(id = 1)))

        assertEquals(
            listOf(
                Observacion(
                    id = 3,
                    estudianteId = 1,
                    unidadId = 1,
                    texto = "Participa mucho",
                    fecha = "2026-11-01"
                )
            ),
            observaciones
        )
    }

    // ---------- guardar ----------

    @Test
    fun guardar_enviaLaObservacionConElObjetivoDeLaUnidad() = runTest {
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L))
        val enviada = slot<ObservacionApi>()
        coEvery { api.crearObservacion(capture(enviada)) } returns ObservacionApi(id = 1L)

        repositorio.guardar("EST-2B-014", 1, "Muy buen trabajo")

        assertEquals("EST-2B-014", enviada.captured.estudianteCodigo)
        assertEquals(10L, enviada.captured.objetivoId)
        assertEquals("Muy buen trabajo", enviada.captured.texto)
    }

    @Test
    fun guardar_sinObjetivo_fallaYNoEnviaNada() = runTest {
        coEvery { api.objetivos(1L) } returns emptyList()

        val error = fallaCon<IllegalStateException> {
            repositorio.guardar("EST-2B-014", 1, "Hola")
        }

        assertEquals("La unidad no tiene objetivo en el servidor", error.message)
        coVerify(exactly = 0) { api.crearObservacion(any()) }
    }
}