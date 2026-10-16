package com.example.mpaz_pro_app_movil.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.mpaz_pro_app_movil.datos.Calificacion
import com.example.mpaz_pro_app_movil.datos.DatosPrueba
import com.example.mpaz_pro_app_movil.datos.Observacion
import com.example.mpaz_pro_app_movil.datos.Unidad
import com.example.mpaz_pro_app_movil.ui.theme.AzulPrincipal
import com.example.mpaz_pro_app_movil.ui.theme.FondoTarjetas
import com.example.mpaz_pro_app_movil.ui.theme.TextoPrincipal
import com.example.mpaz_pro_app_movil.ui.theme.TextoSecundario
import com.example.mpaz_pro_app_movil.ui.theme.VerdePrincipal
import com.example.mpaz_pro_app_movil.validacion.Fechas
import com.example.mpaz_pro_app_movil.validacion.Validador
import com.example.mpaz_pro_app_movil.viewmodel.InformeUnidad

// =============================================
// INFORMES DEL DOCENTE
// =============================================
// informes             : un informe por cada unidad ya asignada
// observaciones        : comentarios que el docente ya dejó
// unidadPorId          : para mostrar el nombre de la unidad de cada observación
// onAgregarObservacion : (estudianteId, unidadId, texto) -> null si salió bien,
//                        o el motivo del error
// onAvisar             : muestra un aviso corto abajo en la pantalla
//
// Informe protegido: solo existen informes de unidades asignadas, y de los
// estudiantes solo se muestra el código, nunca el nombre.
@Composable
fun InformeDocente(
    informes: List<InformeUnidad>,
    observaciones: List<Observacion>,
    unidadPorId: (Int) -> Unidad?,
    onAgregarObservacion: (Int, Int, String) -> String?,
    onAvisar: (String) -> Unit
) {
    if (informes.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FondoTarjetas)
        ) {
            Text(
                text = "Todavía no hay informes. Asigna una unidad a un curso " +
                        "y aquí verás cómo le va a los estudiantes.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundario,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    Text(
        text = "Resultados por unidad",
        style = MaterialTheme.typography.titleMedium,
        color = TextoPrincipal
    )

    informes.forEach { informe ->
        TarjetaInforme(informe)
    }

    Text(
        text = "Los resultados vienen de la prueba de cada asignatura, así que se " +
                "comparten entre todas las unidades de esa asignatura. " +
                "Corresponden al dispositivo del estudiante de prueba.",
        style = MaterialTheme.typography.bodySmall,
        color = TextoSecundario
    )

    // ---------- Observaciones ----------
    FormularioObservacion(
        unidades = informes.map { it.unidad },
        onGuardar = onAgregarObservacion,
        onAvisar = onAvisar
    )

    Text(
        text = "Observaciones guardadas (${observaciones.size})",
        style = MaterialTheme.typography.titleMedium,
        color = TextoPrincipal
    )

    if (observaciones.isEmpty()) {
        Text("Todavía no hay observaciones.", color = TextoSecundario)
    }

    // La más reciente primero.
    observaciones.reversed().forEach { observacion ->
        val unidad = unidadPorId(observacion.unidadId)
        val estudiante = DatosPrueba.estudiantes.firstOrNull { it.id == observacion.estudianteId }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FondoTarjetas)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${estudiante?.codigo ?: "Estudiante"} · " +
                            "${unidad?.titulo ?: "Unidad"} · " +
                            Fechas.aVisible(observacion.fecha),
                    style = MaterialTheme.typography.labelLarge,
                    color = AzulPrincipal
                )
                Text(
                    text = observacion.texto,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoPrincipal
                )
            }
        }
    }
}

// Tarjeta con los resultados de una unidad: intentos, notas y promedio.
@Composable
private fun TarjetaInforme(informe: InformeUnidad) {
    val promedio = Calificacion.promedio(informe.notas)
    val mejor = informe.notas.maxOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjetas)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = informe.unidad.asignatura,
                style = MaterialTheme.typography.labelLarge,
                color = AzulPrincipal
            )
            Text(
                text = informe.unidad.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )
            Text(
                text = "Cursos: ${informe.asignaciones.joinToString { it.curso }}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
            Text(
                text = "Intentos usados en la prueba de ${informe.unidad.asignatura}: " +
                        "${informe.intentosUsados} de ${informe.maxIntentos}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoPrincipal
            )

            if (informe.notas.isEmpty()) {
                Text(
                    text = "Aún no hay notas registradas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            } else {
                informe.notas.forEachIndexed { posicion, nota ->
                    Text(
                        text = "Intento ${posicion + 1}: ${Calificacion.formatear(nota)}",
                        style = MaterialTheme.typography.bodyMedium,
                        // Verde si aprobó (4,0 o más), rojo si no.
                        color = if (nota >= 4.0) VerdePrincipal else MaterialTheme.colorScheme.error
                    )
                }
                if (promedio != null && mejor != null) {
                    Text(
                        text = "Promedio: ${Calificacion.formatear(promedio)} · " +
                                "Mejor nota: ${Calificacion.formatear(mejor)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextoPrincipal
                    )
                }
            }
        }
    }
}

// Formulario para dejar una observación sobre un estudiante en una unidad.
@Composable
private fun FormularioObservacion(
    unidades: List<Unidad>,
    onGuardar: (Int, Int, String) -> String?,
    onAvisar: (String) -> Unit
) {
    var unidadId by remember { mutableStateOf<Int?>(null) }
    var estudianteId by remember { mutableStateOf<Int?>(null) }
    var texto by remember { mutableStateOf("") }

    // Los errores solo se muestran después de tocar el botón la primera vez.
    var mostrarErrores by remember { mutableStateOf(false) }
    var errorAlGuardar by remember { mutableStateOf<String?>(null) }

    // Cada error sale del Validador central. null significa "está bien".
    val errorUnidad = Validador.opcionElegida(unidadId?.toString(), "Elige una unidad.")
    val errorEstudiante = Validador.opcionElegida(estudianteId?.toString(), "Elige un estudiante.")
    val errorTexto = Validador.longitud(texto, "El texto de la observación", 10, 200)

    val todoCorrecto = errorUnidad == null && errorEstudiante == null && errorTexto == null

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(
            text = "Nueva observación",
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipal
        )

        Text(text = "Unidad", style = MaterialTheme.typography.labelLarge)
        unidades.forEach { unidad ->
            FilterChip(
                modifier = Modifier.fillMaxWidth(),
                selected = unidadId == unidad.id,
                onClick = { unidadId = unidad.id },
                label = { Text(unidad.titulo) }
            )
        }
        MensajeError(if (mostrarErrores) errorUnidad else null)

        // De cada estudiante solo se muestra el código y el curso.
        Text(text = "Estudiante", style = MaterialTheme.typography.labelLarge)
        DatosPrueba.estudiantes.forEach { estudiante ->
            FilterChip(
                modifier = Modifier.fillMaxWidth(),
                selected = estudianteId == estudiante.id,
                onClick = { estudianteId = estudiante.id },
                label = { Text("${estudiante.codigo} · ${estudiante.curso}") }
            )
        }
        MensajeError(if (mostrarErrores) errorEstudiante else null)

        CampoValidado(
            valor = texto,
            alCambiar = { texto = it },
            etiqueta = "Observación",
            icono = Icons.Filled.Edit,
            error = if (mostrarErrores) errorTexto else null,
            correcto = errorTexto == null,
            ayuda = "Entre 10 y 200 caracteres.",
            unaLinea = false,
            lineasMinimas = 3
        )

        // Si el ViewModel rechaza guardar, aquí se explica por qué.
        MensajeError(errorAlGuardar)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                mostrarErrores = true

                val unidadElegida = unidadId
                val estudianteElegido = estudianteId
                if (todoCorrecto && unidadElegida != null && estudianteElegido != null) {
                    val error = onGuardar(estudianteElegido, unidadElegida, texto)
                    errorAlGuardar = error

                    // Si salió bien, el formulario queda limpio.
                    if (error == null) {
                        unidadId = null
                        estudianteId = null
                        texto = ""
                        mostrarErrores = false
                        onAvisar("Observación guardada.")
                    }
                }
            }
        ) {
            Text("Guardar observación")
        }
    }
}