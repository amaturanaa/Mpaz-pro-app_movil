package com.example.mpaz_pro_app_movil.pruebas.utiles

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
// NUEVO (tarea 4): para saber si el docente permite revisar respuestas.
import com.example.mpaz_pro_app_movil.datos.ConfigPruebas
import java.util.Locale
import kotlin.math.roundToInt

// ============================================================
// NUEVO (tarea 5): PRUEBA COMPARTIDA POR LAS CUATRO ASIGNATURAS
// ============================================================
// Antes, PruebaLenguaje, PruebaMatematicas, PruebaCiencias y PruebaHistoria
// tenían cada una ~570 líneas CASI IGUALES: solo cambiaban las preguntas,
// los títulos y el archivo donde se guardan las notas.
// Ahora toda la lógica (intentos, nota chilena, pantallas, revisión)
// vive UNA sola vez aquí, y cada prueba solo entrega sus datos.
// Si hay que arreglar algo de las pruebas, se arregla en este archivo
// y queda arreglado en las cuatro.

// MODELO: estructura común para las preguntas de cualquier prueba.
// 'correcta' es el índice de la alternativa buena:
// 0 = primera; 1 = segunda; 2 = tercera; 3 = cuarta.
// 'explicacion' se muestra al revisar las respuestas.
data class PreguntaPrueba(
    val texto: String,
    val alternativas: List<String>,
    val correcta: Int,
    val explicacion: String
)

// LO QUE CAMBIA DE UNA PRUEBA A OTRA.
// titulo          -> "Prueba de Matemática"
// subtitulo       -> texto pequeño bajo el título
// asignatura      -> nombre completo, para preguntarle a ConfigPruebas
//                    si el docente permite revisar respuestas
// archivo         -> archivo de SharedPreferences donde se guardan
//                    intentos y notas (debe coincidir con ConfigPruebas.archivoDe)
// preguntas       -> el banco de preguntas
// colores         -> paleta suave de la asignatura (ver PaletasPrueba)
// archivoAntiguo  -> solo Lenguaje: archivo de una versión vieja de la app
//                    del que se copian intentos y notas (null = no hay)
// =============================================
// NUEVO (colores): PALETA DE COLORES DE UNA PRUEBA
// =============================================
// Cada prueba usa una versión SUAVE del color de su asignatura
// (los mismos de las tarjetas de "Mis asignaturas"):
// Lenguaje = rosado, Matemáticas = azul, Ciencias = verde, Historia = amarillo.
// fondo      -> tarjetas grandes (inicio, pregunta, resultado)
// principal  -> botones, barra de progreso, nota y alternativa elegida
// oscuro     -> títulos y textos importantes sobre el fondo
// secundario -> textos pequeños grises
// seleccion  -> fondo de la alternativa que el niño eligió
// divisor    -> línea delgada dentro de las tarjetas
// barraVacia -> barritas de progreso que todavía no se completan
data class ColoresPrueba(
    val fondo: Color,
    val principal: Color,
    val oscuro: Color,
    val secundario: Color,
    val seleccion: Color,
    val divisor: Color,
    val barraVacia: Color
)

// Las cuatro paletas, en un solo lugar para cambiarlas fácil.
// Los colores "principal" son lo bastante oscuros para que el texto
// blanco de los botones se lea bien.
object PaletasPrueba {
    val lenguaje = ColoresPrueba(
        fondo = Color(0xFFFFEEF3),
        principal = Color(0xFFB83B66),
        oscuro = Color(0xFF6B1F3A),
        secundario = Color(0xFF8A5468),
        seleccion = Color(0xFFFFE0EA),
        divisor = Color(0xFFF6C9D7),
        barraVacia = Color(0xFFF3D3DE)
    )
    // Matemáticas conserva el azul que ya tenían todas las pruebas.
    val matematicas = ColoresPrueba(
        fondo = Color(0xFFEDF5FF),
        principal = Color(0xFF3169C6),
        oscuro = Color(0xFF1C365F),
        secundario = Color(0xFF557195),
        seleccion = Color(0xFFE6F0FF),
        divisor = Color(0xFFC7D9F1),
        barraVacia = Color(0xFFD1DEF2)
    )
    val ciencias = ColoresPrueba(
        fondo = Color(0xFFEAF8F0),
        principal = Color(0xFF188272),
        oscuro = Color(0xFF0F4D3F),
        secundario = Color(0xFF4F7A6C),
        seleccion = Color(0xFFDDF3E7),
        divisor = Color(0xFFBFE6D2),
        barraVacia = Color(0xFFCDEBDC)
    )
    val historia = ColoresPrueba(
        fondo = Color(0xFFFFF7E0),
        principal = Color(0xFF9A6200),
        oscuro = Color(0xFF5C3D00),
        secundario = Color(0xFF85704A),
        seleccion = Color(0xFFFFF0C7),
        divisor = Color(0xFFF2DFA6),
        barraVacia = Color(0xFFF0E3BC)
    )
}

data class ConfiguracionPrueba(
    val titulo: String,
    val subtitulo: String,
    val asignatura: String,
    val archivo: String,
    val preguntas: List<PreguntaPrueba>,
    val archivoAntiguo: String? = null,
    // NUEVO (colores): si una prueba no dice nada, queda azul como antes.
    val colores: ColoresPrueba = PaletasPrueba.matematicas
)

// ============================================================
// PANTALLA COMPARTIDA DE LAS PRUEBAS
// ============================================================
// Recibe la configuración de UNA prueba y dibuja sus tres pantallas:
// inicio con historial, preguntas y resultado.
@Composable
fun PruebaGenerica(config: ConfiguracionPrueba) {
    // El contexto permite acceder al almacenamiento de esta app.
    val context = LocalContext.current

    // Cada asignatura usa un archivo DISTINTO de SharedPreferences.
    // Se mantienen las mismas claves de la versión anterior para
    // NO borrar el historial de notas ni los intentos ya utilizados.
    val preferencias = remember(context) {
        context.getSharedPreferences(config.archivo, Context.MODE_PRIVATE)
    }

    // COMPATIBILIDAD CON VERSIONES ANTERIORES (solo Lenguaje):
    // Una versión antigua guardaba los intentos en otro archivo
    // (config.archivoAntiguo). Si todavía existen datos ahí y
    // NO tenemos datos en el almacenamiento actual, los copiamos.
    // Así no se pierden los intentos ni las notas ya conseguidas.
    remember(preferencias, config.archivoAntiguo) {
        val nombreAntiguo = config.archivoAntiguo
        if (nombreAntiguo != null) {
            val preferenciasAntiguas =
                context.getSharedPreferences(nombreAntiguo, Context.MODE_PRIVATE)
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
        }
        true
    }

    // Máximo permitido, por defecto tres. El docente lo cambia
    // al asignar una unidad (ConfigPruebas.guardarMaxIntentos).
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

    // El banco de preguntas llega desde cada prueba.
    val preguntas = config.preguntas

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

    // NUEVO (tarea 4): ¿el docente permite revisar las respuestas?
    // Lo decide el Checkbox "Permitir ver respuestas" al asignar la unidad.
    // Se lee una vez cada vez que se abre la prueba. Si el docente
    // nunca lo configuró, ConfigPruebas responde true (sí se permite).
    val puedeVerRespuestas = remember(context) {
        ConfigPruebas.verRespuestasPermitido(context, config.asignatura)
    }

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

    // NUEVO (colores): la paleta ahora llega desde cada prueba.
    // Se dejaron los mismos nombres de antes (azul, azulPastel...)
    // para no cambiar el resto del archivo: ahora "azul" significa
    // "el color principal de ESTA asignatura".
    val azulOscuro = config.colores.oscuro
    val azul = config.colores.principal
    val azulPastel = config.colores.fondo
    val textoSecundario = config.colores.secundario
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

                // Tarjeta de color: título a la izquierda, intentos a la derecha.
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
                                    text = config.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = azulOscuro
                                )
                                Text(
                                    text = config.subtitulo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textoSecundario
                                )
                                Text(
                                    text = "${preguntas.size} preguntas · ${preguntas.size} puntos",
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

                        HorizontalDivider(color = config.colores.divisor)

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
                    text = config.titulo,
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
                                            if (indice <= preguntaActual) azul else config.colores.barraVacia,
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
                                config.colores.seleccion
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
                        HorizontalDivider(color = config.colores.divisor)
                        Text("Intentos restantes: $intentosRestantes", color = azulOscuro)
                        if (intentosRestantes == 0) {
                            Text(
                                "Ya no puedes volver a rendir esta prueba.",
                                color = Color(0xFFB3261E)
                            )
                        }
                    }
                }

                // NUEVO (tarea 4): el botón solo aparece si el docente lo permite.
                // Si no, mostramos un aviso para que el niño sepa por qué no está.
                if (puedeVerRespuestas) {
                    // Revisión con retroalimentación educativa, opcional.
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { mostrarRevision = !mostrarRevision }
                    ) {
                        Text(if (mostrarRevision) "Ocultar respuestas" else "Revisar respuestas")
                    }
                } else {
                    Text(
                        text = "Tu profesor no activó la revisión de respuestas para esta prueba.",
                        style = MaterialTheme.typography.bodySmall,
                        color = textoSecundario
                    )
                }

                // NUEVO (tarea 4): doble seguro, la revisión tampoco se dibuja
                // si no está permitida.
                if (mostrarRevision && puedeVerRespuestas) {
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