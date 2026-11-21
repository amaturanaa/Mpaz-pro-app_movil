package com.example.mpaz_pro_app_movil.viewmodel

import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.red.AsignaturaApi
import com.example.mpaz_pro_app_movil.red.AvanceApi
import com.example.mpaz_pro_app_movil.red.asignacionApi
import com.example.mpaz_pro_app_movil.red.objetivoApi
import com.example.mpaz_pro_app_movil.red.unidadApi
import io.mockk.coEvery
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class EstudianteViewModelTest {

    private val entorno = EntornoDePrueba(
        etiqueta = "EstudianteViewModel",
        mensajeErrorCarga = "no se pudieron cargar las unidades del curso"
    )
    private val api get() = entorno.api

    @Before
    fun preparar() = entorno.iniciar()

    @After
    fun limpiar() = entorno.terminar()

    // Unidad 1: publicada y asignada. Unidad 2: publicada pero sin asignar.
    // Unidad 3: en borrador, aunque tenga una asignación.
    private fun servidor(avances: List<AvanceApi>) {
        coEvery { api.asignaturas() } returns listOf(AsignaturaApi(1L, "Matemática", "2.º Básico"))
        coEvery { api.unidades() } returns listOf(
            unidadApi(id = 1L, titulo = "Unidad 1", estado = "Publicado"),
            unidadApi(id = 2L, titulo = "Unidad 2", estado = "Publicado"),
            unidadApi(id = 3L, titulo = "Unidad 3", estado = "Borrador")
        )
        coEvery { api.objetivos(1L) } returns listOf(objetivoApi(id = 10L, unidadId = 1L))
        coEvery { api.objetivos(2L) } returns listOf(objetivoApi(id = 20L, unidadId = 2L))
        coEvery { api.objetivos(3L) } returns listOf(objetivoApi(id = 30L, unidadId = 3L))
        coEvery { api.contenidos(any()) } returns emptyList()
        coEvery { api.asignaciones("2° Básico B") } returns listOf(
            asignacionApi(id = 5L, objetivoId = 10L, maxIntentos = 3),
            asignacionApi(id = 6L, objetivoId = 30L)
        )
        coEvery { api.avance("EST-2B-001") } returns avances
    }

    private fun crearViewModel(): EstudianteViewModel {
        val viewModel = EstudianteViewModel(entorno.aplicacion)
        entorno.esperarCarga()
        return viewModel
    }

    @Test
    fun tieneUnaOpcionPorAsignatura() {
        servidor(emptyList())

        assertEquals(4, crearViewModel().asignaturas.size)
    }

    @Test
    fun soloMuestraUnidadesPublicadasYAsignadasASuCurso() {
        servidor(emptyList())

        val viewModel = crearViewModel()

        assertEquals(1, viewModel.unidadesAsignadas.size)
        val asignada = viewModel.unidadesAsignadas[0]
        assertEquals(1, asignada.unidad.id)
        assertEquals(EstadoUnidad.PUBLICADO, asignada.unidad.estado)
        assertEquals(listOf("2° Básico B"), asignada.cursos)
        // El servidor guarda 2026-12-15 y la persona lo ve como 15/12/2026
        assertEquals("15/12/2026", asignada.fechaHasta)
    }

    @Test
    fun laPruebaLleva_losIntentosYLasNotasDelAvance() {
        servidor(
            listOf(AvanceApi(asignacionId = 5L, intentosUsados = 2, mejorNota = 6.0, promedio = 5.5))
        )

        val viewModel = crearViewModel()

        // La asignación de la unidad en borrador no aparece
        assertEquals(1, viewModel.pruebasAsignadas.size)
        val prueba = viewModel.pruebasAsignadas[0]
        assertEquals(5, prueba.asignacionId)
        assertEquals(1, prueba.unidad.id)
        assertEquals(3, prueba.maxIntentos)
        assertEquals(2, prueba.intentosUsados)
        assertEquals(6.0, prueba.mejorNota!!, 0.0)
        assertEquals(5.5, prueba.promedio!!, 0.0)
    }

    @Test
    fun siTodaviaNoRindioLaPrueba_empiezaSinIntentosNiNotas() {
        servidor(emptyList())

        val prueba = crearViewModel().pruebasAsignadas[0]

        assertEquals(0, prueba.intentosUsados)
        assertNull(prueba.mejorNota)
        assertNull(prueba.promedio)
    }

    @Test
    fun alTerminarDeCargar_noQuedaCargandoNiConError() {
        servidor(emptyList())

        val viewModel = crearViewModel()
        esperarHasta { !viewModel.cargando }

        assertFalse(viewModel.cargando)
        assertNull(viewModel.errorCarga)
    }

    @Test
    fun sinConexion_muestraElMensajeDeError() {
        coEvery { api.asignaturas() } throws IOException("sin conexión")

        val viewModel = crearViewModel()
        esperarHasta { !viewModel.cargando }

        assertEquals(
            "No se pudieron cargar tus unidades. Revisa la conexión.",
            viewModel.errorCarga
        )
        assertTrue(viewModel.unidadesAsignadas.isEmpty())
        assertTrue(viewModel.pruebasAsignadas.isEmpty())
    }

    @Test
    fun alVolverACargar_seBorraElErrorAnterior() {
        coEvery { api.asignaturas() } throws IOException("sin conexión")
        val viewModel = crearViewModel()
        esperarHasta { !viewModel.cargando }
        assertEquals(
            "No se pudieron cargar tus unidades. Revisa la conexión.",
            viewModel.errorCarga
        )

        servidor(emptyList())
        viewModel.cargarDesdeBackend()
        esperarHasta { viewModel.unidadesAsignadas.isNotEmpty() }

        assertNull(viewModel.errorCarga)
    }
}