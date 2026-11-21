package com.example.mpaz_pro_app_movil.auth

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ClaveSeguraTest {

    @Before
    fun preparar() {
        // En una prueba local no existe el Base64 de Android: se usa el de Java.
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any<ByteArray>(), any()) } answers {
            java.util.Base64.getEncoder().encodeToString(firstArg<ByteArray>())
        }
        every { Base64.decode(any<String>(), any()) } answers {
            java.util.Base64.getDecoder().decode(firstArg<String>())
        }
    }

    @After
    fun limpiar() {
        unmockkAll()
    }

    @Test
    fun cifrar_conLaMismaClaveYSal_dalSiempreElMismoResultado() {
        val sal = ClaveSegura.generarSal()

        assertEquals(ClaveSegura.cifrar("123456", sal), ClaveSegura.cifrar("123456", sal))
    }

    @Test
    fun cifrar_noGuardaLaClaveTalComoSeEscribe() {
        val sal = ClaveSegura.generarSal()

        assertNotEquals("123456", ClaveSegura.cifrar("123456", sal))
    }

    @Test
    fun cifrar_conOtraClaveOOtraSal_daOtroResultado() {
        val sal = ClaveSegura.generarSal()
        val otraSal = ClaveSegura.generarSal()

        assertNotEquals(ClaveSegura.cifrar("123456", sal), ClaveSegura.cifrar("654321", sal))
        assertNotEquals(ClaveSegura.cifrar("123456", sal), ClaveSegura.cifrar("123456", otraSal))
    }

    @Test
    fun generarSal_creaSalesDistintasDeDieciseisBytes() {
        val primera = ClaveSegura.generarSal()
        val segunda = ClaveSegura.generarSal()

        assertNotEquals(primera, segunda)
        assertEquals(16, java.util.Base64.getDecoder().decode(primera).size)
    }

    @Test
    fun coincide_conLaClaveCorrecta_esTrue() {
        val sal = ClaveSegura.generarSal()
        val guardada = ClaveSegura.cifrar("123456", sal)

        assertTrue(ClaveSegura.coincide("123456", sal, guardada))
    }

    @Test
    fun coincide_conUnaClaveIncorrecta_esFalse() {
        val sal = ClaveSegura.generarSal()
        val guardada = ClaveSegura.cifrar("123456", sal)

        assertFalse(ClaveSegura.coincide("000000", sal, guardada))
    }
}