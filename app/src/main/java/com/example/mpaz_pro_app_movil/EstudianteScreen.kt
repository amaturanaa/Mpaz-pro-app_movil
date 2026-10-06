package com.example.mpaz_pro_app_movil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.components.MenuOpciones

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteScreen(onCerrarSesion: () -> Unit) {
    var pestana by remember { mutableStateOf(0) }
    var menuAbierto by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (pestana) {
                           0 -> "Mis asignaturas"
                           1 -> "Prueba final"
                           else -> "Mi avance"
                        }
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuAbierto = true} ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Opciones"
                            )
                        }
                        MenuOpciones(
                            isExpanded = menuAbierto,
                            opciones = listOf("Cerrar sesión"),
                            onItemClick = { opcion ->
                                if (opcion=="Cerrar sesión") onCerrarSesion()
                            },
                            onDismiss = { menuAbierto = false }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pestana == 0,
                    onClick = { pestana = 0},
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Asignaturas") },
                    label = { Text("Asignaturas") }
                )
                NavigationBarItem(
                    selected = pestana == 1,
                    onClick = { pestana = 1},
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Pruebas") },
                    label = { Text("Pruebas") }
                )
                NavigationBarItem(
                    selected = pestana == 2,
                    onClick = { pestana = 2},
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Avance") },
                    label = { Text("Avance") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (pestana) {
                0 -> ContenidoAsignaturas()
                1 -> Text("Aquí va la prueba final", style = MaterialTheme.typography.titleMedium)
                else -> Text("Aquí va el avance del estudiante", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun ContenidoAsignaturas() {
    val asignaturas = listOf(
        "Lenguaje y Comunicación",
        "Matemáticas",
        "Ciencias Naturales",
        "Historia, Geografía y Ciencias Sociales"
    )
    asignaturas.forEach { asignatura ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = asignatura,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}



