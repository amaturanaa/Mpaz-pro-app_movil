package com.example.mpaz_pro_app_movil.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MenuOpciones(
    isExpanded: Boolean,
    opciones: List<String>,
    onItemClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = isExpanded, onDismissRequest = onDismiss){
        opciones.forEach { opcion ->
            DropdownMenuItem(
                text = { Text(text = opcion) },
                onClick = {
                    onItemClick(opcion)
                    onDismiss()
                }
            )

        }
    }

}