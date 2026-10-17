package com.example.mpaz_pro_app_movil.screens

// =============================================
// IMPORTACIONES
// =============================================
// BackHandler: hace que el botón "atrás" del teléfono vuelva a la lista.
import androidx.activity.compose.BackHandler
// Herramientas para ordenar los elementos y permitir desplazamiento.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// Icono de la flecha para volver.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
// Tarjetas, textos, barra superior, etc.
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// El "molde" de una unidad creada por la UTP.
import com.example.mpaz_pro_app_movil.datos.model.Unidad

// =============================================
// PANTALLA DE UNA UNIDAD PUBLICADA (VISTA ALUMNO)
// =============================================
// Muestra lo que la UTP escribió al crear la unidad:
// objetivo, explicación y ejemplo.
//   unidad   -> la unidad que el alumno tocó
//   onVolver -> qué hacer al presionar la flecha de volver
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadPublicadaScreen(unidad: Unidad, onVolver: () -> Unit) {

    // El botón atrás del teléfono también vuelve a la lista.
    BackHandler(onBack = onVolver)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                // Arriba se ve el título de la unidad (ej: "Chani").
                title = { Text(unidad.titulo) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Asignatura y código OA, en letra pequeña.
            Text(
                text = "${unidad.asignatura} · ${unidad.codigoOA}",
                style = MaterialTheme.typography.labelLarge
            )

            // Usamos la misma tarjeta tres veces, cambiando título y texto.
            TarjetaContenido(titulo = "¿Qué vamos a aprender?", texto = unidad.objetivo)
            TarjetaContenido(titulo = "Lee y escucha", texto = unidad.explicacion)
            TarjetaContenido(titulo = "Ejemplo", texto = unidad.ejemplo)

            BotonNarracionTexto(
                "Explicación. ${unidad.explicacion}. Ejemplo. ${unidad.ejemplo}"
            )
        }
    }
}

// Tarjeta sencilla con un título y un texto.
// "private" significa que solo se usa dentro de este archivo.
@Composable
private fun TarjetaContenido(titulo: String, texto: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(texto, style = MaterialTheme.typography.bodyLarge)
        }
    }
}