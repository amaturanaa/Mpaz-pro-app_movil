package com.example.mpaz_pro_app_movil.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.datos.Calificacion
import com.example.mpaz_pro_app_movil.datos.ConfigPruebas
import com.example.mpaz_pro_app_movil.pruebas.utiles.ColoresPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PaletasPrueba


// Los datos de una asignatura, ya leídos.
private data class ResumenAvance(
    val asignatura: String,
    val notas: List<Double>,
    val intentosUsados: Int,
    val maxIntentos: Int
)

@Composable
fun AvanceEstudiante(asignaturas: List<String>) {
    val context = LocalContext.current

    val resumenes = asignaturas.map { asignatura ->
        ResumenAvance(
            asignatura = asignatura,
            notas = ConfigPruebas.notas(context, asignatura),
            intentosUsados = ConfigPruebas.intentosUsados(context, asignatura),
            maxIntentos = ConfigPruebas.maxIntentos(context, asignatura)
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
                    text = "Pruebas rendidas: $pruebasRendidas de ${asignaturas.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Una tarjeta por asignatura, con el color de su prueba.
        // El orden coincide con la lista del ViewModel:
        // 0 Lenguaje, 1 Matemáticas, 2 Ciencias, 3 Historia.
        resumenes.forEachIndexed { posicion, resumen ->
            val colores = when (posicion) {
                0 -> PaletasPrueba.lenguaje
                1 -> PaletasPrueba.matematicas
                2 -> PaletasPrueba.ciencias
                else -> PaletasPrueba.historia
            }
            TarjetaAvance(resumen, colores)
        }
    }
}

@Composable
private fun TarjetaAvance(resumen: ResumenAvance, colores: ColoresPrueba) {
    val mejorNota = resumen.notas.maxOrNull()
    val promedio = Calificacion.promedio(resumen.notas)
    val fraccion = (resumen.intentosUsados.toFloat() / resumen.maxIntentos).coerceIn(0f, 1f)
    val sinIntentos = resumen.intentosUsados >= resumen.maxIntentos

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
                progress = { fraccion },
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