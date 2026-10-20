package com.example.mpaz_pro_app_movil.screens.estudiante

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.ui.theme.AzulClaro
import com.example.mpaz_pro_app_movil.ui.theme.TextoPrincipal
import com.example.mpaz_pro_app_movil.viewmodel.UnidadAsignada

// Pantalla de UNA asignatura.
// unidades      -> las unidades de ESTA asignatura que el docente asignó
//                  (EstudianteScreen ya las entrega filtradas)
// onAbrirUnidad -> se ejecuta cuando el alumno toca una unidad
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadScreen(
    asignatura: String,
    unidades: List<UnidadAsignada>,
    onAbrirUnidad: (Unidad) -> Unit,
    onVolver: () -> Unit
) {
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

            // ====================================================
            // UNIDADES DE ESTA ASIGNATURA
            // ====================================================
            // Antes estaban en la pantalla principal, mezcladas con todas
            // las asignaturas. Ahora cada una vive dentro de la suya.
            Text(
                text = "Unidades de esta asignatura",
                style = MaterialTheme.typography.titleLarge
            )

            if (unidades.isEmpty()) {
                Text(
                    text = "Tu profesor todavía no te ha asignado unidades de esta asignatura.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // "(unidad, cursos, fechaHasta)" saca los tres datos de UnidadAsignada.
            unidades.forEach { (unidad, cursos, fechaHasta) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Al tocar la tarjeta, avisamos qué unidad se eligió.
                        .clickable { onAbrirUnidad(unidad) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulClaro)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Título de la unidad en grande.
                        Text(
                            text = unidad.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextoPrincipal
                        )
                        // Objetivo, para que el alumno sepa de qué trata.
                        Text(
                            text = unidad.objetivo,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoPrincipal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Curso: ${cursos.joinToString(", ")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoPrincipal
                        )
                        Text(
                            text = "Disponible hasta: $fechaHasta",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoPrincipal
                        )
                    }
                }
            }

            Text(
                text = "Para repasar",
                style = MaterialTheme.typography.titleLarge
            )

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
            // Esta actividad práctica NO consume los intentos.
            if (asignatura == "Ciencias Naturales") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEDF5FF)
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
    }
}


// Contenido de repaso general de cada asignatura.
fun explicacionDe(asignatura: String): String = when (asignatura) {
    "Lenguaje y Comunicación" ->
        "Una oración comunica una idea completa. Comienza con mayúscula y termina con un punto."
    "Matemáticas" ->
        "Sumar es juntar cantidades. Podemos contar hacia adelante para encontrar el total."
    "Ciencias Naturales" ->
        "Las plantas son seres vivos. Necesitan agua, luz y aire para crecer."
    else ->
        "Una comunidad está formada por personas que comparten lugares, normas y servicios."
}

fun ejemploDe(asignatura: String): String = when (asignatura) {
    "Lenguaje y Comunicación" -> "\"El perro corre.\" tiene una idea completa."
    "Matemáticas" -> "6 + 4 = 10. Desde 6 avanzamos cuatro números: 7, 8, 9 y 10."
    "Ciencias Naturales" -> "Una planta cerca de la ventana recibe luz y hay que regarla."
    else -> "La escuela, la plaza y el consultorio son lugares de la comunidad."
}