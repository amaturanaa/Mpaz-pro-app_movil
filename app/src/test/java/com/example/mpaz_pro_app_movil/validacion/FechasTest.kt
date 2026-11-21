package com.example.mpaz_pro_app_movil.validacion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FechasTest {

    @Test
    fun aIso_convierteDeDiaMesAnioAAnioMesDia() {
        assertEquals("2026-12-15", Fechas.aIso("15/12/2026"))
        assertEquals("2026-01-05", Fechas.aIso("05/01/2026"))
    }

    @Test
    fun aIso_ignoraEspaciosAlrededor() {
        assertEquals("2026-12-15", Fechas.aIso("  15/12/2026 "))
    }

    @Test
    fun aIso_conFormatoIncorrecto_devuelveNull() {
        assertNull(Fechas.aIso("15-12-2026"))
        assertNull(Fechas.aIso("1/1/2026"))
        assertNull(Fechas.aIso("hola"))
        assertNull(Fechas.aIso(""))
    }

    @Test
    fun aIso_conDiaOMesImposible_devuelveNull() {
        assertNull(Fechas.aIso("31/04/2026"))
        assertNull(Fechas.aIso("00/10/2026"))
        assertNull(Fechas.aIso("10/13/2026"))
        assertNull(Fechas.aIso("10/00/2026"))
    }

    @Test
    fun aIso_respetaLosAniosBisiestos() {
        assertEquals("2028-02-29", Fechas.aIso("29/02/2028"))
        assertNull(Fechas.aIso("29/02/2027"))
        // 2100 no es bisiesto (divisible por 100 pero no por 400)
        assertNull(Fechas.aIso("29/02/2100"))
    }

    @Test
    fun aIso_conAnioFueraDeRango_devuelveNull() {
        assertNull(Fechas.aIso("15/12/1999"))
        assertNull(Fechas.aIso("15/12/2101"))
    }

    @Test
    fun aVisible_convierteDeAnioMesDiaADiaMesAnio() {
        assertEquals("15/12/2026", Fechas.aVisible("2026-12-15"))
    }

    @Test
    fun aVisible_siElTextoNoTieneFormato_loDevuelveTalCual() {
        assertEquals("hola", Fechas.aVisible("hola"))
    }

    @Test
    fun hoyIso_tieneFormatoAnioMesDia() {
        assertTrue(Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(Fechas.hoyIso()))
    }

    @Test
    fun hoyVisible_esLaMismaFechaDeHoyIso() {
        assertEquals(Fechas.hoyIso(), Fechas.aIso(Fechas.hoyVisible()))
    }
}