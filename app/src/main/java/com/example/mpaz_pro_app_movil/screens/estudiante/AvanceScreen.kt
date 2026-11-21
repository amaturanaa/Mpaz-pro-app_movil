package com.example.mpaz_pro_app_movil.screens.estudiante

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.datos.Calificacion
import com.example.mpaz_pro_app_movil.pruebas.paletaDeAsignatura
import com.example.mpaz_pro_app_movil.viewmodel.PruebaUnidad

// Avance del estudiante: una tarjeta por cada prueba asignada.
// Los datos (intentos, mejor nota y promedio) vienen del backend.
@Composable
fun AvanceEstudiante(pruebas: List<PruebaUnidad>) {
    // Cada prueba cuenta solo su nota más alta.
    val promedioGeneral = Calificacion.promedio(pruebas.mapNotNull { it.mejorNota })
    val pruebasRendidas = pruebas.count { it.mejorNota != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Resumen general
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "⭐ Mi avance",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (promedioGeneral != null) {
                        "🏆 Promedio general: ${Calificacion.formatear(promedioGeneral)}"
                    } else {
                        "🌱 Todavía no has rendido ninguna prueba."
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "✅ Pruebas rendidas: $pruebasRendidas de ${pruebas.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Si el docente todavía no asigna nada, lo explicamos.
        if (pruebas.isEmpty()) {
            Text(
                text = "😊 Cuando tu profesor te asigne una unidad, aquí verás tu avance.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Una tarjeta por prueba, con el color de su asignatura.
        pruebas.forEach { prueba ->
            TarjetaAvance(prueba)
        }
    }
}

@Composable
private fun TarjetaAvance(prueba: PruebaUnidad) {
    val colores = paletaDeAsignatura(prueba.unidad.asignatura)
    val mejorNota = prueba.mejorNota
    val promedio = prueba.promedio
    val fraccion = (prueba.intentosUsados.toFloat() / prueba.maxIntentos.coerceAtLeast(1))
        .coerceIn(0f, 1f)
    val sinIntentos = prueba.intentosUsados >= prueba.maxIntentos

    // ANIMACIÓN DE LA BARRA DE INTENTOS
    // La barra parte vacía y se llena suavemente hasta el valor real.
    var objetivo by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(fraccion) { objetivo = fraccion }
    val progresoAnimado by animateFloatAsState(
        targetValue = objetivo,
        animationSpec = tween(durationMillis = 700),
        label = "progresoIntentos"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colores.fondo)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${iconoDeAsignatura(prueba.unidad.asignatura)} ${prueba.unidad.asignatura}",
                style = MaterialTheme.typography.labelMedium,
                color = colores.secundario
            )
            Text(
                text = prueba.unidad.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = colores.oscuro
            )

            if (mejorNota == null || promedio == null) {
                Text(
                    text = "📝 Todavía no has rendido esta prueba.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colores.secundario
                )
            } else {
                Text(
                    text = "🏅 Mejor nota: ${Calificacion.formatear(mejorNota)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = colores.principal
                )
                Text(
                    text = "📊 Promedio: ${Calificacion.formatear(promedio)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colores.secundario
                )
            }

            LinearProgressIndicator(
                progress = { progresoAnimado },
                modifier = Modifier.fillMaxWidth(),
                color = colores.principal,
                trackColor = colores.barraVacia
            )
            Text(
                text = "🎯 Intentos usados: ${prueba.intentosUsados} de ${prueba.maxIntentos}" +
                        if (sinIntentos) " (sin intentos disponibles)" else "",
                style = MaterialTheme.typography.bodySmall,
                color = colores.secundario
            )
        }
    }
}