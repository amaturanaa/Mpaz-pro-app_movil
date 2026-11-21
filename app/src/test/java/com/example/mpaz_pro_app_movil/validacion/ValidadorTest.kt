package com.example.mpaz_pro_app_movil.validacion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorTest {

    // ---------- normalizarCodigoOA ----------

    @Test
    fun normalizarCodigoOA_pasaAMayusculasYQuitaEspacios() {
        assertEquals("OA-LEN-01", Validador.normalizarCodigoOA("  oa-len-01 "))
    }

    @Test
    fun normalizarCodigoOA_cambiaGuionesRarosPorGuionNormal() {
        // \u2013 es el guion largo que a veces pone el teclado del celular
        assertEquals("OA-LEN-01", Validador.normalizarCodigoOA("OA\u2013LEN\u201301"))
    }

    // ---------- reglas generales ----------

    @Test
    fun obligatorio_conTextoVacioODeEspacios_devuelveMensaje() {
        assertEquals("Título es obligatorio.", Validador.obligatorio("", "Título"))
        assertEquals("Título es obligatorio.", Validador.obligatorio("   ", "Título"))
    }

    @Test
    fun obligatorio_conTexto_devuelveNull() {
        assertNull(Validador.obligatorio("Hola", "Título"))
    }

    @Test
    fun longitud_revisaMinimoMaximoYVacio() {
        assertEquals("Título es obligatorio.", Validador.longitud("  ", "Título", 3, 10))
        assertEquals(
            "Título debe tener al menos 3 caracteres.",
            Validador.longitud("ab", "Título", 3, 10)
        )
        assertEquals(
            "Título no puede superar los 5 caracteres.",
            Validador.longitud("abcdef", "Título", 3, 5)
        )
        assertNull(Validador.longitud("abcd", "Título", 3, 5))
    }

    @Test
    fun longitud_ignoraEspaciosDelInicioYDelFinal() {
        assertEquals(
            "Título debe tener al menos 3 caracteres.",
            Validador.longitud("  ab  ", "Título", 3, 10)
        )
    }

    @Test
    fun opcionElegida_conNullOVacio_devuelveElMensaje() {
        assertEquals("Elige una", Validador.opcionElegida(null, "Elige una"))
        assertEquals("Elige una", Validador.opcionElegida(" ", "Elige una"))
        assertNull(Validador.opcionElegida("Matemáticas", "Elige una"))
    }

    // ---------- unidades ----------

    @Test
    fun asignatura_sinElegir_devuelveMensaje() {
        assertEquals("Elige una asignatura.", Validador.asignatura(null))
        assertNull(Validador.asignatura("Matemáticas"))
    }

    @Test
    fun codigoOA_vacio_esObligatorio() {
        assertEquals("El código OA es obligatorio.", Validador.codigoOA(""))
    }

    @Test
    fun codigoOA_sinPrefijo_avisaQueDebeEmpezarConOA() {
        val mensaje = Validador.codigoOA("LEN-01")
        assertNotNull(mensaje)
        assertTrue(mensaje!!.startsWith("Debe empezar con OA-"))
    }

    @Test
    fun codigoOA_conFormatoIncorrecto_indicaElLargoQueTiene() {
        val mensaje = Validador.codigoOA("OA-LEN-1")
        assertNotNull(mensaje)
        assertTrue(mensaje!!.contains("Formato: OA-XXX-00"))
        assertTrue(mensaje.contains("8 de 9"))
    }

    @Test
    fun codigoOA_correcto_aunqueVengaEnMinusculas_devuelveNull() {
        assertNull(Validador.codigoOA("OA-LEN-01"))
        assertNull(Validador.codigoOA("oa-mat-12"))
    }

    @Test
    fun codigoNoRepetido_detectaRepetidoSinImportarMayusculas() {
        val existentes = listOf("OA-LEN-01", "OA-MAT-01")
        assertEquals(
            "Ya existe una unidad con el código OA-LEN-01.",
            Validador.codigoNoRepetido("oa-len-01", existentes)
        )
        assertNull(Validador.codigoNoRepetido("OA-CIE-01", existentes))
    }

    // ---------- preguntas ----------

    @Test
    fun alternativasDistintas_conRepetidas_devuelveMensaje() {
        assertEquals(
            "Las alternativas no pueden repetirse.",
            Validador.alternativasDistintas(listOf("Uno", "uno ", "Tres", "Cuatro"))
        )
    }

    @Test
    fun alternativasDistintas_conTodasDistintasOConVacias_devuelveNull() {
        assertNull(Validador.alternativasDistintas(listOf("Uno", "Dos", "Tres", "Cuatro")))
        // Si falta escribir alguna, ese error lo avisa cada campo
        assertNull(Validador.alternativasDistintas(listOf("Uno", "", "Tres", "Cuatro")))
    }

    @Test
    fun alternativaCorrecta_sinMarcar_devuelveMensaje() {
        assertEquals(
            "Marca cuál es la alternativa correcta.",
            Validador.alternativaCorrecta(null)
        )
        assertNull(Validador.alternativaCorrecta(0))
    }

    // ---------- fechas ----------

    @Test
    fun fecha_vacia_esObligatoria() {
        assertEquals("Fecha de inicio es obligatoria.", Validador.fecha("", "Fecha de inicio"))
    }

    @Test
    fun fecha_inexistente_pideUnaFechaReal() {
        val mensaje = Validador.fecha("31/02/2026", "Fecha de inicio")
        assertNotNull(mensaje)
        assertTrue(mensaje!!.contains("dd/mm/aaaa"))
    }

    @Test
    fun fecha_valida_devuelveNull() {
        assertNull(Validador.fecha("15/12/2026", "Fecha de inicio"))
    }

    @Test
    fun rangoFechasIso_conTerminoAntesDelInicio_devuelveMensaje() {
        assertEquals(
            "La fecha de término no puede ser anterior a la de inicio.",
            Validador.rangoFechasIso("2099-01-02", "2099-01-01")
        )
    }

    @Test
    fun rangoFechasIso_conTerminoYaPasado_devuelveMensaje() {
        assertEquals(
            "La fecha de término no puede ser anterior a hoy.",
            Validador.rangoFechasIso("2000-01-01", "2000-01-02")
        )
    }

    @Test
    fun rangoFechasIso_conRangoFuturo_devuelveNull() {
        assertNull(Validador.rangoFechasIso("2099-01-01", "2099-12-31"))
    }

    @Test
    fun rangoFechas_conTextoVisible_funcionaIgualQueConIso() {
        assertEquals(
            "La fecha de término no puede ser anterior a la de inicio.",
            Validador.rangoFechas("02/01/2099", "01/01/2099")
        )
        assertNull(Validador.rangoFechas("01/01/2099", "31/12/2099"))
    }

    @Test
    fun rangoFechas_conFechaInvalida_devuelveNull() {
        // Ese error ya lo da fecha()
        assertNull(Validador.rangoFechas("hola", "31/12/2099"))
        assertNull(Validador.rangoFechas("01/01/2099", "31/02/2099"))
    }

    // ---------- intentos ----------

    @Test
    fun intentos_fueraDeRango_devuelveMensaje() {
        assertEquals("Elige cuántos intentos se permiten.", Validador.intentos(null))
        assertEquals("Los intentos deben ser entre 1 y 3.", Validador.intentos(0))
        assertEquals("Los intentos deben ser entre 1 y 3.", Validador.intentos(4))
    }

    @Test
    fun intentos_entreUnoYTres_devuelveNull() {
        assertNull(Validador.intentos(1))
        assertNull(Validador.intentos(2))
        assertNull(Validador.intentos(3))
    }

    @Test
    fun intentosEscritos_revisaVacioLetrasYRango() {
        assertEquals("Escribe cuántos intentos se permiten.", Validador.intentosEscritos("  "))
        assertEquals(
            "Escribe solo números, sin letras ni signos.",
            Validador.intentosEscritos("2a")
        )
        assertEquals("Los intentos deben ser entre 1 y 3.", Validador.intentosEscritos("5"))
        assertNull(Validador.intentosEscritos(" 2 "))
    }
}