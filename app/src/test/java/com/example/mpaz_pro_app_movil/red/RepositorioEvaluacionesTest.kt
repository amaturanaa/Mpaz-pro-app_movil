package com.example.mpaz_pro_app_movil.red

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioEvaluacionesTest {

    private val api = mockk<ApiMpaz>()
    private val repositorio = RepositorioEvaluaciones(api)

    // ---------- preguntasParaRendir ----------

    @Test
    fun preguntasParaRendir_convierteYDescartaLasIncompletas() = runTest {
        coEvery { api.preguntas(100L) } returns listOf(
            PreguntaConAlternativasApi(
                pregunta = PreguntaApi(id = 1L, enunciado = "¿2 + 2?"),
                alternativas = listOf(
                    AlternativaApi(id = 11L, texto = "3"),
                    AlternativaApi(id = 12L, texto = "4")
                )
            ),
            PreguntaConAlternativasApi(pregunta = null),
            PreguntaConAlternativasApi(pregunta = PreguntaApi(id = null, enunciado = "sin id"))
        )

        val preguntas = repositorio.preguntasParaRendir(100L)

        assertEquals(1, preguntas.size)
        assertEquals(1L, preguntas[0].id)
        assertEquals("¿2 + 2?", preguntas[0].enunciado)
        assertEquals(listOf("3", "4"), preguntas[0].alternativas.map { it.texto })
        assertEquals(listOf(11L, 12L), preguntas[0].alternativas.map { it.id })
    }

    // ---------- rendirIntento ----------

    @Test
    fun rendirIntento_enviaLasRespuestasDelEstudianteDePrueba() = runTest {
        val enviado = slot<IntentoRequestApi>()
        coEvery { api.rendirIntento(7L, capture(enviado)) } returns
                IntentoApi(id = 90L, nota = 5.5)

        val intento = repositorio.rendirIntento(7L, mapOf(1L to 12L, 2L to 22L))

        assertEquals(90L, intento.id)
        assertEquals(5.5, intento.nota!!, 0.0)
        assertEquals("EST-2B-001", enviado.captured.estudianteCodigo)
        assertEquals(
            listOf(RespuestaRequestApi(1L, 12L), RespuestaRequestApi(2L, 22L)),
            enviado.captured.respuestas
        )
    }

    // ---------- revisarIntento y miAvance ----------

    @Test
    fun revisarIntento_pideLaRevisionConElCodigoDelEstudiante() = runTest {
        coEvery { api.revisarIntento(90L, "EST-2B-001") } returns
                IntentoRevisionApi(id = 90L, numero = 1)

        assertEquals(1, repositorio.revisarIntento(90L).numero)
    }

    @Test
    fun miAvance_pideElAvanceDelEstudiante() = runTest {
        coEvery { api.avance("EST-2B-001") } returns listOf(
            AvanceApi(asignacionId = 7L, intentosUsados = 1, mejorNota = 6.0)
        )

        val avance = repositorio.miAvance()

        assertEquals(1, avance.size)
        assertEquals(6.0, avance[0].mejorNota!!, 0.0)
    }

    // ---------- pruebaDeAsignacion ----------

    private fun preparaPreguntas() {
        coEvery { api.preguntas(100L) } returns listOf(
            PreguntaConAlternativasApi(
                pregunta = PreguntaApi(id = 1L, enunciado = "¿2 + 2?"),
                alternativas = listOf(AlternativaApi(id = 11L, texto = "4"))
            )
        )
    }

    @Test
    fun pruebaDeAsignacion_juntaAsignacionAvanceYPreguntas() = runTest {
        coEvery { api.asignaciones("2° Básico B") } returns listOf(
            asignacionApi(id = 7L, pruebaId = 100L, maxIntentos = 3, permiteRevisar = false)
        )
        coEvery { api.avance("EST-2B-001") } returns listOf(
            AvanceApi(asignacionId = 7L, intentosUsados = 2, mejorNota = 5.0)
        )
        preparaPreguntas()

        val prueba = repositorio.pruebaDeAsignacion(7L)

        assertEquals(7L, prueba.asignacionId)
        assertEquals(2, prueba.intentosUsados)
        assertEquals(3, prueba.maxIntentos)
        assertEquals(5.0, prueba.mejorNota!!, 0.0)
        assertEquals(false, prueba.permiteRevisar)
        assertEquals(1, prueba.preguntas.size)
    }

    @Test
    fun pruebaDeAsignacion_sinAvance_empiezaConCeroIntentos() = runTest {
        coEvery { api.asignaciones("2° Básico B") } returns listOf(asignacionApi(id = 7L))
        coEvery { api.avance("EST-2B-001") } returns emptyList()
        preparaPreguntas()

        val prueba = repositorio.pruebaDeAsignacion(7L)

        assertEquals(0, prueba.intentosUsados)
        assertNull(prueba.mejorNota)
        assertTrue(prueba.permiteRevisar)
    }

    @Test
    fun pruebaDeAsignacion_siLaAsignacionYaNoExiste_falla() = runTest {
        coEvery { api.asignaciones("2° Básico B") } returns emptyList()

        val error = fallaCon<IllegalStateException> {
            repositorio.pruebaDeAsignacion(7L)
        }

        assertEquals("La asignación ya no existe", error.message)
    }
}