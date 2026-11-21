package com.example.mpaz_pro_app_movil.red

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositorioInformesTest {

    @Test
    fun informeDeAsignacion_devuelveUnRegistroPorEstudiante() = runTest {
        val api = mockk<ApiMpaz>()
        coEvery { api.informe(7L) } returns listOf(
            InformeApi(estudianteCodigo = "EST-2B-014", intentosUsados = 2, mejorNota = 6.0, promedio = 5.5),
            InformeApi(estudianteCodigo = "EST-2B-015", intentosUsados = 1, mejorNota = 4.0, promedio = 4.0)
        )

        val informe = RepositorioInformes(api).informeDeAsignacion(7)

        assertEquals(2, informe.size)
        assertEquals("EST-2B-014", informe[0].estudianteCodigo)
        assertEquals(6.0, informe[0].mejorNota!!, 0.0)
    }
}