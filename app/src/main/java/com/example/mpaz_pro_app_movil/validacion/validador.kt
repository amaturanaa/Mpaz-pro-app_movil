package com.example.mpaz_pro_app_movil.validacion

// VALIDADOR CENTRALIZADO
// Todas las reglas de validación de la app viven aquí.
// Cada función devuelve:
//   - null          -> el valor es válido
//   - un texto      -> el mensaje de error que verá la persona
// Así cualquier formulario usa las mismas reglas y los mismos mensajes.
object Validador {

    // Formato del código de Objetivo de Aprendizaje: OA-LEN-01, OA-MAT-02...
    // OA- + tres letras mayúsculas + guion + dos dígitos.
    private val formatoCodigoOA = Regex("^OA-[A-Z]{3}-\\d{2}$")

    // Revisa que el campo no esté vacío ni solo con espacios.
    fun obligatorio(valor: String, nombreCampo: String): String? {
        return if (valor.isBlank()) "$nombreCampo es obligatorio." else null
    }

    // Revisa que el largo esté entre un mínimo y un máximo.
    // trim() quita los espacios del inicio y del final antes de contar.
    fun longitud(valor: String, nombreCampo: String, minimo: Int, maximo: Int): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "$nombreCampo es obligatorio."
            texto.length < minimo -> "$nombreCampo debe tener al menos $minimo caracteres."
            texto.length > maximo -> "$nombreCampo no puede superar los $maximo caracteres."
            else -> null
        }
    }

    // Revisa que se haya elegido una asignatura de la lista.
    fun asignatura(valor: String?): String? {
        return if (valor.isNullOrBlank()) "Elige una asignatura." else null
    }

    // Revisa el formato del código OA.
    fun codigoOA(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "El código OA es obligatorio."
            !formatoCodigoOA.matches(texto) -> "Usa el formato OA-XXX-00, por ejemplo OA-LEN-01."
            else -> null
        }
    }

    // Revisa que el código no esté usado por otra unidad.
    fun codigoNoRepetido(valor: String, codigosExistentes: List<String>): String? {
        val texto = valor.trim()
        val repetido = codigosExistentes.any { it.equals(texto, ignoreCase = true) }
        return if (repetido) "Ya existe una unidad con el código $texto." else null
    }
}