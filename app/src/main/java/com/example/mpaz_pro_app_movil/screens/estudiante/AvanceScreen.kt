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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.datos.Calificacion
import com.example.mpaz_pro_app_movil.datos.ConfigPruebas
import com.example.mpaz_pro_app_movil.pruebas.paletaDeAsignatura
import com.example.mpaz_pro_app_movil.pruebas.utiles.ColoresPrueba
import com.example.mpaz_pro_app_movil.viewmodel.PruebaUnidad


// Los datos de la prueba de una unidad, ya leídos.
private data class ResumenAvance(
    val titulo: String,
    val asignatura: String,
    val notas: List<Double>,
    val intentosUsados: Int,
    val maxIntentos: Int,
    val colores: ColoresPrueba
)

// Avance del estudiante: una tarjeta por cada prueba asignada.
@Composable
fun AvanceEstudiante(pruebas: List<PruebaUnidad>) {
    val context = LocalContext.current

    val resumenes = pruebas.map { prueba ->
        ResumenAvance(
            titulo = prueba.unidad.titulo,
            asignatura = prueba.unidad.asignatura,
            notas = ConfigPruebas.notas(context, prueba.unidad.id),
            intentosUsados = ConfigPruebas.intentosUsados(context, prueba.unidad.id),
            maxIntentos = ConfigPruebas.maxIntentos(context, prueba.unidad.id),
            colores = paletaDeAsignatura(prueba.unidad.asignatura)
        )
    }

    val todasLasNotas = resumenes.flatMap { it.notas }
    val promedioGeneral = Calificacion.promedio(todasLasNotas)
    val pruebasRendidas = resumenes.count { it.notas.isNotEmpty() }

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
                    text = "Mi avance",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (promedioGeneral != null) {
                        "Promedio general: ${Calificacion.formatear(promedioGeneral)}"
                    } else {
                        "Todavía no has rendido ninguna prueba."
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Pruebas rendidas: $pruebasRendidas de ${resumenes.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Si el docente todavía no asigna nada, lo explicamos.
        if (resumenes.isEmpty()) {
            Text(
                text = "Cuando tu profesor te asigne una unidad, aquí verás tu avance.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Una tarjeta por prueba, con el color de su asignatura.
        resumenes.forEach { resumen ->
            TarjetaAvance(resumen)
        }
    }
}

@Composable
private fun TarjetaAvance(resumen: ResumenAvance) {
    val colores = resumen.colores
    val mejorNota = resumen.notas.maxOrNull()
    val promedio = Calificacion.promedio(resumen.notas)
    val fraccion = (resumen.intentosUsados.toFloat() / resumen.maxIntentos).coerceIn(0f, 1f)
    val sinIntentos = resumen.intentosUsados >= resumen.maxIntentos

    // ANIMACIÓN DE LA BARRA DE INTENTOS
    // La barra parte vacía y, al abrir la pestaña, se llena suavemente
    // hasta el valor real. LaunchedEffect cambia el objetivo después de
    // dibujar la primera vez; animateFloatAsState hace el recorrido.
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
                text = resumen.asignatura,
                style = MaterialTheme.typography.labelMedium,
                color = colores.secundario
            )
            Text(
                text = resumen.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = colores.oscuro
            )

            if (mejorNota == null || promedio == null) {
                Text(
                    text = "Todavía no has rendido esta prueba.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colores.secundario
                )
            } else {
                Text(
                    text = "Mejor nota: ${Calificacion.formatear(mejorNota)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = colores.principal
                )
                Text(
                    text = "Promedio: ${Calificacion.formatear(promedio)}",
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
                text = "Intentos usados: ${resumen.intentosUsados} de ${resumen.maxIntentos}" +
                        if (sinIntentos) " (sin intentos disponibles)" else "",
                style = MaterialTheme.typography.bodySmall,
                color = colores.secundario
            )
        }
    }
}