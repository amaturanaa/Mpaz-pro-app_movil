package com.example.mpaz_pro_app_movil

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.components.EtiquetaEstado
import com.example.mpaz_pro_app_movil.datos.Pregunta
import com.example.mpaz_pro_app_movil.datos.Unidad
import com.example.mpaz_pro_app_movil.ui.theme.AzulPrincipal
import com.example.mpaz_pro_app_movil.ui.theme.FondoTarjetas
import com.example.mpaz_pro_app_movil.ui.theme.GrisBorde
import com.example.mpaz_pro_app_movil.ui.theme.TextoPrincipal
import com.example.mpaz_pro_app_movil.ui.theme.TextoSecundario
import com.example.mpaz_pro_app_movil.ui.theme.VerdeClaro
import com.example.mpaz_pro_app_movil.ui.theme.VerdePrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleUnidadUtp(
    unidad: Unidad,
    preguntas: List<Pregunta>,
    onVolver: () -> Unit
) {
    BackHandler { onVolver() }

    Scaffold(
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

        // verticalScroll = la pantalla se desliza y se ven las 5 preguntas.
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
            EtiquetaEstado(estado = unidad.estado)

            BloqueTexto("Objetivo de aprendizaje", unidad.objetivo)
            BloqueTexto("Explicación", unidad.explicacion)
            BloqueTexto("Ejemplo guiado", unidad.ejemplo)

            Text(
                text = "Preguntas (${preguntas.size})",
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold
            )

            if (preguntas.isEmpty()) {
                Text("Esta unidad todavía no tiene preguntas.", color = TextoSecundario)
            }

            preguntas.forEachIndexed { indice, pregunta ->
                TarjetaPregunta(numero = indice + 1, pregunta = pregunta)
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

@Composable
private fun TarjetaPregunta(numero: Int, pregunta: Pregunta) {
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
            Text(
                text = "$numero. ${pregunta.texto}",
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold
            )

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