package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RepositorioContenidosTest {

    private val api = mockk<ApiMpaz>()

    @Before
    fun preparar() {
        Sesion.token = null
        mockkObject(RedMpaz)
        every { RedMpaz.api } returns api
    }

    @After
    fun limpiar() {
        unmockkAll()
    }

    // ---------- estadoDesdeTexto ----------

    @Test
    fun estadoDesdeTexto_reconoceLosTextosDelServidor() {
        assertEquals(EstadoUnidad.EN_REVISION, RepositorioContenidos.estadoDesdeTexto("En revisión"))
        assertEquals(EstadoUnidad.PUBLICADO, RepositorioContenidos.estadoDesdeTexto("Publicado"))
    }

    @Test
    fun estadoDesdeTexto_siNoLoReconoce_dejaLaUnidadEnBorrador() {
        assertEquals(EstadoUnidad.BORRADOR, RepositorioContenidos.estadoDesdeTexto(null))
        assertEquals(EstadoUnidad.BORRADOR, RepositorioContenidos.estadoDesdeTexto("raro"))
    }

    // ---------- cargarUnidades ----------

    @Test
    fun cargarUnidades_juntaUnidadObjetivoYContenido() = runTest {
        coEvery { api.asignaturas() } returns listOf(
            AsignaturaApi(1L, "Matemática", "2.º Básico"),
            AsignaturaApi(2L, "Lenguaje y Comunicación", "2.º Básico")
        )
        coEvery { api.unidades() } returns listOf(
            unidadApi(id = 1L, asignaturaId = 1L, titulo = "Números", estado = "Publicado"),
            unidadApi(id = 2L, asignaturaId = 2L, titulo = "Cuentos", estado = "En revisión"),
            unidadApi(id = 3L, asignaturaId = 99L, titulo = null, estado = null)
        )
        coEvery { api.objetivos(1L) } returns listOf(
            objetivoApi(id = 10L, codigo = "OA-MAT-01", descripcion = "Contar")
        )
        coEvery { api.contenidos(10L) } returns listOf(
            contenidoApi(id = 1L, explicacion = "Explica", ejemplo = "Ej")
        )
        coEvery { api.objetivos(2L) } returns emptyList()
        coEvery { api.objetivos(3L) } returns emptyList()

        val unidades = RepositorioContenidos.cargarUnidades()

        assertEquals(3, unidades.size)

        // El servidor dice "Matemática" y la app dice "Matemáticas"
        assertEquals("Matemáticas", unidades[0].asignatura)
        assertEquals("Números", unidades[0].titulo)
        assertEquals("OA-MAT-01", unidades[0].codigoOA)
        assertEquals("Contar", unidades[0].objetivo)
        assertEquals("Explica", unidades[0].explicacion)
        assertEquals("Ej", unidades[0].ejemplo)
        assertEquals(EstadoUnidad.PUBLICADO, unidades[0].estado)

        // Sin objetivo ni contenido, los textos quedan vacíos
        assertEquals("Lenguaje y Comunicación", unidades[1].asignatura)
        assertEquals("", unidades[1].codigoOA)
        assertEquals(EstadoUnidad.EN_REVISION, unidades[1].estado)

        // Asignatura que el servidor no conoce, título y estado vacíos
        assertEquals("Sin asignatura", unidades[2].asignatura)
        assertEquals("", unidades[2].titulo)
        assertEquals(EstadoUnidad.BORRADOR, unidades[2].estado)
    }

    // ---------- crearUnidad ----------

    @Test
    fun crearUnidad_creaUnidadObjetivoYContenidoYDevuelveElIdDelServidor() = runTest {
        coEvery { api.asignaturas() } returns listOf(AsignaturaApi(1L, "Matemática", "2.º Básico"))
        // Ya hay una unidad en esa asignatura, así que la nueva queda en el orden 2
        coEvery { api.unidades() } returns listOf(unidadApi(id = 9L, asignaturaId = 1L))
        val unidadEnviada = slot<UnidadApi>()
        coEvery { api.crearUnidad(capture(unidadEnviada)) } returns unidadApi(id = 20L)
        val objetivoEnviado = slot<ObjetivoApi>()
        coEvery { api.crearObjetivo(capture(objetivoEnviado)) } returns objetivoApi(id = 30L)
        val contenidoEnviado = slot<ContenidoApi>()
        coEvery { api.crearContenido(capture(contenidoEnviado)) } returns contenidoApi(id = 40L)

        val creada = RepositorioContenidos.crearUnidad(
            unidadDePrueba(id = 0, asignatura = "Matemáticas", estado = EstadoUnidad.PUBLICADO)
        )

        assertEquals(20, creada.id)
        assertEquals(EstadoUnidad.BORRADOR, creada.estado)

        assertEquals(1L, unidadEnviada.captured.asignaturaId)
        assertEquals(2, unidadEnviada.captured.orden)
        assertEquals("Borrador", unidadEnviada.captured.estado)
        assertEquals("UTP", unidadEnviada.captured.responsableUtp)

        assertEquals(20L, objetivoEnviado.captured.unidadId)
        assertEquals("OA-MAT-01", objetivoEnviado.captured.codigo)

        assertEquals(30L, contenidoEnviado.captured.objetivoId)
        assertEquals("Contamos de 10 en 10", contenidoEnviado.captured.explicacionEscrita)
    }

    @Test
    fun crearUnidad_conAsignaturaQueElServidorNoTiene_falla() = runTest {
        coEvery { api.asignaturas() } returns emptyList()

        val error = fallaCon<IllegalStateException> {
            RepositorioContenidos.crearUnidad(unidadDePrueba(id = 0))
        }

        assertEquals("La asignatura no existe en el servidor", error.message)
    }

    @Test
    fun crearUnidad_siElServidorNoDevuelveId_falla() = runTest {
        coEvery { api.asignaturas() } returns listOf(AsignaturaApi(1L, "Matemática", "2.º Básico"))
        coEvery { api.unidades() } returns emptyList()
        coEvery { api.crearUnidad(any()) } returns unidadApi(id = null)

        val error = fallaCon<IllegalStateException> {
            RepositorioContenidos.crearUnidad(unidadDePrueba(id = 0))
        }

        assertEquals("El servidor no devolvió el id", error.message)
    }

    // ---------- editarUnidad ----------

    @Test
    fun editarUnidad_actualizaUnidadObjetivoYContenido() = runTest {
        coEvery { api.unidades() } returns listOf(unidadApi(id = 5L, asignaturaId = 2L, titulo = "Viejo"))
        coEvery { api.asignaturas() } returns listOf(AsignaturaApi(1L, "Matemática", "2.º Básico"))
        val unidadEnviada = slot<UnidadApi>()
        coEvery { api.actualizarUnidad(5L, capture(unidadEnviada)) } returns unidadApi(id = 5L)
        coEvery { api.objetivos(5L) } returns listOf(
            objetivoApi(id = 50L, unidadId = 5L, codigo = "OA-VIEJO-01")
        )
        val objetivoEnviado = slot<ObjetivoApi>()
        coEvery { api.actualizarObjetivo(50L, capture(objetivoEnviado)) } returns objetivoApi(id = 50L)
        coEvery { api.contenidos(50L) } returns listOf(contenidoApi(id = 60L, objetivoId = 50L))
        val contenidoEnviado = slot<ContenidoApi>()
        coEvery { api.actualizarContenido(60L, capture(contenidoEnviado)) } returns contenidoApi(id = 60L)

        RepositorioContenidos.editarUnidad(
            unidadDePrueba(id = 5, asignatura = "Matemáticas", titulo = "Nuevo título")
        )

        assertEquals("Nuevo título", unidadEnviada.captured.titulo)
        assertEquals(1L, unidadEnviada.captured.asignaturaId)
        assertEquals("OA-MAT-01", objetivoEnviado.captured.codigo)
        assertEquals("Contar hasta 100", objetivoEnviado.captured.descripcion)
        assertEquals("Contamos de 10 en 10", contenidoEnviado.captured.explicacionEscrita)
        assertEquals("10, 20, 30", contenidoEnviado.captured.ejemplosGuiados)
    }

    @Test
    fun editarUnidad_siNoExisteEnElServidor_falla() = runTest {
        coEvery { api.unidades() } returns emptyList()

        val error = fallaCon<IllegalStateException> {
            RepositorioContenidos.editarUnidad(unidadDePrueba(id = 5))
        }

        assertEquals("La unidad no existe en el servidor", error.message)
    }

    @Test
    fun editarUnidad_sinObjetivoNiContenido_losCrea() = runTest {
        coEvery { api.unidades() } returns listOf(unidadApi(id = 5L))
        coEvery { api.asignaturas() } returns listOf(AsignaturaApi(1L, "Matemática", "2.º Básico"))
        coEvery { api.actualizarUnidad(5L, any()) } returns unidadApi(id = 5L)
        coEvery { api.objetivos(5L) } returns emptyList()
        coEvery { api.crearObjetivo(any()) } returns objetivoApi(id = 70L, unidadId = 5L)
        coEvery { api.contenidos(70L) } returns emptyList()
        coEvery { api.crearContenido(any()) } returns contenidoApi(id = 80L, objetivoId = 70L)

        RepositorioContenidos.editarUnidad(unidadDePrueba(id = 5))

        coVerify(exactly = 1) { api.crearObjetivo(any()) }
        coVerify(exactly = 1) { api.crearContenido(any()) }
        coVerify(exactly = 0) { api.actualizarObjetivo(any(), any()) }
        coVerify(exactly = 0) { api.actualizarContenido(any(), any()) }
    }

    // ---------- cambiarEstado ----------

    @Test
    fun cambiarEstado_enviaElTextoDelNuevoEstado() = runTest {
        coEvery {
            api.cambiarEstadoUnidad(5L, CambioEstado("En revisión"))
        } returns unidadApi(id = 5L)

        RepositorioContenidos.cambiarEstado(5, EstadoUnidad.EN_REVISION)

        coVerify(exactly = 1) {
            api.cambiarEstadoUnidad(5L, CambioEstado("En revisión"))
        }
    }
}