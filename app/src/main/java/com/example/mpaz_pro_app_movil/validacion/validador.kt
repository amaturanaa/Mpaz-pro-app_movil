package com.example.mpaz_pro_app_movil.validacion

// =============================================
// VALIDADOR CENTRALIZADO
// =============================================
// Todas las reglas de validación de la app viven aquí.
// Cada función devuelve:
//   - null          -> el valor es válido
//   - un texto      -> el mensaje de error que verá la persona
// Así cualquier formulario usa las mismas reglas y los mismos mensajes.
object Validador {

    // Formato del código de Objetivo de Aprendizaje: OA-LEN-01, OA-MAT-02...
    // OA- + tres letras mayúsculas + guion + dos dígitos.
    private val formatoCodigoOA = Regex("^OA-[A-Z]{3}-\\d{2}$")

    // Los teclados del celular a veces cambian el guion "-" por otros
    // parecidos (– — ‑ −). Esta lista los reconoce a todos.
    private val guionesRaros = Regex("[\\u2010-\\u2015\\u2212\\uFE63\\uFF0D]")

    // Deja el código limpio: mayúsculas, guion normal, y solo letras,
    // números y guiones (sin espacios ni caracteres invisibles).
    fun normalizarCodigoOA(valor: String): String {
        return valor
            .uppercase()
            .replace(guionesRaros, "-")
            .filter { it.isLetterOrDigit() || it == '-' }
    }

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

    // Revisa el formato del código OA, diciendo qué parte está mal.
    fun codigoOA(valor: String): String? {
        val texto = normalizarCodigoOA(valor)
        return when {
            texto.isEmpty() ->
                "El código OA es obligatorio."
            !texto.startsWith("OA-") ->
                "Debe empezar con OA- (letra O, no el número cero)."
            !formatoCodigoOA.matches(texto) ->
                "Formato: OA-XXX-00, por ejemplo OA-LEN-01 (tiene ${texto.length} de 9 caracteres)."
            else -> null
        }
    }

    // Revisa que el código no esté usado por otra unidad.
    fun codigoNoRepetido(valor: String, codigosExistentes: List<String>): String? {
        val texto = normalizarCodigoOA(valor)
        val repetido = codigosExistentes.any { it.equals(texto, ignoreCase = true) }
        return if (repetido) "Ya existe una unidad con el código $texto." else null
    }
}