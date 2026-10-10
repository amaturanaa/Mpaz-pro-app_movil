
package com.example.mpaz_pro_app_movil.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Esta función crea la pantalla del profesor.
@Composable
fun ProfesorScreen() {

    // Guarda la cantidad de intentos seleccionados.
    // Por defecto, la prueba permite 3 intentos.
    var intentosMaximos by remember {
        mutableIntStateOf(3)
    }

    // Organiza los elementos verticalmente.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Título principal.
        Text(
            text = "Panel del profesor",
            style = MaterialTheme.typography.headlineMedium
        )

        // Nombre de la prueba que configuraremos.
        Text(
            text = "Configurar prueba de Lenguaje",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Tema: Gerundios"
        )

        Text(
            text = "Máximo de intentos permitidos:"
        )

        // Crea tres opciones: 1, 2 y 3 intentos.
        (1..3).forEach { numero ->

            Row {

                // Botón circular para elegir una opción.
                RadioButton(
                    selected = intentosMaximos == numero,

                    onClick = {
                        // Guarda la opción seleccionada.
                        intentosMaximos = numero
                    }
                )

                Text(
                    text = "$numero intento(s)",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        // Botón para guardar la configuración.
        Button(
            onClick = {
                // Pendiente: conectar con la
                // configuración de la prueba.
            },

            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar configuración")
        }
    }
}
