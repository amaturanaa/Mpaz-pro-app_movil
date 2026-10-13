package com.example.mpaz_pro_app_movil.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import com.example.mpaz_pro_app_movil.datos.DatosPrueba
import com.example.mpaz_pro_app_movil.datos.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.Unidad
import com.example.mpaz_pro_app_movil.validacion.Validador

// =============================================
// FORMULARIO PARA CREAR UNA UNIDAD (perfil UTP)
// =============================================
// codigosExistentes : códigos OA ya usados, para no repetirlos
// siguienteId       : número que recibirá la unidad nueva
// onCrear           : se llama con la unidad ya armada cuando todo es válido
@Composable
fun FormularioUnidad(
    codigosExistentes: List<String>,
    siguienteId: Int,
    onCrear: (Unidad) -> Unit
) {
    // Lo que la persona va escribiendo. Se guarda con remember
    // para que no se pierda cuando la pantalla se redibuja.
    var asignatura by remember { mutableStateOf<String?>(null) }
    var titulo by remember { mutableStateOf("") }
    var codigoOA by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }
    var explicacion by remember { mutableStateOf("") }
    var ejemplo by remember { mutableStateOf("") }

    // Los errores solo se muestran después de tocar "Guardar" la primera vez.
    // Desde ahí se actualizan mientras la persona corrige.
    var mostrarErrores by remember { mutableStateOf(false) }

    // Cada error sale del Validador central. null significa "está bien".
    val errorAsignatura = Validador.asignatura(asignatura)
    val errorTitulo = Validador.longitud(titulo, "El título", 5, 60)
    val errorCodigo = Validador.codigoOA(codigoOA)
        ?: Validador.codigoNoRepetido(codigoOA, codigosExistentes)
    val errorObjetivo = Validador.longitud(objetivo, "El objetivo", 10, 150)
    val errorExplicacion = Validador.longitud(explicacion, "La explicación", 20, 300)
    val errorEjemplo = Validador.longitud(ejemplo, "El ejemplo", 5, 200)

    val todoCorrecto = errorAsignatura == null &&
            errorTitulo == null &&
            errorCodigo == null &&
            errorObjetivo == null &&
            errorExplicacion == null &&
            errorEjemplo == null

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(
            text = "Datos de la unidad",
            style = MaterialTheme.typography.titleMedium
        )

        // Asignatura: se elige tocando una opción.
        Text(
            text = "Asignatura",
            style = MaterialTheme.typography.labelLarge
        )
        DatosPrueba.asignaturas.forEach { opcion ->
            FilterChip(
                modifier = Modifier.fillMaxWidth(),
                selected = asignatura == opcion,
                onClick = { asignatura = opcion },
                label = { Text(opcion) }
            )
        }
        if (mostrarErrores && errorAsignatura != null) {
            Text(
                text = errorAsignatura,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        CampoValidado(
            valor = titulo,
            alCambiar = { titulo = it },
            etiqueta = "Título de la unidad",
            icono = Icons.Filled.Edit,
            error = if (mostrarErrores) errorTitulo else null,
            correcto = errorTitulo == null,
            ayuda = "Entre 5 y 60 caracteres."
        )

        CampoValidado(
            valor = codigoOA,
            // El código siempre se escribe en mayúsculas.
            alCambiar = { codigoOA = it.uppercase() },
            etiqueta = "Código OA",
            icono = Icons.Filled.Info,
            error = if (mostrarErrores) errorCodigo else null,
            correcto = errorCodigo == null,
            ayuda = "Formato OA-XXX-00, por ejemplo OA-LEN-01."
        )

        CampoValidado(
            valor = objetivo,
            alCambiar = { objetivo = it },
            etiqueta = "Objetivo de aprendizaje",
            icono = Icons.Filled.Star,
            error = if (mostrarErrores) errorObjetivo else null,
            correcto = errorObjetivo == null,
            ayuda = "Entre 10 y 150 caracteres.",
            unaLinea = false,
            lineasMinimas = 2
        )

        CampoValidado(
            valor = explicacion,
            alCambiar = { explicacion = it },
            etiqueta = "Explicación para el estudiante",
            icono = Icons.AutoMirrored.Filled.List,
            error = if (mostrarErrores) errorExplicacion else null,
            correcto = errorExplicacion == null,
            ayuda = "Entre 20 y 300 caracteres.",
            unaLinea = false,
            lineasMinimas = 3
        )

        CampoValidado(
            valor = ejemplo,
            alCambiar = { ejemplo = it },
            etiqueta = "Ejemplo",
            icono = Icons.Filled.PlayArrow,
            error = if (mostrarErrores) errorEjemplo else null,
            correcto = errorEjemplo == null,
            ayuda = "Entre 5 y 200 caracteres.",
            unaLinea = false,
            lineasMinimas = 2
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Primero se muestran los errores (si los hay).
                mostrarErrores = true

                // Solo se crea la unidad si todos los campos son válidos.
                val asignaturaElegida = asignatura
                if (todoCorrecto && asignaturaElegida != null) {
                    onCrear(
                        Unidad(
                            id = siguienteId,
                            asignatura = asignaturaElegida,
                            titulo = titulo.trim(),
                            codigoOA = codigoOA.trim(),
                            objetivo = objetivo.trim(),
                            explicacion = explicacion.trim(),
                            ejemplo = ejemplo.trim(),
                            // Toda unidad nueva parte como borrador.
                            estado = EstadoUnidad.BORRADOR
                        )
                    )
                }
            }
        ) {
            Text("Guardar unidad")
        }
    }
}