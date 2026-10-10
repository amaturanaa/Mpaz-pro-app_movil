package com.example.mpaz_pro_app_movil.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.datos.EstadoUnidad
import com.example.mpaz_pro_app_movil.ui.theme.*

// Menú desplegable con opciones.
@Composable
fun MenuOpciones(
    isExpanded: Boolean,
    opciones: List<String>,
    onItemClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = isExpanded, onDismissRequest = onDismiss) {
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

// Etiqueta de color con el estado de una unidad.
@Composable
fun EtiquetaEstado(estado: EstadoUnidad) {
    Surface(
        color = colorDeEstado(estado),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = estado.texto,
            style = MaterialTheme.typography.labelLarge,
            color = TextoPrincipal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

// Qué color de la paleta le corresponde a cada estado.
fun colorDeEstado(estado: EstadoUnidad): Color = when (estado) {
    EstadoUnidad.BORRADOR -> GrisBorde
    EstadoUnidad.EN_REVISION -> AmarilloClaro
    EstadoUnidad.AUTORIZADO -> AzulClaro
    EstadoUnidad.PUBLICADO -> VerdeClaro
    EstadoUnidad.DESHABILITADO -> RosadoClaro
}