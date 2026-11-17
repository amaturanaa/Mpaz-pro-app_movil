package com.example.mpaz_pro_app_movil.red

import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Unidad


// REPOSITORIO DE CONTENIDOS (backend)
// Habla con contenidos-service. Cada unidad de la app se reparte en
// tres tablas del backend: unidad, objetivo de aprendizaje y contenido didáctico.
object RepositorioContenidos {

    // El backend llama "Matemática" a lo que la app llama "Matemáticas".
    private fun nombreEnApp(nombre: String?): String =
        if (nombre == "Matemática") "Matemáticas" else nombre.orEmpty()

    // El backend manda el estado como texto ("En revisión", "Publicado"...).
    fun estadoDesdeTexto(texto: String?): EstadoUnidad =
        EstadoUnidad.entries.firstOrNull { it.texto == texto } ?: EstadoUnidad.BORRADOR

    private suspend fun idDeAsignatura(nombreApp: String): Long {
        val asignatura = RedMpaz.api.asignaturas()
            .firstOrNull { nombreEnApp(it.nombre) == nombreApp }
        return asignatura?.id
            ?: throw IllegalStateException("La asignatura no existe en el servidor")
    }

    // Pide las unidades con su objetivo y su contenido.
    suspend fun cargarUnidades(): List<Unidad> {
        val api = RedMpaz.api
        val asignaturas = api.asignaturas().associateBy { it.id }

        return api.unidades().map { u ->
            val objetivo = u.id?.let { api.objetivos(it).firstOrNull() }
            val contenido = objetivo?.id?.let { api.contenidos(it).firstOrNull() }

            Unidad(
                id = (u.id ?: 0L).toInt(),
                asignatura = nombreEnApp(asignaturas[u.asignaturaId]?.nombre ?: "Sin asignatura"),
                titulo = u.titulo.orEmpty(),
                codigoOA = objetivo?.codigo.orEmpty(),
                objetivo = objetivo?.descripcion.orEmpty(),
                explicacion = contenido?.explicacionEscrita.orEmpty(),
                ejemplo = contenido?.ejemplosGuiados.orEmpty(),
                estado = estadoDesdeTexto(u.estado)
            )
        }
    }

    // Crea la unidad, su objetivo y su contenido. Devuelve la unidad con el id del servidor.
    suspend fun crearUnidad(unidad: Unidad): Unidad {
        val api = RedMpaz.api
        val responsable = Sesion.email ?: "UTP"
        val asignaturaId = idDeAsignatura(unidad.asignatura)
        val orden = api.unidades().count { it.asignaturaId == asignaturaId } + 1

        val unidadCreada = api.crearUnidad(
            UnidadApi(
                id = null,
                asignaturaId = asignaturaId,
                titulo = unidad.titulo,
                orden = orden,
                estado = EstadoUnidad.BORRADOR.texto,
                responsableUtp = responsable,
                fechaActualizacion = null
            )
        )
        val unidadId = unidadCreada.id ?: throw IllegalStateException("El servidor no devolvió el id")

        val objetivoCreado = api.crearObjetivo(
            ObjetivoApi(
                id = null,
                unidadId = unidadId,
                codigo = unidad.codigoOA,
                descripcion = unidad.objetivo,
                ejeCurricular = null,
                textoReferencia = null,
                estado = EstadoUnidad.BORRADOR.texto,
                responsableUtp = responsable,
                fechaActualizacion = null
            )
        )

        api.crearContenido(
            ContenidoApi(
                id = null,
                objetivoId = objetivoCreado.id,
                titulo = unidad.titulo,
                explicacionEscrita = unidad.explicacion,
                archivoAudio = null,
                imagenes = null,
                ejemplosGuiados = unidad.ejemplo,
                instrucciones = null,
                orden = 1,
                responsableOrigen = responsable
            )
        )

        return unidad.copy(id = unidadId.toInt(), estado = EstadoUnidad.BORRADOR)
    }

    // Actualiza la unidad, su objetivo y su contenido (si no existen, los crea).
    suspend fun editarUnidad(unidad: Unidad) {
        val api = RedMpaz.api
        val responsable = Sesion.email ?: "UTP"
        val id = unidad.id.toLong()

        val actual = api.unidades().firstOrNull { it.id == id }
            ?: throw IllegalStateException("La unidad no existe en el servidor")
        val asignaturaId = idDeAsignatura(unidad.asignatura)

        api.actualizarUnidad(id, actual.copy(asignaturaId = asignaturaId, titulo = unidad.titulo))

        val objetivoActual = api.objetivos(id).firstOrNull()
        val objetivo = if (objetivoActual?.id != null) {
            api.actualizarObjetivo(
                objetivoActual.id,
                objetivoActual.copy(codigo = unidad.codigoOA, descripcion = unidad.objetivo)
            )
        } else {
            api.crearObjetivo(
                ObjetivoApi(
                    id = null,
                    unidadId = id,
                    codigo = unidad.codigoOA,
                    descripcion = unidad.objetivo,
                    ejeCurricular = null,
                    textoReferencia = null,
                    estado = actual.estado ?: EstadoUnidad.BORRADOR.texto,
                    responsableUtp = responsable,
                    fechaActualizacion = null
                )
            )
        }

        val contenidoActual = objetivo.id?.let { api.contenidos(it).firstOrNull() }
        if (contenidoActual?.id != null) {
            api.actualizarContenido(
                contenidoActual.id,
                contenidoActual.copy(
                    titulo = unidad.titulo,
                    explicacionEscrita = unidad.explicacion,
                    ejemplosGuiados = unidad.ejemplo
                )
            )
        } else {
            api.crearContenido(
                ContenidoApi(
                    id = null,
                    objetivoId = objetivo.id,
                    titulo = unidad.titulo,
                    explicacionEscrita = unidad.explicacion,
                    archivoAudio = null,
                    imagenes = null,
                    ejemplosGuiados = unidad.ejemplo,
                    instrucciones = null,
                    orden = 1,
                    responsableOrigen = responsable
                )
            )
        }
    }

    // Pide al backend el cambio de estado (PATCH /unidades/{id}/estado).
    // Si el servidor lo rechaza, lanza un error.
    suspend fun cambiarEstado(unidadId: Int, nuevo: EstadoUnidad) {
        RedMpaz.api.cambiarEstadoUnidad(unidadId.toLong(), CambioEstado(nuevo.texto))
    }
}