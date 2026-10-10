package com.example.mpaz_pro_app_movil.pruebas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.example.mpaz_pro_app_movil.datos.DatosPrueba
import kotlin.math.roundToInt

@Composable
fun PruebaLenguaje(unidadId: Int = 1) {

    // =====================================
    // 1. TOMAMOS LAS PREGUNTAS DE LA UNIDAD
    // =====================================

    // Las preguntas ya no se escriben aquí: vienen de los datos
    // compartidos, los mismos que revisa la UTP.
    val preguntas = remember(unidadId) {
        DatosPrueba.preguntas.filter { pregunta ->
            pregunta.unidadId == unidadId && pregunta.esPruebaFinal
        }
    }

    // Si la unidad todavía no tiene preguntas, lo avisamos y no seguimos.
    if (preguntas.isEmpty()) {
        Text(
            text = "Esta unidad todavía no tiene preguntas.",
            style = MaterialTheme.typography.bodyLarge
        )
        return
    }

    // =====================================
    // 2. CONTROLAMOS QUÉ SE MUESTRA
    // =====================================

    // pantalla = 0: listado de pruebas
    // pantalla = 1: cuestionario
    // pantalla = 2: resultado con nota

    var pantalla by remember {
        mutableIntStateOf(0)
    }

    // Indica qué pregunta está respondiendo.
    // 0 significa la primera pregunta.
    var preguntaActual by remember {
        mutableIntStateOf(0)
    }

    // Aquí guardaremos las respuestas del niño.
    // null significa que no ha respondido.
    val respuestas = remember {
        mutableStateListOf<Int?>().apply {
            repeat(preguntas.size) {
                add(null)
            }
        }
    }

    // =====================================
    // 3. CALCULAMOS LOS PUNTOS Y LA NOTA
    // =====================================

    // Sumamos un punto por cada respuesta correcta.
    val puntaje = preguntas.indices.count { i ->
        respuestas[i] == preguntas[i].correcta
    }

    // Calculamos el porcentaje de respuestas correctas.
    val porcentaje = puntaje.toDouble() / preguntas.size

    // Escala chilena con 60% de exigencia.
    // 60% de respuestas correctas equivale a nota 4.0.
    val notaCalculada = if (porcentaje <= 0.60) {
        1.0 + (porcentaje / 0.60) * 3.0
    } else {
        4.0 + ((porcentaje - 0.60) / 0.40) * 3.0
    }

    // Dejamos la nota con un solo decimal.
    val nota = (notaCalculada * 10).roundToInt() / 10.0

    // =====================================
    // 4. MOSTRAMOS LA PANTALLA
    // =====================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        when (pantalla) {

            // =================================
            // PANTALLA 0: LISTADO DE PRUEBAS
            // =================================

            0 -> {

                Text(
                    text = "Mis pruebas",
                    style = MaterialTheme.typography.titleLarge
                )

                // Esta tarjeta se puede presionar.
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            // Reiniciamos la prueba.
                            preguntaActual = 0

                            // Borramos respuestas anteriores.
                            respuestas.indices.forEach { i ->
                                respuestas[i] = null
                            }

                            // Abrimos el cuestionario.
                            pantalla = 1
                        },

                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Prueba de Lenguaje",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Tema: Gerundios"
                        )

                        Text(
                            text = "${preguntas.size} preguntas - ${preguntas.size} puntos"
                        )

                        Text(
                            text = "Toca aquí para comenzar"
                        )
                    }
                }
            }

            // =================================
            // PANTALLA 1: CUESTIONARIO
            // =================================

            1 -> {

                // Obtenemos la pregunta que corresponde.
                val pregunta = preguntas[preguntaActual]

                Text(
                    text = "Prueba de gerundios",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Pregunta ${preguntaActual + 1} de ${preguntas.size}"
                )

                // Mostramos el enunciado.
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = pregunta.texto,
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Mostramos las cuatro alternativas.
                pregunta.alternativas.forEachIndexed {
                        indice, alternativa ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                // Guardamos la respuesta elegida.
                                respuestas[preguntaActual] = indice
                            }
                            .padding(vertical = 6.dp),

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected =
                                respuestas[preguntaActual] == indice,

                            onClick = {
                                respuestas[preguntaActual] = indice
                            }
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = alternativa,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // BOTONES PARA CAMBIAR DE PREGUNTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Permite regresar a la pregunta anterior.
                    OutlinedButton(
                        enabled = preguntaActual > 0,
                        onClick = {
                            preguntaActual--
                        }
                    ) {
                        Text("Anterior")
                    }

                    // Avanza o termina la prueba.
                    Button(
                        // No permite continuar sin responder.
                        enabled =
                            respuestas[preguntaActual] != null,

                        onClick = {

                            // Si todavía quedan preguntas...
                            if (preguntaActual < preguntas.lastIndex) {

                                preguntaActual++

                            } else {

                                // Terminamos y mostramos la nota.
                                pantalla = 2
                            }
                        }
                    ) {

                        Text(
                            if (preguntaActual == preguntas.lastIndex) {
                                "Finalizar"
                            } else {
                                "Siguiente"
                            }
                        )
                    }
                }
            }

            // =================================
            // PANTALLA 2: RESULTADO FINAL
            // =================================

            2 -> {

                Text(
                    text = "¡Prueba terminada!",
                    style = MaterialTheme.typography.titleLarge
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = "Tu puntaje: $puntaje / ${preguntas.size}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Mostramos la nota con coma decimal.
                        Text(
                            text = "Tu nota: ${
                                String.format(
                                    Locale.forLanguageTag("es-CL"),
                                    "%.1f",
                                    nota
                                )
                            }",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        Text(
                            text = if (nota >= 4.0) {
                                "¡Buen trabajo!"
                            } else {
                                "¡Sigue practicando!"
                            }
                        )
                    }
                }

                // Botón para regresar al listado.
                Button(
                    onClick = {
                        pantalla = 0
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver a mis pruebas")
                }
            }
        }
    }
}