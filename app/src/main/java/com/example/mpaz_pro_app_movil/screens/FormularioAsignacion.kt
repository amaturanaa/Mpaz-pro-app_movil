package com.example.mpaz_pro_app_movil.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.components.CampoValidado
import com.example.mpaz_pro_app_movil.components.MensajeError
import com.example.mpaz_pro_app_movil.datos.Unidad
import com.example.mpaz_pro_app_movil.validacion.Fechas
import com.example.mpaz_pro_app_movil.validacion.Validador

// =============================================
// FORMULARIO PARA ASIGNAR UNA UNIDAD (perfil Docente)
// =============================================
// unidades  : las unidades publicadas que se pueden asignar
// cursos    : cursos disponibles
// onAsignar : intenta asignar (unidad, curso, inicio, término, intentos);
//             devuelve null si salió bien o el motivo del error
@Composable
fun FormularioAsignacion(
    unidades: List<Unidad>,
    cursos: List<String>,
    onAsignar: (Unidad, String, String, String, Int) -> String?
) {
    if (unidades.isEmpty()) {
        Text(
            text = "No hay unidades publicadas para asignar. " +
                    "La UTP debe publicar una primero.",
            style = MaterialTheme.typography.bodyLarge
        )
        return
    }

    var unidadId by remember { mutableStateOf<Int?>(null) }
    var curso by remember { mutableStateOf<String?>(null) }
    var inicio by remember { mutableStateOf(Fechas.hoyVisible()) }
    var fin by remember { mutableStateOf("") }
    var intentos by remember { mutableStateOf<Int?>(3) }

    // Los errores solo se muestran después de tocar "Asignar" la primera vez.
    var mostrarErrores by remember { mutableStateOf(false) }
    var errorAlGuardar by remember { mutableStateOf<String?>(null) }

    // Cada error sale del Validador central. null significa "está bien".
    val errorUnidad = Validador.opcionElegida(unidadId?.toString(), "Elige una unidad.")
    val errorCurso = Validador.opcionElegida(curso, "Elige un curso.")
    val errorInicio = Validador.fecha(inicio, "La fecha de inicio")
    val errorFin = Validador.fecha(fin, "La fecha de término")
        ?: Validador.rangoFechas(inicio, fin)
    val errorIntentos = Validador.intentos(intentos)

    val todoCorrecto = errorUnidad == null &&
            errorCurso == null &&
            errorInicio == null &&
            errorFin == null &&
            errorIntentos == null

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(
            text = "Asignar una unidad",
            style = MaterialTheme.typography.titleMedium
        )

        // Unidad: solo aparecen las publicadas.
        Text(text = "Unidad", style = MaterialTheme.typography.labelLarge)
        unidades.forEach { unidad ->
            FilterChip(
                modifier = Modifier.fillMaxWidth(),
                selected = unidadId == unidad.id,
                onClick = { unidadId = unidad.id },
                label = { Text("${unidad.titulo} (${unidad.asignatura})") }
            )
        }
        MensajeError(if (mostrarErrores) errorUnidad else null)

        // Curso
        Text(text = "Curso", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            cursos.forEach { opcion ->
                FilterChip(
                    selected = curso == opcion,
                    onClick = { curso = opcion },
                    label = { Text(opcion) }
                )
            }
        }
        MensajeError(if (mostrarErrores) errorCurso else null)

        CampoValidado(
            valor = inicio,
            alCambiar = { inicio = it },
            etiqueta = "Fecha de inicio",
            icono = Icons.Filled.DateRange,
            error = if (mostrarErrores) errorInicio else null,
            correcto = errorInicio == null,
            ayuda = "Formato dd/mm/aaaa."
        )

        CampoValidado(
            valor = fin,
            alCambiar = { fin = it },
            etiqueta = "Fecha de término",
            icono = Icons.Filled.DateRange,
            error = if (mostrarErrores) errorFin else null,
            correcto = errorFin == null,
            ayuda = "Formato dd/mm/aaaa, por ejemplo 15/12/2026."
        )

        // Intentos permitidos para la prueba (de 1 a 3).
        Text(text = "Intentos permitidos", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..3).forEach { cantidad ->
                FilterChip(
                    selected = intentos == cantidad,
                    onClick = { intentos = cantidad },
                    label = { Text(if (cantidad == 1) "1 intento" else "$cantidad intentos") }
                )
            }
        }
        MensajeError(if (mostrarErrores) errorIntentos else null)

        // Si el ViewModel rechaza la asignación, aquí se explica por qué.
        MensajeError(errorAlGuardar)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                mostrarErrores = true

                val unidadElegida = unidades.firstOrNull { it.id == unidadId }
                val cursoElegido = curso
                val intentosElegidos = intentos

                if (todoCorrecto &&
                    unidadElegida != null &&
                    cursoElegido != null &&
                    intentosElegidos != null
                ) {
                    val error = onAsignar(
                        unidadElegida, cursoElegido, inicio, fin, intentosElegidos
                    )
                    errorAlGuardar = error

                    // Si salió bien, el formulario queda limpio para la próxima.
                    if (error == null) {
                        unidadId = null
                        curso = null
                        inicio = Fechas.hoyVisible()
                        fin = ""
                        intentos = 3
                        mostrarErrores = false
                    }
                }
            }
        ) {
            Text("Asignar unidad")
        }
    }
}