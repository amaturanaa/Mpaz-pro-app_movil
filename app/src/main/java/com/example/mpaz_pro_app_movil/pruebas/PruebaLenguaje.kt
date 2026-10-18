package com.example.mpaz_pro_app_movil.pruebas

// ============================================================
// PRUEBA DE LENGUAJE
// ============================================================
// NUEVO (tarea 5): antes este archivo tenía ~570 líneas con toda la
// lógica de la prueba. Ahora solo tiene LO PROPIO de Lenguaje:
// sus textos y sus preguntas. Todo lo demás (intentos, nota,
// pantallas, revisión de respuestas) está en utiles/PruebaGenerica.kt,
// que comparten las cuatro pruebas.
import androidx.compose.runtime.Composable
import com.example.mpaz_pro_app_movil.pruebas.utiles.ConfiguracionPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PaletasPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PreguntaPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PruebaGenerica

// EstudianteScreen sigue llamando PruebaLenguaje() igual que antes.
@Composable
fun PruebaLenguaje() {
    PruebaGenerica(config = configuracionLenguaje)
}

// Datos de esta prueba. Se crean una sola vez (fuera de la pantalla).
private val configuracionLenguaje = ConfiguracionPrueba(
    titulo = "Prueba de Lenguaje",
    subtitulo = "Gerundios · Lenguaje y Comunicación",
    // Nombre completo: con él ConfigPruebas sabe si el docente
    // permite revisar respuestas en esta asignatura.
    asignatura = "Lenguaje y Comunicación",
    // Mismo archivo de antes: así NO se pierden intentos ni notas.
    archivo = "prueba_lenguaje",
    // NUEVO (colores): versión suave del color de la asignatura.
    colores = PaletasPrueba.lenguaje,
    // BANCO DE PREGUNTAS: contenidos de segundo básico (Chile).
    preguntas = listOf(
        // Pregunta 1: reconocer el gerundio de «cantar».
        PreguntaPrueba(
            texto = "¿Cuál es el gerundio de cantar?",
            alternativas = listOf("Cantar", "Cantando", "Cantó", "Canta"),
            correcta = 1,
            explicacion = "El gerundio de cantar es cantando. Termina en -ando."
        ),
        // Pregunta 2: completar una oración con gerundio.
        PreguntaPrueba(
            texto = "La niña está ___ un libro.",
            alternativas = listOf("Leyendo", "Leer", "Leyó", "Lee"),
            correcta = 0,
            explicacion = "Decimos «está leyendo». Leyendo expresa una acción en desarrollo."
        ),
        // Pregunta 3: reconocer el gerundio de «correr».
        PreguntaPrueba(
            texto = "¿Cuál es el gerundio de correr?",
            alternativas = listOf("Corría", "Corrió", "Corriendo", "Corre"),
            correcta = 2,
            explicacion = "El gerundio de correr es corriendo. Termina en -iendo."
        ),
        // Pregunta 4: completar una oración con gerundio.
        PreguntaPrueba(
            texto = "Los niños están ___ en el patio.",
            alternativas = listOf("Jugar", "Jugaron", "Juegan", "Jugando"),
            correcta = 3,
            explicacion = "La oración correcta es «Los niños están jugando en el patio»."
        ),
        // Pregunta 5: reconocer el gerundio de «escribir».
        PreguntaPrueba(
            texto = "¿Cuál es el gerundio de escribir?",
            alternativas = listOf("Escribiendo", "Escribe", "Escribió", "Escribir"),
            correcta = 0,
            explicacion = "El gerundio de escribir es escribiendo. Termina en -iendo."
        )
    ),
    // Una versión vieja de la app guardaba aquí los intentos de Lenguaje.
    // PruebaGenerica los copia para no perder notas antiguas.
    archivoAntiguo = "prueba_lenguaje_gerundios"
)