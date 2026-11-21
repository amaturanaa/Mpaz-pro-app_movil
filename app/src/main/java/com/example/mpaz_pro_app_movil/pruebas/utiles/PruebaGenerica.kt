package com.example.mpaz_pro_app_movil.pruebas.utiles

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.screens.estudiante.iconoDeAsignatura
import kotlinx.coroutines.launch
import java.util.Locale


// PREGUNTA TAL COMO LA VE EL ESTUDIANTE (sin respuesta correcta).
// preguntaId     -> id de la pregunta en el backend
// alternativas   -> textos que se muestran
// alternativaIds -> ids de cada alternativa, en el mismo orden que los textos
data class PreguntaPrueba(
    val preguntaId: Long,
    val texto: String,
    val alternativas: List<String>,
    val alternativaIds: List<Long>
)

// Lo que devuelve el servidor al enviar un intento.
data class ResultadoIntento(val intentoId: Long, val nota: Double)


// PALETA DE COLORES DE UNA PRUEBA
data class ColoresPrueba(
    val fondo: Color,
    val principal: Color,
    val oscuro: Color,
    val secundario: Color,
    val seleccion: Color,
    val divisor: Color,
    val barraVacia: Color
)

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


// CONFIGURACIÓN DE UNA PRUEBA
// Todo lo que la pantalla necesita viene del backend (a través de PruebaDeUnidad).
// maxIntentos / intentosUsados / mejorNota -> datos de la asignación y del avance
// permiteRevisar                            -> lo decidió el docente al asignar
// alEnviar  -> envía las respuestas (preguntaId -> alternativaId) y devuelve el resultado
// alRevisar -> devuelve, para cada preguntaId, si la respuesta del alumno fue correcta
data class ConfiguracionPrueba(
    val titulo: String,
    val subtitulo: String,
    val asignatura: String,
    val preguntas: List<PreguntaPrueba>,
    val colores: ColoresPrueba = PaletasPrueba.matematicas,
    val maxIntentos: Int,
    val intentosUsados: Int,
    val mejorNota: Double?,
    val permiteRevisar: Boolean,
    val alEnviar: suspend (Map<Long, Long>) -> ResultadoIntento,
    val alRevisar: suspend (Long) -> Map<Long, Boolean>
)


// PANTALLA COMPARTIDA DE LAS PRUEBAS
// Dibuja tres pantallas: inicio, preguntas y resultado.
// La nota y el número de intentos los calcula y guarda el backend.
@Composable
fun PruebaGenerica(config: ConfiguracionPrueba) {
    val scope = rememberCoroutineScope()
    val preguntas = config.preguntas

    var intentosUsados by remember(config) { mutableIntStateOf(config.intentosUsados) }
    var mejorNota by remember(config) { mutableStateOf(config.mejorNota) }
    val intentosRestantes = (config.maxIntentos - intentosUsados).coerceAtLeast(0)

    // 0 = inicio, 1 = cuestionario, 2 = resultado.
    var pantalla by remember { mutableIntStateOf(0) }
    var preguntaActual by remember { mutableIntStateOf(0) }

    // Respuestas elegidas: índice de alternativa por pregunta, null si no respondió.
    val respuestas = remember(config) {
        mutableStateListOf<Int?>().apply { repeat(preguntas.size) { add(null) } }
    }

    var enviando by remember { mutableStateOf(false) }
    var errorEnvio by remember { mutableStateOf<String?>(null) }
    var resultado by remember { mutableStateOf<ResultadoIntento?>(null) }

    // preguntaId -> acertó. null mientras no se ha pedido la revisión.
    var aciertos by remember { mutableStateOf<Map<Long, Boolean>?>(null) }
    var mostrarRevision by remember { mutableStateOf(false) }
    var revisando by remember { mutableStateOf(false) }
    var errorRevision by remember { mutableStateOf<String?>(null) }

    fun notaChilena(valor: Double): String = String.format(
        Locale.forLanguageTag("es-CL"), "%.1f", valor
    )

    val azulOscuro = config.colores.oscuro
    val azul = config.colores.principal
    val azulPastel = config.colores.fondo
    val textoSecundario = config.colores.secundario
    val verdeSuave = Color(0xFFD5F3E7)
    val rojoSuave = Color(0xFFFFDFDF)

    fun enviar() {
        if (enviando || intentosRestantes <= 0) return
        if (respuestas.any { it == null }) {
            errorEnvio = "Responde todas las preguntas antes de finalizar."
            return
        }
        val envio = preguntas.indices.associate { indice ->
            preguntas[indice].preguntaId to preguntas[indice].alternativaIds[respuestas[indice]!!]
        }

        enviando = true
        errorEnvio = null
        scope.launch {
            try {
                val recibido = config.alEnviar(envio)
                resultado = recibido
                intentosUsados++
                mejorNota = maxOf(mejorNota ?: recibido.nota, recibido.nota)
                aciertos = null
                mostrarRevision = false
                pantalla = 2
            } catch (error: Exception) {
                errorEnvio = "No se pudo enviar la prueba. Revisa la conexión e inténtalo de nuevo."
            } finally {
                enviando = false
            }
        }
    }

    fun alternarRevision() {
        if (mostrarRevision) {
            mostrarRevision = false
            return
        }
        if (aciertos != null) {
            mostrarRevision = true
            return
        }
        val intento = resultado ?: return
        revisando = true
        errorRevision = null
        scope.launch {
            try {
                aciertos = config.alRevisar(intento.intentoId)
                mostrarRevision = true
            } catch (error: Exception) {
                errorRevision = "No se pudo cargar la revisión. Inténtalo de nuevo."
            } finally {
                revisando = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (pantalla) {

            // PANTALLA 0: INICIO
            0 -> {
                Text(
                    text = "✏️ Mis pruebas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

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
                                    text = "📝 ${config.titulo}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = azulOscuro
                                )
                                Text(
                                    text = "${iconoDeAsignatura(config.asignatura)} ${config.subtitulo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textoSecundario
                                )
                                Text(
                                    text = "❓ ${preguntas.size} preguntas · ⭐ ${preguntas.size} puntos",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textoSecundario
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .background(
                                        if (intentosRestantes > 0) Color(0xFFD5F3E7) else Color(0xFFFFDFDF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$intentosRestantes de ${config.maxIntentos}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (intentosRestantes > 0)
                                        Color(0xFF126B4B) else Color(0xFFB3261E)
                                )
                                Text(
                                    text = "🎯 Disponibles",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textoSecundario
                                )
                            }
                        }

                        HorizontalDivider(color = config.colores.divisor)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✔️ Utilizados: $intentosUsados",
                                color = textoSecundario,
                                style = MaterialTheme.typography.bodySmall
                            )

                            Button(
                                enabled = intentosRestantes > 0 && preguntas.isNotEmpty(),
                                onClick = {
                                    preguntaActual = 0
                                    respuestas.indices.forEach { respuestas[it] = null }
                                    resultado = null
                                    aciertos = null
                                    mostrarRevision = false
                                    errorEnvio = null
                                    pantalla = 1
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = azul)
                            ) {
                                Text(if (intentosRestantes > 0) "🚀 Comenzar →" else "🔒 Bloqueada")
                            }
                        }
                    }
                }

                mejorNota?.let { mejor ->
                    Text(
                        text = "🏅 Mejor nota: ${notaChilena(mejor)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = azul
                    )
                }
            }

            // PANTALLA 1: PREGUNTAS
            1 -> {
                Text(
                    text = "📝 ${config.titulo}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "🔁 Intento ${intentosUsados + 1} de ${config.maxIntentos}",
                    color = textoSecundario
                )

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
                            Text("🏃 Tu progreso", color = azulOscuro, fontWeight = FontWeight.SemiBold)
                            Text("${preguntaActual + 1} de ${preguntas.size}", color = azulOscuro)
                        }

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
                            "🤔 PREGUNTA ${preguntaActual + 1}",
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
                    "👉 Selecciona una alternativa:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textoSecundario
                )

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

                errorEnvio?.let {
                    Text(it, color = Color(0xFFB3261E), style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        enabled = preguntaActual > 0 && !enviando,
                        onClick = { preguntaActual-- }
                    ) { Text("⬅️ Anterior") }

                    Button(
                        enabled = respuestas[preguntaActual] != null && !enviando,
                        onClick = {
                            if (preguntaActual < preguntas.lastIndex) {
                                preguntaActual++
                            } else {
                                enviar()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = azul)
                    ) {
                        if (enviando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(if (preguntaActual == preguntas.lastIndex) "🏁 Finalizar" else "Siguiente ➡️")
                        }
                    }
                }
            }

            // PANTALLA 2: RESULTADO Y REVISIÓN
            2 -> {
                val recibido = resultado

                Text(
                    text = "🎉 ¡Prueba terminada!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (recibido != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = azulPastel)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("🏆 Tu resultado", color = azulOscuro, fontWeight = FontWeight.SemiBold)
                            Text(
                                notaChilena(recibido.nota),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = azul
                            )
                            Text(
                                if (recibido.nota >= 4.0) "🌟 ¡Buen trabajo!" else "💪 ¡Sigue practicando!",
                                color = textoSecundario
                            )
                            HorizontalDivider(color = config.colores.divisor)
                            Text("🔁 Intentos restantes: $intentosRestantes", color = azulOscuro)
                            if (intentosRestantes == 0) {
                                Text(
                                    "🔒 Ya no puedes volver a rendir esta prueba.",
                                    color = Color(0xFFB3261E)
                                )
                            }
                        }
                    }
                } else {
                    Text("No hay resultado para mostrar.", color = textoSecundario)
                }

                if (config.permiteRevisar) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !revisando && resultado != null,
                        onClick = { alternarRevision() }
                    ) {
                        Text(
                            when {
                                revisando -> "⏳ Cargando revisión..."
                                mostrarRevision -> "🙈 Ocultar respuestas"
                                else -> "👀 Revisar respuestas"
                            }
                        )
                    }
                    errorRevision?.let {
                        Text(it, color = Color(0xFFB3261E), style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Text(
                        text = "ℹ️ Tu profesor no activó la revisión de respuestas para esta prueba.",
                        style = MaterialTheme.typography.bodySmall,
                        color = textoSecundario
                    )
                }

                if (mostrarRevision && config.permiteRevisar) {
                    val marcas = aciertos.orEmpty()
                    preguntas.forEachIndexed { indice, pregunta ->
                        val acerto = marcas[pregunta.preguntaId] == true
                        val elegida = respuestas[indice]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (acerto) verdeSuave else rojoSuave
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    "${if (acerto) "✅" else "❌"} Pregunta ${indice + 1}: ${pregunta.texto}",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "✍️ Tu respuesta: ${elegida?.let { pregunta.alternativas[it] } ?: "Sin responder"}"
                                )
                                Text(if (acerto) "👏 Respondiste bien" else "😕 Respondiste mal")
                            }
                        }
                    }
                }

                Button(
                    onClick = { pantalla = 0 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = azul)
                ) {
                    Text("🏠 Volver a mis pruebas")
                }
            }
        }
    }
}