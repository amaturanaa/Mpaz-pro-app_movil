package com.example.mpaz_pro_app_movil.pruebas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.pruebas.utiles.ColoresPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.ConfiguracionPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PaletasPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PreguntaPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PruebaGenerica
import com.example.mpaz_pro_app_movil.pruebas.utiles.ResultadoIntento
import com.example.mpaz_pro_app_movil.red.PruebaRemota
import com.example.mpaz_pro_app_movil.red.RepositorioEvaluaciones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// PRUEBA DE UNA ASIGNACIÓN
// Carga la prueba desde el backend (preguntas sin respuesta correcta, intentos y
// mejor nota) y la entrega a PruebaGenerica. Nada se guarda en el teléfono.
@Composable
fun PruebaDeUnidad(unidad: Unidad, asignacionId: Int) {
    val repo = remember { RepositorioEvaluaciones() }
    var prueba by remember(asignacionId) { mutableStateOf<PruebaRemota?>(null) }
    var errorCarga by remember(asignacionId) { mutableStateOf<String?>(null) }
    var reintento by remember { mutableIntStateOf(0) }

    LaunchedEffect(asignacionId, reintento) {
        errorCarga = null
        try {
            prueba = withContext(Dispatchers.IO) {
                repo.pruebaDeAsignacion(asignacionId.toLong())
            }
        } catch (error: Exception) {
            errorCarga = "No se pudo cargar la prueba. Revisa la conexión."
        }
    }

    val cargada = prueba
    when {
        errorCarga != null -> Column(modifier = Modifier.padding(16.dp)) {
            Text(errorCarga.orEmpty())
            OutlinedButton(onClick = { reintento++ }) { Text("Reintentar") }
        }

        cargada == null -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))

        cargada.preguntas.isEmpty() -> Text(
            "Esta prueba todavía no tiene preguntas.",
            modifier = Modifier.padding(16.dp)
        )

        else -> {
            val configuracion = remember(cargada) {
                ConfiguracionPrueba(
                    titulo = "Prueba: ${unidad.titulo}",
                    subtitulo = "${unidad.titulo} · ${unidad.asignatura}",
                    asignatura = unidad.asignatura,
                    colores = paletaDeAsignatura(unidad.asignatura),
                    preguntas = cargada.preguntas.map { pregunta ->
                        PreguntaPrueba(
                            preguntaId = pregunta.id,
                            texto = pregunta.enunciado,
                            alternativas = pregunta.alternativas.map { it.texto },
                            alternativaIds = pregunta.alternativas.map { it.id }
                        )
                    },
                    maxIntentos = cargada.maxIntentos,
                    intentosUsados = cargada.intentosUsados,
                    mejorNota = cargada.mejorNota,
                    permiteRevisar = cargada.permiteRevisar,
                    alEnviar = { respuestas ->
                        val intento = repo.rendirIntento(cargada.asignacionId, respuestas)
                        ResultadoIntento(
                            intentoId = intento.id ?: 0L,
                            nota = intento.nota ?: 0.0
                        )
                    },
                    alRevisar = { intentoId ->
                        val revision = repo.revisarIntento(intentoId)
                        revision.respuestas.orEmpty().associate {
                            (it.preguntaId ?: 0L) to (it.correcta == true)
                        }
                    }
                )
            }
            PruebaGenerica(config = configuracion)
        }
    }
}

// Color de cada asignatura (el mismo de sus tarjetas en "Mis asignaturas").
fun paletaDeAsignatura(asignatura: String): ColoresPrueba = when {
    asignatura.startsWith("Lenguaje") -> PaletasPrueba.lenguaje
    asignatura.startsWith("Matem") -> PaletasPrueba.matematicas
    asignatura.startsWith("Ciencias") -> PaletasPrueba.ciencias
    else -> PaletasPrueba.historia
}