package com.example.mpaz_pro_app_movil.pruebas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.mpaz_pro_app_movil.datos.ConfigPruebas
import com.example.mpaz_pro_app_movil.datos.model.Pregunta
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.pruebas.utiles.ColoresPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.ConfiguracionPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PaletasPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PreguntaPrueba
import com.example.mpaz_pro_app_movil.pruebas.utiles.PruebaGenerica

// PRUEBA DE UNA UNIDAD
// Toma las preguntas que la UTP escribió para la unidad y las convierte
// en la configuración que entiende PruebaGenerica (intentos, nota,
// resultado y revisión). Así cualquier unidad nueva que el docente asigne
// ya trae su prueba, sin escribir código nuevo.
@Composable
fun PruebaDeUnidad(unidad: Unidad, preguntas: List<Pregunta>) {
    val configuracion = remember(unidad, preguntas) {
        ConfiguracionPrueba(
            titulo = "Prueba: ${unidad.titulo}",
            subtitulo = "${unidad.titulo} · ${unidad.asignatura}",
            asignatura = unidad.asignatura,
            // Un archivo por unidad: los intentos y las notas no se mezclan
            // con los de otras unidades de la misma asignatura.
            archivo = ConfigPruebas.archivoDeUnidad(unidad.id),
            colores = paletaDeAsignatura(unidad.asignatura),
            preguntas = preguntas.map { pregunta ->
                PreguntaPrueba(
                    texto = pregunta.texto,
                    alternativas = pregunta.alternativas,
                    correcta = pregunta.correcta,
                    // La UTP no escribe explicación: se muestra la respuesta correcta.
                    explicacion = "La respuesta correcta es «" +
                            pregunta.alternativas.getOrElse(pregunta.correcta) { "" } + "»."
                )
            }
        )
    }

    PruebaGenerica(config = configuracion)
}

// Color de cada asignatura (el mismo de sus tarjetas en "Mis asignaturas").
fun paletaDeAsignatura(asignatura: String): ColoresPrueba = when {
    asignatura.startsWith("Lenguaje") -> PaletasPrueba.lenguaje
    asignatura.startsWith("Matem") -> PaletasPrueba.matematicas
    asignatura.startsWith("Ciencias") -> PaletasPrueba.ciencias
    else -> PaletasPrueba.historia
}