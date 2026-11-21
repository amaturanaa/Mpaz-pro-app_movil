package com.example.mpaz_pro_app_movil.datos.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EstadoUnidadTest {

    @Test
    fun borrador_soloPuedePasarARevision() {
        assertEquals(
            listOf(EstadoUnidad.EN_REVISION),
            EstadoUnidad.BORRADOR.siguientesPermitidos()
        )
    }

    @Test
    fun enRevision_puedeAutorizarseODevolverseABorrador() {
        assertEquals(
            listOf(EstadoUnidad.AUTORIZADO, EstadoUnidad.BORRADOR),
            EstadoUnidad.EN_REVISION.siguientesPermitidos()
        )
    }

    @Test
    fun autorizado_soloPuedePublicarse() {
        assertEquals(
            listOf(EstadoUnidad.PUBLICADO),
            EstadoUnidad.AUTORIZADO.siguientesPermitidos()
        )
    }

    @Test
    fun publicado_soloPuedeDeshabilitarse() {
        assertEquals(
            listOf(EstadoUnidad.DESHABILITADO),
            EstadoUnidad.PUBLICADO.siguientesPermitidos()
        )
    }

    @Test
    fun deshabilitado_vuelveABorrador() {
        assertEquals(
            listOf(EstadoUnidad.BORRADOR),
            EstadoUnidad.DESHABILITADO.siguientesPermitidos()
        )
    }

    @Test
    fun nadiePuedeSaltarsePasos() {
        assertFalse(EstadoUnidad.PUBLICADO in EstadoUnidad.BORRADOR.siguientesPermitidos())
        assertFalse(EstadoUnidad.PUBLICADO in EstadoUnidad.EN_REVISION.siguientesPermitidos())
        assertTrue(EstadoUnidad.entries.all { it.siguientesPermitidos().isNotEmpty() })
    }
}