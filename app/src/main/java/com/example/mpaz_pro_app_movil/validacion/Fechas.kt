package com.example.mpaz_pro_app_movil.validacion

import java.util.Calendar
import java.util.Locale

// =============================================
// FECHAS: lo que escribe la persona <-> lo que se guarda
// =============================================
// La persona escribe y lee "15/12/2026" (día/mes/año).
// Internamente se guarda "2026-12-15" (año-mes-día), que se puede
// comparar como texto: la fecha menor es la que va primero.
object Fechas {

    private val formatoVisible = Regex("^(\\d{2})/(\\d{2})/(\\d{4})$")

    private fun esBisiesto(anio: Int): Boolean =
        (anio % 4 == 0 && anio % 100 != 0) || anio % 400 == 0

    private fun diasDelMes(mes: Int, anio: Int): Int = when (mes) {
        2 -> if (esBisiesto(anio)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }

    // "15/12/2026" -> "2026-12-15". Devuelve null si no es una fecha real
    // (por ejemplo 31/02/2026 o 15-12-2026).
    fun aIso(visible: String): String? {
        val partes = formatoVisible.matchEntire(visible.trim()) ?: return null
        val dia = partes.groupValues[1].toInt()
        val mes = partes.groupValues[2].toInt()
        val anio = partes.groupValues[3].toInt()

        if (anio < 2000 || anio > 2100) return null
        if (mes < 1 || mes > 12) return null
        if (dia < 1 || dia > diasDelMes(mes, anio)) return null

        return String.format(Locale.US, "%04d-%02d-%02d", anio, mes, dia)
    }

    // "2026-12-15" -> "15/12/2026". Si el texto no tiene ese formato,
    // se devuelve tal cual para no romper la pantalla.
    fun aVisible(iso: String): String {
        val partes = iso.split("-")
        if (partes.size != 3) return iso
        return "${partes[2]}/${partes[1]}/${partes[0]}"
    }

    // La fecha de hoy en formato guardado.
    fun hoyIso(): String {
        val ahora = Calendar.getInstance()
        return String.format(
            Locale.US, "%04d-%02d-%02d",
            ahora.get(Calendar.YEAR),
            ahora.get(Calendar.MONTH) + 1,
            ahora.get(Calendar.DAY_OF_MONTH)
        )
    }

    // La fecha de hoy como la ve la persona.
    fun hoyVisible(): String = aVisible(hoyIso())
}