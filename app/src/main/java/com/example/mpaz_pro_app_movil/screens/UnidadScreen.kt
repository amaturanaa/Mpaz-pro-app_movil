package com.example.mpaz_pro_app_movil.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadScreen(asignatura: String, onVolver: () -> Unit) {
    // Recuerda si el estudiante abrió la nueva actividad de animales.
    // Solo se usa cuando estamos dentro de Ciencias Naturales.
    var mostrandoActividadAnimales by rememberSaveable(asignatura) {
        mutableStateOf(false)
    }

    // La actividad tiene su propia pantalla, su botón Volver y
    // su propio tratamiento del botón atrás del teléfono.
    if (asignatura == "Ciencias Naturales" && mostrandoActividadAnimales) {
        ActividadAnimalesScreen(onVolver = { mostrandoActividadAnimales = false })
        return
    }

    // El botón atrás del teléfono también vuelve a la lista
    BackHandler(onBack = onVolver)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(asignatura) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Lee y escucha", style = MaterialTheme.typography.titleMedium)
                    Text(explicacionDe(asignatura), style = MaterialTheme.typography.bodyLarge)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Ejemplo", style = MaterialTheme.typography.titleMedium)
                    Text(ejemploDe(asignatura), style = MaterialTheme.typography.bodyLarge)
                }
            }

            // ====================================================
            // NUEVA ACTIVIDAD (SOLO CIENCIAS NATURALES)
            // ====================================================
            // La evaluación final sigue en la pestaña "Pruebas".
            // Esta actividad práctica NO consume los 3 intentos.
            if (asignatura == "Ciencias Naturales") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFEDF5FF)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Actividad: Exploradores de animales",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Saca o elige entre 3 y 10 fotos de animales, " +
                                    "incluidos insectos, y clasifícalos como " +
                                    "vertebrados o invertebrados."
                        )
                        Button(
                            onClick = { mostrandoActividadAnimales = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Abrir actividad de animales")
                        }
                    }
                }
            }

            // Reproduce la explicación y el ejemplo en voz alta
            BotonNarracion(asignatura)
        }
            // Por ahora no hace nada: aquí irá la vista de práctica
            Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Star, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Practicar")
            }
        }
    }


// Contenido de prueba. Después vendrá de la base de datos.
fun explicacionDe(asignatura: String): String = when (asignatura) {
    "Lenguaje y Comunicación" ->
        "Una oración comunica una idea completa. Comienza con mayúscula y termina con un punto."
    "Matemática" ->
        "Sumar es juntar cantidades. Podemos contar hacia adelante para encontrar el total."
    "Ciencias Naturales" ->
        "Las plantas son seres vivos. Necesitan agua, luz y aire para crecer."
    else ->
        "Una comunidad está formada por personas que comparten lugares, normas y servicios."
}

fun ejemploDe(asignatura: String): String = when (asignatura) {
    "Lenguaje y Comunicación" -> "\"El perro corre.\" tiene una idea completa."
    "Matemática" -> "6 + 4 = 10. Desde 6 avanzamos cuatro números: 7, 8, 9 y 10."
    "Ciencias Naturales" -> "Una planta cerca de la ventana recibe luz y hay que regarla."
    else -> "La escuela, la plaza y el consultorio son lugares de la comunidad."
}