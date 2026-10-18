package com.example.mpaz_pro_app_movil.pruebas

// ============================================================
// PRUEBA DE MATEMÁTICA
// ============================================================
// NUEVO (tarea 5): antes este archivo tenía ~570 líneas con toda la
// lógica de la prueba. Ahora solo tiene LO PROPIO de Matemática:
// sus textos y sus preguntas. Todo lo demás (intentos, nota,
// pantallas, revisión de respuestas) está en utiles/PruebaGenerica.kt,
// que comparten las cuatro pruebas.
import androidx.compose.runtime.Composable
import com.example.mpaz_pro_app_movil.pruebas.utiles.ConfiguracionPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PaletasPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PreguntaPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PruebaGenerica

// EstudianteScreen sigue llamando PruebaMatematicas() igual que antes.
@Composable
fun PruebaMatematicas() {
    PruebaGenerica(config = configuracionMatematicas)
}

// Datos de esta prueba. Se crean una sola vez (fuera de la pantalla).
private val configuracionMatematicas = ConfiguracionPrueba(
    titulo = "Prueba de Matemática",
    subtitulo = "Números, operaciones y geometría",
    // Nombre completo: con él ConfigPruebas sabe si el docente
    // permite revisar respuestas en esta asignatura.
    asignatura = "Matemáticas",
    // Mismo archivo de antes: así NO se pierden intentos ni notas.
    archivo = "prueba_matematicas",
    // NUEVO (colores): versión suave del color de la asignatura.
    colores = PaletasPrueba.matematicas,
    // BANCO DE PREGUNTAS: contenidos de segundo básico (Chile).
    preguntas = listOf(
        // Pregunta 1: Matemática.
        PreguntaPrueba(
            texto = "¿Cuánto es 36 + 24?",
            alternativas = listOf("50", "60", "62", "70"),
            correcta = 1,
            explicacion = "36 + 24 = 60."
        ),
        // Pregunta 2: Matemática.
        PreguntaPrueba(
            texto = "¿Cuánto es 85 - 30?",
            alternativas = listOf("55", "65", "45", "50"),
            correcta = 0,
            explicacion = "85 - 30 = 55."
        ),
        // Pregunta 3: Matemática.
        PreguntaPrueba(
            texto = "En el número 74, ¿cuántas decenas hay?",
            alternativas = listOf("4", "7", "74", "70"),
            correcta = 1,
            explicacion = "74 está formado por 7 decenas y 4 unidades."
        ),
        // Pregunta 4: Matemática.
        PreguntaPrueba(
            texto = "¿Qué número sigue en la secuencia 5, 10, 15, 20, ...?",
            alternativas = listOf("21", "25", "30", "40"),
            correcta = 1,
            explicacion = "La secuencia aumenta de 5 en 5."
        ),
        // Pregunta 5: Matemática.
        PreguntaPrueba(
            texto = "¿Cuántos lados tiene un triángulo?",
            alternativas = listOf("2", "3", "4", "5"),
            correcta = 1,
            explicacion = "Un triángulo tiene 3 lados."
        )
    )
)