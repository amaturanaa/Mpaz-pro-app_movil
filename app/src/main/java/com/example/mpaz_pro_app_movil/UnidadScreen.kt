package com.example.mpaz_pro_app_movil

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadScreen(asignatura: String, onVolver: () -> Unit) {
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

            // Por ahora no hace nada: aquí irá el texto a voz
            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Escuchar")
            }

            // Por ahora no hace nada: aquí irá la vista de práctica
            Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Star, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Practicar")
            }
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