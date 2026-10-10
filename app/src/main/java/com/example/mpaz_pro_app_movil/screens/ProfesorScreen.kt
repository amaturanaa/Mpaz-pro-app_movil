
package com.example.mpaz_pro_app_movil.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// =============================================
// DATOS DE CADA PRUEBA CONFIGURABLE
// =============================================
// clavePreferencias debe coincidir exactamente
// con el nombre utilizado en cada Prueba*.kt.
private data class PruebaConfigurable(
    val nombre: String,
    val tema: String,
    val clavePreferencias: String
)

// =============================================
// PANTALLA DEL DOCENTE
// =============================================
@Composable
fun ProfesorScreen(onCerrarSesion: () -> Unit) {

    // El docente podrá cambiar los intentos de cada prueba.
    val pruebas = remember {
        listOf(
            PruebaConfigurable("Lenguaje", "Gerundios", "prueba_lenguaje"),
            PruebaConfigurable("Matemática", "Números y operaciones", "prueba_matematicas"),
            PruebaConfigurable("Ciencias", "Seres vivos", "prueba_ciencias"),
            PruebaConfigurable("Historia", "Chile y su entorno", "prueba_historia")
        )
    }

    // 0 = Lenguaje; 1 = Matemática; 2 = Ciencias; 3 = Historia.
    var pruebaElegida by remember { mutableIntStateOf(0) }
    val prueba = pruebas[pruebaElegida]
    val context = LocalContext.current

    // Leemos el almacenamiento correspondiente a la prueba
    // elegida. Cada asignatura usa un archivo independiente.
    val preferencias = remember(context, prueba.clavePreferencias) {
        context.getSharedPreferences(
            prueba.clavePreferencias,
            Context.MODE_PRIVATE
        )
    }

    // El selector recupera el último máximo guardado
    // de esta asignatura, o 3 si aún no existe.
    var intentosMaximos by remember(prueba.clavePreferencias) {
        mutableIntStateOf(
            preferencias.getInt("max_intentos", 3).coerceIn(1, 3)
        )
    }

    // Muestra un mensaje después de guardar.
    var mensajeGuardado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Panel del docente",
            style = MaterialTheme.typography.headlineMedium
        )
        Text("Configura las evaluaciones de segundo básico")

        // Separar cada prueba ayuda a no modificar
        // accidentalmente los intentos de otra asignatura.
        Text(
            text = "Selecciona una prueba",
            style = MaterialTheme.typography.titleMedium
        )

        // Dos filas con dos asignaturas cada una.
        pruebas.chunked(2).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fila.forEach { opcion ->
                    val indice = pruebas.indexOf(opcion)
                    FilterChip(
                        modifier = Modifier.weight(1f),
                        selected = pruebaElegida == indice,
                        onClick = {
                            pruebaElegida = indice
                            mensajeGuardado = ""
                        },
                        label = { Text(opcion.nombre) }
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Prueba de ${prueba.nombre}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text("Tema: ${prueba.tema}")
                Text("Cantidad máxima de intentos:")

                // Crea los tres RadioButton: 1, 2 y 3.
                (1..3).forEach { cantidad ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = intentosMaximos == cantidad,
                            onClick = {
                                intentosMaximos = cantidad
                                mensajeGuardado = ""
                            }
                        )
                        Text("$cantidad intento(s)")
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // Guardamos el límite en el mismo sitio
                        // donde la prueba del alumno lo consulta.
                        preferencias.edit()
                            .putInt("max_intentos", intentosMaximos)
                            .apply()

                        mensajeGuardado = "Guardado: ${prueba.nombre} permite " +
                                "$intentosMaximos intento(s)."
                    }
                ) {
                    Text("Guardar configuración")
                }

                // Solo mostramos la confirmación si se guardó.
                if (mensajeGuardado.isNotEmpty()) {
                    Text(mensajeGuardado)
                }
            }
        }

        Text(
            text = "Nota: cambiar el máximo no reinicia los intentos ya utilizados. " +
                    "En esta versión de prueba, los intentos se guardan por instalación, " +
                    "no por alumno.",
            style = MaterialTheme.typography.bodySmall
        )

        // Devuelve a la pantalla de inicio de sesión.
        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

