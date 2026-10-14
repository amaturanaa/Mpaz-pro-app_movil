package com.example.mpaz_pro_app_movil.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Botón independiente de texto a voz.
 * No cambia UnidadScreen ni las actividades de cada asignatura.
 */
@Composable
fun BotonNarracion(asignatura: String) {
    val contexto = LocalContext.current
    var lector by remember(contexto) { mutableStateOf<TextToSpeech?>(null) }
    var vozLista by remember(contexto) { mutableStateOf(false) }
    var aviso by remember(contexto) { mutableStateOf("") }

    // Se inicia al abrir la pantalla y se libera al salir.
    DisposableEffect(contexto) {
        val motor = TextToSpeech(contexto.applicationContext) { resultado ->
            vozLista = resultado == TextToSpeech.SUCCESS
            if (resultado != TextToSpeech.SUCCESS) {
                aviso = "No se pudo iniciar el lector de voz del teléfono."
            }
        }
        lector = motor

        onDispose {
            motor.stop()
            motor.shutdown()
        }
    }

    OutlinedButton(
        onClick = {
            val motor = lector
            if (motor != null && vozLista) {
                // Elegimos español. Si Chile no está disponible, probamos español general.
                var idioma = motor.setLanguage(Locale.forLanguageTag("es-CL"))
                if (idioma < TextToSpeech.LANG_AVAILABLE) {
                    idioma = motor.setLanguage(Locale.forLanguageTag("es"))
                }
                if (idioma < TextToSpeech.LANG_AVAILABLE) {
                    aviso = "Instala o activa una voz en español en los ajustes del teléfono."
                } else {
                    aviso = ""
                    val texto = "Explicación. ${explicacionDe(asignatura)}. " +
                            "Ejemplo. ${ejemploDe(asignatura)}"
                    motor.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "lectura_materia")
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = vozLista
    ) {
        Text("Escuchar")
    }

    if (aviso.isNotEmpty()) {
        Text(aviso)
    }
}
