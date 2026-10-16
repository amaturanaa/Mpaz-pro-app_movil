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

    // ---------- Reglas generales ----------

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

    // Revisa que se haya elegido una opción (chips, botones de opción).
    fun opcionElegida(valor: String?, mensaje: String): String? {
        return if (valor.isNullOrBlank()) mensaje else null
    }

    // ---------- Unidades ----------

    // Revisa que se haya elegido una asignatura de la lista.
    fun asignatura(valor: String?): String? =
        opcionElegida(valor, "Elige una asignatura.")

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

    // ---------- Preguntas ----------

    // Las cuatro alternativas deben estar escritas y ser distintas entre sí.
    fun alternativasDistintas(alternativas: List<String>): String? {
        val limpias = alternativas.map { it.trim().lowercase() }
        if (limpias.any { it.isEmpty() }) return null // ya lo avisa cada campo
        return if (limpias.toSet().size != limpias.size) {
            "Las alternativas no pueden repetirse."
        } else null
    }

    // Revisa que se haya marcado cuál alternativa es la correcta.
    fun alternativaCorrecta(posicion: Int?): String? {
        return if (posicion == null) "Marca cuál es la alternativa correcta." else null
    }

    // ---------- Fechas y asignaciones ----------

    // Revisa que sea una fecha real escrita como dd/mm/aaaa.
    fun fecha(valor: String, nombreCampo: String): String? {
        if (valor.isBlank()) return "$nombreCampo es obligatoria."
        return if (Fechas.aIso(valor) == null) {
            "Escribe una fecha real como dd/mm/aaaa, por ejemplo 15/12/2026."
        } else null
    }

    // Revisa el rango con fechas ya guardadas (año-mes-día):
    // el término no puede ser antes del inicio ni antes de hoy.
    fun rangoFechasIso(inicioIso: String, finIso: String): String? {
        return when {
            finIso < inicioIso -> "La fecha de término no puede ser anterior a la de inicio."
            finIso < Fechas.hoyIso() -> "La fecha de término no puede ser anterior a hoy."
            else -> null
        }
    }

    // Lo mismo, pero con lo que escribió la persona (dd/mm/aaaa).
    // Si alguna fecha no es válida devuelve null: ese error ya lo da fecha().
    fun rangoFechas(inicioVisible: String, finVisible: String): String? {
        val inicio = Fechas.aIso(inicioVisible) ?: return null
        val fin = Fechas.aIso(finVisible) ?: return null
        return rangoFechasIso(inicio, fin)
    }

    // Los intentos permitidos van de 1 a 3.
    fun intentos(valor: Int?): String? {
        return when {
            valor == null -> "Elige cuántos intentos se permiten."
            valor < 1 || valor > 3 -> "Los intentos deben ser entre 1 y 3."
            else -> null
        }
    }

    // Lo mismo, pero para el campo donde la persona escribe el número.
    fun intentosEscritos(texto: String): String? {
        val limpio = texto.trim()
        return when {
            limpio.isEmpty() -> "Escribe cuántos intentos se permiten."
            limpio.any { !it.isDigit() } -> "Escribe solo números, sin letras ni signos."
            else -> intentos(limpio.toIntOrNull())
        }
    }
}