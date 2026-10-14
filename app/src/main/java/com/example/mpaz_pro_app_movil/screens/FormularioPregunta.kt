package com.example.mpaz_pro_app_movil.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.components.CampoValidado
import com.example.mpaz_pro_app_movil.components.MensajeError
import com.example.mpaz_pro_app_movil.datos.Pregunta
import com.example.mpaz_pro_app_movil.ui.theme.FondoTarjetas
import com.example.mpaz_pro_app_movil.validacion.Validador

// =============================================
// FORMULARIO DE PREGUNTA (perfil UTP)
// =============================================
// Una pregunta tiene un texto, cuatro alternativas y una correcta.
//
// unidadId    : unidad a la que pertenece la pregunta
// siguienteId : número que recibirá la pregunta
// onGuardar   : intenta guardar; devuelve null si salió bien o el motivo del error
// onCancelar  : cierra el formulario sin guardar
@Composable
fun FormularioPregunta(
    unidadId: Int,
    siguienteId: Int,
    onGuardar: (Pregunta) -> String?,
    onCancelar: () -> Unit
) {
    val letras = listOf("A", "B", "C", "D")

    var texto by remember { mutableStateOf("") }
    val alternativas = remember { mutableStateListOf("", "", "", "") }
    var correcta by remember { mutableStateOf<Int?>(null) }

    // Los errores solo se muestran después de tocar "Guardar" la primera vez.
    var mostrarErrores by remember { mutableStateOf(false) }
    var errorAlGuardar by remember { mutableStateOf<String?>(null) }

    // Cada error sale del Validador central. null significa "está bien".
    val errorTexto = Validador.longitud(texto, "El texto de la pregunta", 10, 150)
    val erroresAlternativas = alternativas.mapIndexed { posicion, alternativa ->
        Validador.longitud(alternativa, "El texto de la alternativa ${letras[posicion]}", 1, 60)
    }
    val errorDistintas = Validador.alternativasDistintas(alternativas.toList())
    val errorCorrecta = Validador.alternativaCorrecta(correcta)

    val todoCorrecto = errorTexto == null &&
            erroresAlternativas.all { it == null } &&
            errorDistintas == null &&
            errorCorrecta == null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjetas)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Nueva pregunta",
                style = MaterialTheme.typography.titleMedium
            )

            CampoValidado(
                valor = texto,
                alCambiar = { texto = it },
                etiqueta = "Pregunta",
                icono = Icons.Filled.Edit,
                error = if (mostrarErrores) errorTexto else null,
                correcto = errorTexto == null,
                ayuda = "Entre 10 y 150 caracteres.",
                unaLinea = false,
                lineasMinimas = 2
            )

            // Las cuatro alternativas, A, B, C y D.
            repeat(4) { posicion ->
                CampoValidado(
                    valor = alternativas[posicion],
                    alCambiar = { alternativas[posicion] = it },
                    etiqueta = "Alternativa ${letras[posicion]}",
                    icono = Icons.AutoMirrored.Filled.List,
                    error = if (mostrarErrores) erroresAlternativas[posicion] else null,
                    correcto = erroresAlternativas[posicion] == null
                )
            }
            MensajeError(if (mostrarErrores) errorDistintas else null)

            // La correcta se elige tocando una letra.
            Text(
                text = "¿Cuál es la alternativa correcta?",
                style = MaterialTheme.typography.labelLarge
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                letras.forEachIndexed { posicion, letra ->
                    FilterChip(
                        selected = correcta == posicion,
                        onClick = { correcta = posicion },
                        label = { Text(letra) }
                    )
                }
            }
            MensajeError(if (mostrarErrores) errorCorrecta else null)

            // Si el ViewModel rechaza guardar, aquí se explica por qué.
            MensajeError(errorAlGuardar)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        mostrarErrores = true

                        val posicionCorrecta = correcta
                        if (todoCorrecto && posicionCorrecta != null) {
                            errorAlGuardar = onGuardar(
                                Pregunta(
                                    id = siguienteId,
                                    unidadId = unidadId,
                                    texto = texto.trim(),
                                    alternativas = alternativas.map { it.trim() },
                                    correcta = posicionCorrecta,
                                    esPruebaFinal = true
                                )
                            )
                        }
                    }
                ) {
                    Text("Guardar pregunta")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onCancelar
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}