package com.example.mpaz_pro_app_movil.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.example.mpaz_pro_app_movil.ui.theme.VerdePrincipal


// CAMPO DE TEXTO CON VALIDACIÓN VISUAL
// Se usa en todos los formularios para que se vean y se comporten igual:
//   - ícono a la izquierda que explica qué se escribe
//   - ícono rojo de advertencia + mensaje cuando hay error
//   - ícono verde cuando el valor es correcto
//   - el mensaje y el ícono aparecen con una animación suave
//
// valor        : texto actual
// alCambiar    : se llama cada vez que la persona escribe
// etiqueta     : nombre del campo
// icono        : ícono de la izquierda
// error        : mensaje de error (null = no mostrar error)
// correcto     : true si el valor ya cumple las reglas
// ayuda        : texto gris de apoyo (se oculta si hay error)
// teclado      : tipo de teclado (texto normal o numérico)
// esFecha      : si es true, solo acepta números y pone las "/" solas
//                (se activa solo en los campos cuya etiqueta empieza con "Fecha")
@Composable
fun CampoValidado(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    correcto: Boolean,
    modifier: Modifier = Modifier,
    ayuda: String? = null,
    unaLinea: Boolean = true,
    lineasMinimas: Int = 1,
    teclado: KeyboardType = KeyboardType.Text,
    esFecha: Boolean = etiqueta.startsWith("Fecha")
) {
    // Si hay error se muestra el error; si no, la ayuda (si existe).
    val textoApoyo = error ?: ayuda

    // El campo guarda el texto Y la posición del cursor. Así, cuando se
    // agrega una "/" sola, el cursor se queda al final y no salta.
    var campo by remember { mutableStateOf(TextFieldValue(valor)) }
    val mostrado = if (campo.text == valor) campo else TextFieldValue(valor, TextRange(valor.length))

    OutlinedTextField(
        value = mostrado,
        onValueChange = { nuevo ->
            if (esFecha) {
                val formateado = formatearFecha(nuevo.text)
                campo = TextFieldValue(formateado, TextRange(formateado.length))
                alCambiar(formateado)
            } else {
                campo = nuevo
                alCambiar(nuevo.text)
            }
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            // Crossfade: un ícono se desvanece mientras aparece el otro.
            // 1 = error, 2 = correcto, 0 = nada.
            Crossfade(
                targetState = when {
                    error != null -> 1
                    correcto -> 2
                    else -> 0
                },
                label = "iconoEstado"
            ) { estado ->
                when (estado) {
                    1 -> Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Campo con error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    2 -> Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Campo correcto",
                        tint = VerdePrincipal
                    )
                    else -> Unit
                }
            }
        },
        isError = error != null,
        singleLine = unaLinea,
        minLines = lineasMinimas,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esFecha) KeyboardType.Number else teclado
        ),
        supportingText = {
            // AnimatedContent: el texto de abajo se funde con el nuevo
            // cuando cambia (de ayuda a error, o al revés).
            AnimatedContent(targetState = textoApoyo, label = "textoApoyo") { texto ->
                if (texto != null) {
                    Text(texto)
                }
            }
        }
    )
}

// Deja solo los números (máximo 8) y pone las "/" en su lugar:
// 23112026 -> 23/11/2026
private fun formatearFecha(texto: String): String {
    val digitos = texto.filter { it.isDigit() }.take(8)
    return buildString {
        digitos.forEachIndexed { indice, digito ->
            if (indice == 2 || indice == 4) append('/')
            append(digito)
        }
    }
}


// MENSAJE DE ERROR ANIMADO
// Para errores que no están dentro de un campo de texto,
// por ejemplo "Elige una asignatura" debajo de unos chips.
// Con null no muestra nada.
@Composable
fun MensajeError(texto: String?) {
    AnimatedContent(targetState = texto, label = "mensajeError") { mensaje ->
        if (mensaje != null) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}