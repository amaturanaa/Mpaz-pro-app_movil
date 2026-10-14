package com.example.mpaz_pro_app_movil.datos

import java.util.Locale
import kotlin.math.roundToInt

// =============================================
// CÁLCULO DE NOTA (una sola fórmula para toda la app)
// =============================================
// Escala chilena de 1,0 a 7,0 con 60 % de exigencia:
// con el 60 % de las respuestas correctas la nota es 4,0.
// La usan las pruebas del estudiante y el informe del docente.
object Calificacion {

    // aciertos = respuestas correctas, total = preguntas de la prueba.
    fun nota(aciertos: Int, total: Int): Double {
        if (total <= 0) return 1.0

        val porcentaje = aciertos.toDouble() / total
        val notaSinRedondear = if (porcentaje <= 0.60) {
            1.0 + (porcentaje / 0.60) * 3.0
        } else {
            4.0 + ((porcentaje - 0.60) / 0.40) * 3.0
        }
        // Redondeo a un decimal: 5,46 -> 5,5
        return (notaSinRedondear * 10).roundToInt() / 10.0
    }

    // La nota se muestra con coma decimal: 5,5 en vez de 5.5.
    fun formatear(nota: Double): String =
        String.format(Locale.forLanguageTag("es-CL"), "%.1f", nota)

    // Promedio de varias notas, o null si todavía no hay ninguna.
    fun promedio(notas: List<Double>): Double? {
        if (notas.isEmpty()) return null
        return (notas.average() * 10).roundToInt() / 10.0
    }
}