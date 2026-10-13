package com.example.mpaz_pro_app_movil.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mpaz_pro_app_movil.ui.theme.VerdePrincipal

// CAMPO DE TEXTO CON VALIDACIÓN VISUAL
// Se usa en todos los formularios para que se vean y se comporten igual:
//   - ícono a la izquierda que explica qué se escribe
//   - ícono rojo de advertencia + mensaje cuando hay error
//   - ícono verde cuando el valor es correcto
//
// valor        : texto actual
// alCambiar    : se llama cada vez que la persona escribe
// etiqueta     : nombre del campo
// icono        : ícono de la izquierda
// error        : mensaje de error (null = no mostrar error)
// correcto     : true si el valor ya cumple las reglas
// ayuda        : texto gris de apoyo (se oculta si hay error)
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
    lineasMinimas: Int = 1
) {
    // Si hay error se muestra el error; si no, la ayuda (si existe).
    val textoApoyo = error ?: ayuda

    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            if (error != null) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
            } else if (correcto) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Campo correcto",
                    tint = VerdePrincipal
                )
            }
        },
        isError = error != null,
        singleLine = unaLinea,
        minLines = lineasMinimas,
        supportingText = if (textoApoyo != null) {
            { Text(textoApoyo) }
        } else {
            null
        }
    )
}