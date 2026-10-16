package com.example.mpaz_pro_app_movil.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.mpaz_pro_app_movil.components.EtiquetaEstado
import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Pregunta
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.datos.model.siguientesPermitidos
import com.example.mpaz_pro_app_movil.ui.theme.AzulPrincipal
import com.example.mpaz_pro_app_movil.ui.theme.FondoTarjetas
import com.example.mpaz_pro_app_movil.ui.theme.GrisBorde
import com.example.mpaz_pro_app_movil.ui.theme.TextoPrincipal
import com.example.mpaz_pro_app_movil.ui.theme.TextoSecundario
import com.example.mpaz_pro_app_movil.ui.theme.VerdeClaro
import com.example.mpaz_pro_app_movil.ui.theme.VerdePrincipal

// =============================================
// DETALLE DE UNIDAD (perfil UTP)
// =============================================
// Muestra el contenido de la unidad y todas sus preguntas, y permite:
//   - cambiar su estado (Borrador -> En revisión -> ... -> Publicado)
//   - agregar y eliminar preguntas (solo si la unidad no está publicada)
//
// Las funciones onCambiarEstado, onAgregarPregunta y onEliminarPregunta
// devuelven null si salió bien, o el motivo del error para mostrarlo.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleUnidadUtp(
    unidad: Unidad,
    preguntas: List<Pregunta>,
    puedeEditar: Boolean,
    siguienteIdPregunta: Int,
    onVolver: () -> Unit,
    onCambiarEstado: (EstadoUnidad) -> String?,
    onAgregarPregunta: (Pregunta) -> String?,
    onEliminarPregunta: (Int) -> String?
) {
    BackHandler { onVolver() }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Si el formulario de pregunta está abierto.
    var mostrarFormulario by remember { mutableStateOf(false) }

    // Pregunta que la persona quiere eliminar (se pide confirmación).
    var preguntaPorEliminar by remember { mutableStateOf<Pregunta?>(null) }

    fun avisar(mensaje: String) {
        scope.launch { snackbarHostState.showSnackbar(mensaje) }
    }

    // Ventana de confirmación antes de borrar una pregunta.
    val pregunta = preguntaPorEliminar
    if (pregunta != null) {
        AlertDialog(
            onDismissRequest = { preguntaPorEliminar = null },
            title = { Text("Eliminar pregunta") },
            text = { Text("¿Seguro que quieres eliminar \"${pregunta.texto}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    avisar(onEliminarPregunta(pregunta.id) ?: "Pregunta eliminada.")
                    preguntaPorEliminar = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { preguntaPorEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Revisar unidad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        // verticalScroll = la pantalla se desliza y se ven todas las preguntas.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = unidad.asignatura,
                style = MaterialTheme.typography.titleSmall,
                color = AzulPrincipal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unidad.titulo,
                style = MaterialTheme.typography.headlineSmall,
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unidad.codigoOA,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )

            // ---------- Estado y botones para cambiarlo ----------
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
                        text = "Estado de la unidad",
                        style = MaterialTheme.typography.labelLarge,
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold
                    )
                    EtiquetaEstado(estado = unidad.estado)

                    // Solo aparecen los pasos permitidos desde el estado actual.
                    unidad.estado.siguientesPermitidos().forEachIndexed { posicion, siguiente ->
                        val alPulsar = {
                            avisar(
                                onCambiarEstado(siguiente)
                                    ?: "Estado cambiado a ${siguiente.texto}."
                            )
                        }
                        if (posicion == 0) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = alPulsar
                            ) {
                                Text("Pasar a ${siguiente.texto}")
                            }
                        } else {
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = alPulsar
                            ) {
                                Text("Pasar a ${siguiente.texto}")
                            }
                        }
                    }
                }
            }

            BloqueTexto("Objetivo de aprendizaje", unidad.objetivo)
            BloqueTexto("Explicación", unidad.explicacion)
            BloqueTexto("Ejemplo guiado", unidad.ejemplo)

            // ---------- Preguntas ----------
            Text(
                text = "Preguntas (${preguntas.size})",
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold
            )

            if (preguntas.isEmpty()) {
                Text("Esta unidad todavía no tiene preguntas.", color = TextoSecundario)
            }

            preguntas.forEachIndexed { indice, item ->
                TarjetaPregunta(
                    numero = indice + 1,
                    pregunta = item,
                    onEliminar = if (puedeEditar) {
                        { preguntaPorEliminar = item }
                    } else null
                )
            }

            if (puedeEditar) {
                if (mostrarFormulario) {
                    FormularioPregunta(
                        unidadId = unidad.id,
                        siguienteId = siguienteIdPregunta,
                        onGuardar = { nueva ->
                            val error = onAgregarPregunta(nueva)
                            if (error == null) {
                                mostrarFormulario = false
                                avisar("Pregunta agregada.")
                            }
                            error
                        },
                        onCancelar = { mostrarFormulario = false }
                    )
                } else {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { mostrarFormulario = true }
                    ) {
                        Text("Agregar pregunta")
                    }
                }
            } else {
                Text(
                    text = "Unidad publicada: sus preguntas no se pueden cambiar. " +
                            "Para modificarlas hay que deshabilitarla primero.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }
        }
    }
}

@Composable
private fun BloqueTexto(titulo: String, contenido: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjetas)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelLarge,
                color = AzulPrincipal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = contenido,
                style = MaterialTheme.typography.bodyLarge,
                color = TextoPrincipal
            )
        }
    }
}

// onEliminar es null cuando la pregunta no se puede borrar:
// en ese caso no aparece el tacho de basura.
@Composable
private fun TarjetaPregunta(
    numero: Int,
    pregunta: Pregunta,
    onEliminar: (() -> Unit)?
) {
    val letras = listOf("A", "B", "C", "D", "E", "F")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjetas)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$numero. ${pregunta.texto}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (onEliminar != null) {
                    IconButton(onClick = onEliminar) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar pregunta"
                        )
                    }
                }
            }

            pregunta.alternativas.forEachIndexed { indice, alternativa ->
                val esCorrecta = indice == pregunta.correcta

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (esCorrecta) VerdeClaro else GrisBorde.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${letras.getOrElse(indice) { "?" }}) $alternativa",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextoPrincipal
                    )
                    if (esCorrecta) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Respuesta correcta",
                            tint = VerdePrincipal
                        )
                    }
                }
            }
        }
    }
}