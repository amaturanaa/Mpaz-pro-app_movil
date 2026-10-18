package com.example.mpaz_pro_app_movil.pruebas

// ============================================================
// PRUEBA DE HISTORIA
// ============================================================
// NUEVO (tarea 5): antes este archivo tenía ~570 líneas con toda la
// lógica de la prueba. Ahora solo tiene LO PROPIO de Historia:
// sus textos y sus preguntas. Todo lo demás (intentos, nota,
// pantallas, revisión de respuestas) está en utiles/PruebaGenerica.kt,
// que comparten las cuatro pruebas.
import androidx.compose.runtime.Composable
import com.example.mpaz_pro_app_movil.pruebas.utiles.ConfiguracionPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PaletasPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PreguntaPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PruebaGenerica

// EstudianteScreen sigue llamando PruebaHistoria() igual que antes.
@Composable
fun PruebaHistoria() {
    PruebaGenerica(config = configuracionHistoria)
}

// Datos de esta prueba. Se crean una sola vez (fuera de la pantalla).
private val configuracionHistoria = ConfiguracionPrueba(
    titulo = "Prueba de Historia",
    subtitulo = "Pueblos originarios, orientación y Chile",
    // Nombre completo: con él ConfigPruebas sabe si el docente
    // permite revisar respuestas en esta asignatura.
    asignatura = "Historia, Geografía y Ciencias Sociales",
    // Mismo archivo de antes: así NO se pierden intentos ni notas.
    archivo = "prueba_historia",
    // NUEVO (colores): versión suave del color de la asignatura.
    colores = PaletasPrueba.historia,
    // BANCO DE PREGUNTAS: contenidos de segundo básico (Chile).
    preguntas = listOf(
        // Pregunta 1: Historia.
        PreguntaPrueba(
            texto = "¿Qué pueblo originario está presente en el centro y sur de Chile?",
            alternativas = listOf("Mapuche", "Egipcio", "Romano", "Vikingo"),
            correcta = 0,
            explicacion = "El pueblo mapuche es originario del territorio que hoy incluye el centro y sur de Chile."
        ),
        // Pregunta 2: Historia.
        PreguntaPrueba(
            texto = "¿Por cuál punto cardinal aparece el Sol por la mañana?",
            alternativas = listOf("Norte", "Sur", "Este", "Oeste"),
            correcta = 2,
            explicacion = "De manera general, el Sol sale por el este."
        ),
        // Pregunta 3: Historia.
        PreguntaPrueba(
            texto = "¿Cuál de estos elementos es parte del paisaje natural?",
            alternativas = listOf("Un edificio", "Un río", "Una carretera", "Un semáforo"),
            correcta = 1,
            explicacion = "Un río es un elemento natural del paisaje."
        ),
        // Pregunta 4: Historia.
        PreguntaPrueba(
            texto = "¿Cuál de estas danzas es tradicional de Chile?",
            alternativas = listOf("Cueca", "Flamenco", "Tango", "Vals vienés"),
            correcta = 0,
            explicacion = "La cueca es una danza tradicional de Chile."
        ),
        // Pregunta 5: Historia.
        PreguntaPrueba(
            texto = "¿En qué zona de Chile se encuentra el desierto de Atacama?",
            alternativas = listOf("Zona norte", "Zona austral", "Zona sur", "En el océano"),
            correcta = 0,
            explicacion = "El desierto de Atacama se encuentra en el norte de Chile."
        )
    )
)