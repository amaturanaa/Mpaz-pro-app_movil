package com.example.mpaz_pro_app_movil.datos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalificacionTest {

    @Test
    fun nota_sinAciertosEsUnoPuntoCero() {
        assertEquals(1.0, Calificacion.nota(0, 10), 0.0)
    }

    @Test
    fun nota_conElSesentaPorCientoEsCuatro() {
        assertEquals(4.0, Calificacion.nota(6, 10), 0.0)
    }

    @Test
    fun nota_conTodoCorrectoEsSiete() {
        assertEquals(7.0, Calificacion.nota(10, 10), 0.0)
    }

    @Test
    fun nota_bajoElSesentaPorCiento_seEscalaEntreUnoYCuatro() {
        assertEquals(3.5, Calificacion.nota(5, 10), 0.0)
    }

    @Test
    fun nota_sobreElSesentaPorCiento_seEscalaEntreCuatroYSiete() {
        assertEquals(5.5, Calificacion.nota(8, 10), 0.0)
    }

    @Test
    fun nota_redondeaAUnDecimal() {
        // 1 de 3 -> 2,666... -> 2,7
        assertEquals(2.7, Calificacion.nota(1, 3), 0.0)
        // 2 de 3 -> 4,5
        assertEquals(4.5, Calificacion.nota(2, 3), 0.0)
    }

    @Test
    fun nota_sinPreguntasEsUnoPuntoCero() {
        assertEquals(1.0, Calificacion.nota(0, 0), 0.0)
        assertEquals(1.0, Calificacion.nota(3, -1), 0.0)
    }

    @Test
    fun formatear_usaComaDecimal() {
        assertEquals("5,5", Calificacion.formatear(5.5))
        assertEquals("7,0", Calificacion.formatear(7.0))
    }

    @Test
    fun promedio_sinNotasEsNull() {
        assertNull(Calificacion.promedio(emptyList()))
    }

    @Test
    fun promedio_calculaYRedondeaAUnDecimal() {
        assertEquals(5.5, Calificacion.promedio(listOf(5.0, 6.0))!!, 0.0)
        assertEquals(6.2, Calificacion.promedio(listOf(5.5, 6.0, 7.0))!!, 0.0)
    }
}