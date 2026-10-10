package com.example.mpaz_pro_app_movil.pruebas

// ============================================================
// IMPORTACIONES: herramientas de Android y Jetpack Compose.
// ============================================================
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.example.mpaz_pro_app_movil.datos.DatosPrueba
import kotlin.math.roundToInt

// ============================================================
// MODELO: estructura común para las cinco preguntas.
// ============================================================
// 'correcta' es el índice de la alternativa buena:
// 0 = primera; 1 = segunda; 2 = tercera; 3 = cuarta.
// 'explicacion' se muestra al revisar las respuestas.
private data class PreguntaPruebaLenguaje(
    val texto: String,
    val alternativas: List<String>,
    val correcta: Int,
    val explicacion: String
)

// ============================================================
// PANTALLA PRINCIPAL DE LA PRUEBA DE LENGUAJE.
// ============================================================
@Composable
fun PruebaLenguaje() {
    // El contexto permite acceder al almacenamiento de esta app.
    val context = LocalContext.current

    // Cada asignatura usa un archivo DISTINTO de SharedPreferences.
    // Se mantienen las mismas claves de la versión anterior para
    // NO borrar el historial de notas ni los intentos ya utilizados.
    val preferencias = remember(context) {
        context.getSharedPreferences("prueba_lenguaje", Context.MODE_PRIVATE)
    }

    // COMPATIBILIDAD CON VERSIONES ANTERIORES:
    // Una versión antigua guardaba los intentos con el nombre
    // "prueba_lenguaje_gerundios". Si todavía existen datos ahí y
    // NO tenemos datos en el almacenamiento actual, los copiamos.
    // Así no se pierden los intentos ni las notas ya conseguidas.
    val preferenciasAntiguas = remember(context) {
        context.getSharedPreferences("prueba_lenguaje_gerundios", Context.MODE_PRIVATE)
    }
    remember(preferencias, preferenciasAntiguas) {
        val editor = preferencias.edit()
        var necesitaGuardar = false
        if (!preferencias.contains("intentos_usados") &&
            preferenciasAntiguas.contains("intentos_usados")) {
            editor.putInt(
                "intentos_usados",
                preferenciasAntiguas.getInt("intentos_usados", 0)
            )
            necesitaGuardar = true
        }
        if (!preferencias.contains("historial_notas") &&
            preferenciasAntiguas.contains("historial_notas")) {
            editor.putString(
                "historial_notas",
                preferenciasAntiguas.getString("historial_notas", "").orEmpty()
            )
            necesitaGuardar = true
        }
        if (necesitaGuardar) editor.apply()
        true
    }

    // Máximo permitido, por defecto tres.
    // ProfesorScreen.kt comparte este almacenamiento y puede
    // cambiar "max_intentos" entre 1 y 3.
    var maxIntentos by remember(preferencias) {
        mutableIntStateOf(preferencias.getInt("max_intentos", 3).coerceIn(1, 3))
    }

    // Recuperamos el número de intentos FINALIZADOS.
    var intentosUsados by remember(preferencias) {
        mutableIntStateOf(preferencias.getInt("intentos_usados", 0))
    }

    // Recuperamos las notas separadas por punto y coma.
    // mutableStateListOf actualiza automáticamente la pantalla.
    val historialNotas = remember(preferencias) {
        mutableStateListOf<Double>().apply {
            val textoGuardado = preferencias.getString("historial_notas", "").orEmpty()
            textoGuardado.split(";").forEach { texto ->
                texto.toDoubleOrNull()?.let { add(it) }
            }
        }
    }

    // ========================================================
    // BANCO DE PREGUNTAS: contenidos de Lenguaje para segundo básico (Chile).
    // ========================================================
    val preguntas = remember {
        listOf(
            // Pregunta 1: reconocer el gerundio de «cantar».
            PreguntaPruebaLenguaje(
                texto = "¿Cuál es el gerundio de cantar?",
                alternativas = listOf("Cantar", "Cantando", "Cantó", "Canta"),
                correcta = 1,
                explicacion = "El gerundio de cantar es cantando. Termina en -ando."
            ),
            // Pregunta 2: completar una oración con gerundio.
            PreguntaPruebaLenguaje(
                texto = "La niña está ___ un libro.",
                alternativas = listOf("Leyendo", "Leer", "Leyó", "Lee"),
                correcta = 0,
                explicacion = "Decimos «está leyendo». Leyendo expresa una acción en desarrollo."
            ),
            // Pregunta 3: reconocer el gerundio de «correr».
            PreguntaPruebaLenguaje(
                texto = "¿Cuál es el gerundio de correr?",
                alternativas = listOf("Corría", "Corrió", "Corriendo", "Corre"),
                correcta = 2,
                explicacion = "El gerundio de correr es corriendo. Termina en -iendo."
            ),
            // Pregunta 4: completar una oración con gerundio.
            PreguntaPruebaLenguaje(
                texto = "Los niños están ___ en el patio.",
                alternativas = listOf("Jugar", "Jugaron", "Juegan", "Jugando"),
                correcta = 3,
                explicacion = "La oración correcta es «Los niños están jugando en el patio»."
            ),
            // Pregunta 5: reconocer el gerundio de «escribir».
            PreguntaPruebaLenguaje(
                texto = "¿Cuál es el gerundio de escribir?",
                alternativas = listOf("Escribiendo", "Escribe", "Escribió", "Escribir"),
                correcta = 0,
                explicacion = "El gerundio de escribir es escribiendo. Termina en -iendo."
            )
        )
        return
    }

    // Control de pantalla:
    // 0 = tarjeta con información, 1 = cuestionario, 2 = resultado.
    var pantalla by remember { mutableIntStateOf(0) }

    // La primera pregunta ocupa el índice cero.
    var preguntaActual by remember { mutableIntStateOf(0) }

    // Respuestas seleccionadas, una por pregunta.
    // null significa que el estudiante aún no ha respondido.
    val respuestas = remember {
        mutableStateListOf<Int?>().apply {
            repeat(preguntas.size) { add(null) }
        }
    }

    // Permite mostrar u ocultar la revisión en la pantalla de resultado.
    var mostrarRevision by remember { mutableStateOf(false) }

    // Nunca mostramos intentos restantes negativos.
    val intentosRestantes = (maxIntentos - intentosUsados).coerceAtLeast(0)

    // Calculamos cuántas respuestas son correctas.
    val puntaje = preguntas.indices.count { indice ->
        respuestas[indice] == preguntas[indice].correcta
    }

    // Proporción de aciertos: 3 / 5 = 0.60 = 60 %.
    val porcentaje = puntaje.toDouble() / preguntas.size

    // Escala referencial chilena 1,0 a 7,0 con 60 % de exigencia.
    // Tres respuestas correctas de cinco equivalen a un 4,0.
    val notaSinRedondear = if (porcentaje <= 0.60) {
        1.0 + (porcentaje / 0.60) * 3.0
    } else {
        4.0 + ((porcentaje - 0.60) / 0.40) * 3.0
    }
    val nota = (notaSinRedondear * 10).roundToInt() / 10.0

    // La nota se muestra con coma decimal: 5,5 en vez de 5.5.
    fun notaChilena(valor: Double): String = String.format(
        Locale.forLanguageTag("es-CL"), "%.1f", valor
    )

    // Paleta de colores, igual a la tarjeta renovada de Lenguaje.
    val azulOscuro = Color(0xFF1C365F)
    val azul = Color(0xFF3169C6)
    val azulPastel = Color(0xFFEDF5FF)
    val textoSecundario = Color(0xFF557195)
    val verdeSuave = Color(0xFFD5F3E7)
    val rojoSuave = Color(0xFFFFDFDF)

    // Esta columna puede desplazarse y ocupa el espacio que le
    // entrega EstudianteScreen, sin necesitar una altura especial.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (pantalla) {

            // ====================================================
            // PANTALLA 0: TARJETA DE INICIO E HISTORIAL DE NOTAS.
            // ====================================================
            0 -> {
                // Leemos nuevamente el valor si el profesor lo cambió
                // antes de entrar a esta pantalla de prueba.
                LaunchedEffect(Unit) {
                    maxIntentos = preferencias.getInt("max_intentos", 3).coerceIn(1, 3)
                }

                Text(
                    text = "Mis pruebas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Tarjeta azul: título a la izquierda, intentos a la derecha.
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = azulPastel)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = "Prueba de Lenguaje",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = azulOscuro
                                )
                                Text(
                                    text = "Gerundios · Lenguaje y Comunicación",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textoSecundario
                                )
                                Text(
                                    text = "5 preguntas · 5 puntos",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textoSecundario
                                )
                            }

                            // Etiqueta verde si quedan intentos, roja si no.
                            Column(
                                modifier = Modifier
                                    .background(
                                        if (intentosRestantes > 0) verdeSuave else rojoSuave,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$intentosRestantes de $maxIntentos",
                                    fontWeight = FontWeight.Bold,
                                    color = if (intentosRestantes > 0)
                                        Color(0xFF126B4B) else Color(0xFFB3261E)
                                )
                                Text(
                                    text = "Disponibles",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textoSecundario
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFC7D9F1))

                        // Estado a la izquierda y botón principal a la derecha.
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Utilizados: $intentosUsados",
                                color = textoSecundario,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Button(
                                // El bloqueo es real: no se inicia si ya
                                // se consumieron todos los intentos.
                                enabled = intentosRestantes > 0,
                                onClick = {
                                    preguntaActual = 0
                                    mostrarRevision = false
                                    // Limpiamos respuestas del intento anterior,
                                    // PERO NO borramos notas ni intentos.
                                    respuestas.indices.forEach { indice ->
                                        respuestas[indice] = null
                                    }
                                    pantalla = 1
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = azul)
                            ) {
                                Text(if (intentosRestantes > 0) "Comenzar →" else "Bloqueada")
                            }
                        }
                    }
                }

                // Historial visible incluso cuando la prueba está bloqueada.
                if (historialNotas.isNotEmpty()) {
                    Text(
                        text = "Mis notas anteriores",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    historialNotas.forEachIndexed { indice, notaAnterior ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Intento ${indice + 1}")
                                Text(
                                    notaChilena(notaAnterior),
                                    fontWeight = FontWeight.Bold,
                                    color = azul
                                )
                            }
                        }
                    }
                }
            }

            // ====================================================
            // PANTALLA 1: PREGUNTAS Y ALTERNATIVAS.
            // ====================================================
            1 -> {
                Text(
                    text = "Prueba de Lenguaje",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Intento ${intentosUsados + 1} de $maxIntentos",
                    color = textoSecundario
                )

                // Tarjeta del progreso: número actual y cinco barritas.
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = azulPastel),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tu progreso", color = azulOscuro, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${preguntaActual + 1} de ${preguntas.size}",
                                color = azulOscuro
                            )
                        }

                        // Una barra por pregunta, sin bibliotecas adicionales.
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(preguntas.size) { indice ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(7.dp)
                                        .background(
                                            if (indice <= preguntaActual) azul else Color(0xFFD1DEF2),
                                            RoundedCornerShape(6.dp)
                                        )
                                )
                            }
                        }
                    }
                }

                // Recuadro de la pregunta actual.
                val pregunta = preguntas[preguntaActual]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = azulPastel)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "PREGUNTA ${preguntaActual + 1}",
                            color = azul,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            pregunta.texto,
                            color = azulOscuro,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    "Selecciona una alternativa:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textoSecundario
                )

                // Cada alternativa es una tarjeta completa y seleccionable.
                pregunta.alternativas.forEachIndexed { indice, alternativa ->
                    val seleccionada = respuestas[preguntaActual] == indice
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { respuestas[preguntaActual] = indice },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp,
                            if (seleccionada) azul else Color(0xFFD5DDE9)
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionada)
                                Color(0xFFE6F0FF)
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = seleccionada,
                                onClick = { respuestas[preguntaActual] = indice },
                                colors = RadioButtonDefaults.colors(selectedColor = azul)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                alternativa,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (seleccionada) azulOscuro
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Navegación entre las cinco preguntas.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        enabled = preguntaActual > 0,
                        onClick = { preguntaActual-- }
                    ) { Text("Anterior") }

                    Button(
                        // El estudiante no puede continuar sin responder.
                        enabled = respuestas[preguntaActual] != null,
                        onClick = {
                            if (preguntaActual < preguntas.lastIndex) {
                                // Avanza sin perder la selección anterior.
                                preguntaActual++
                            } else if (intentosUsados < maxIntentos) {
                                // Finalizar consume UN intento, no cada pregunta.
                                intentosUsados++
                                historialNotas.add(nota)

                                // Guardamos con las MISMAS claves antiguas
                                // para preservar el historial existente.
                                preferencias.edit()
                                    .putInt("intentos_usados", intentosUsados)
                                    .putString("historial_notas", historialNotas.joinToString(";"))
                                    .apply()

                                mostrarRevision = false
                                pantalla = 2
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = azul)
                    ) {
                        Text(if (preguntaActual == preguntas.lastIndex) "Finalizar" else "Siguiente")
                    }
                }
            }

            // ====================================================
            // PANTALLA 2: NOTA, PUNTAJE Y REVISIÓN DE RESPUESTAS.
            // ====================================================
            2 -> {
                Text(
                    text = "¡Prueba terminada!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = azulPastel)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Tu resultado", color = azulOscuro, fontWeight = FontWeight.SemiBold)
                        Text(
                            notaChilena(nota),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = azul
                        )
                        Text("Respuestas correctas: $puntaje de ${preguntas.size}", color = azulOscuro)
                        Text(
                            if (nota >= 4.0) "¡Buen trabajo!" else "¡Sigue practicando!",
                            color = textoSecundario
                        )
                        HorizontalDivider(color = Color(0xFFC7D9F1))
                        Text("Intentos restantes: $intentosRestantes", color = azulOscuro)
                        if (intentosRestantes == 0) {
                            Text(
                                "Ya no puedes volver a rendir esta prueba.",
                                color = Color(0xFFB3261E)
                            )
                        }
                    }
                }

                // Revisión con retroalimentación educativa, opcional.
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { mostrarRevision = !mostrarRevision }
                ) {
                    Text(if (mostrarRevision) "Ocultar respuestas" else "Revisar respuestas")
                }

                if (mostrarRevision) {
                    preguntas.forEachIndexed { indice, pregunta ->
                        val fueCorrecta = respuestas[indice] == pregunta.correcta
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (fueCorrecta) verdeSuave else rojoSuave
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text("Pregunta ${indice + 1}: ${pregunta.texto}", fontWeight = FontWeight.Bold)
                                Text("Tu respuesta: ${pregunta.alternativas[respuestas[indice] ?: 0]}")
                                Text("Correcta: ${pregunta.alternativas[pregunta.correcta]}")
                                Text(pregunta.explicacion)
                            }
                        }
                    }
                }

                Button(
                    onClick = { pantalla = 0 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = azul)
                ) {
                    Text("Volver a mis pruebas")
                }
            }
        }
    }
}
