package com.example.mpaz_pro_app_movil.pruebas

// ============================================================
// PRUEBA DE CIENCIAS NATURALES
// ============================================================
// NUEVO (tarea 5): antes este archivo tenía ~570 líneas con toda la
// lógica de la prueba. Ahora solo tiene LO PROPIO de Ciencias Naturales:
// sus textos y sus preguntas. Todo lo demás (intentos, nota,
// pantallas, revisión de respuestas) está en utiles/PruebaGenerica.kt,
// que comparten las cuatro pruebas.
import androidx.compose.runtime.Composable
import com.example.mpaz_pro_app_movil.pruebas.utiles.ConfiguracionPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PaletasPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PreguntaPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PruebaGenerica

// EstudianteScreen sigue llamando PruebaCiencias() igual que antes.
@Composable
fun PruebaCiencias() {
    PruebaGenerica(config = configuracionCiencias)
}

// Datos de esta prueba. Se crean una sola vez (fuera de la pantalla).
private val configuracionCiencias = ConfiguracionPrueba(
    titulo = "Prueba de Ciencias Naturales",
    subtitulo = "Seres vivos, hábitats y agua",
    // Nombre completo: con él ConfigPruebas sabe si el docente
    // permite revisar respuestas en esta asignatura.
    asignatura = "Ciencias Naturales",
    // Mismo archivo de antes: así NO se pierden intentos ni notas.
    archivo = "prueba_ciencias",
    // NUEVO (colores): versión suave del color de la asignatura.
    colores = PaletasPrueba.ciencias,
    // BANCO DE PREGUNTAS: contenidos de segundo básico (Chile).
    preguntas = listOf(
        // Pregunta 1: Ciencias Naturales.
        PreguntaPrueba(
            texto = "¿Cuál de estos animales es vertebrado?",
            alternativas = listOf("Mariposa", "Caracol", "Perro", "Hormiga"),
            correcta = 2,
            explicacion = "El perro tiene columna vertebral."
        ),
        // Pregunta 2: Ciencias Naturales.
        PreguntaPrueba(
            texto = "¿Dónde vive normalmente un pez?",
            alternativas = listOf("En el agua", "En un árbol", "Bajo la arena", "En un nido"),
            correcta = 0,
            explicacion = "El agua es el hábitat de los peces."
        ),
        // Pregunta 3: Ciencias Naturales.
        PreguntaPrueba(
            texto = "¿Cuál es una etapa en la vida de una mariposa?",
            alternativas = listOf("Piedra", "Oruga", "Semilla", "Raíz"),
            correcta = 1,
            explicacion = "La oruga es una etapa del ciclo de vida de la mariposa."
        ),
        // Pregunta 4: Ciencias Naturales.
        PreguntaPrueba(
            texto = "¿Qué acción ayuda a cuidar el agua?",
            alternativas = listOf("Dejar la llave abierta", "Botar basura al río", "Cerrar la llave al cepillarse", "Jugar con la manguera abierta"),
            correcta = 2,
            explicacion = "Cerrar la llave evita desperdiciar agua."
        ),
        // Pregunta 5: Ciencias Naturales.
        PreguntaPrueba(
            texto = "¿Qué necesitan las plantas para vivir?",
            alternativas = listOf("Solo piedras", "Agua y luz", "Solo plástico", "Solo juguetes"),
            correcta = 1,
            explicacion = "Las plantas necesitan agua y luz para desarrollarse."
        )
    )
)