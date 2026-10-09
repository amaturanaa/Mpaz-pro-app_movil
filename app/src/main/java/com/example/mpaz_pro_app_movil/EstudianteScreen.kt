
package com.example.mpaz_pro_app_movil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteScreen(onCerrarSesion: () -> Unit) {

    // Controla la pestaña seleccionada
    var pestana by remember { mutableStateOf(0) }

    // Estado del menú lateral
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    // Permite abrir y cerrar el menú
    val scope = rememberCoroutineScope()

    // CONTENEDOR DEL MENÚ LATERAL
    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {

            ModalDrawerSheet {

                Text(
                    text = "Menú principal",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                HorizontalDivider()

                // OPCIÓN: MIS ASIGNATURAS
                NavigationDrawerItem(
                    label = { Text("Mis asignaturas") },
                    selected = pestana == 0,
                    onClick = {
                        pestana = 0
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // OPCIÓN: PRUEBAS
                NavigationDrawerItem(
                    label = { Text("Pruebas") },
                    selected = pestana == 1,
                    onClick = {
                        pestana = 1
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // OPCIÓN: MI AVANCE
                NavigationDrawerItem(
                    label = { Text("Mi avance") },
                    selected = pestana == 2,
                    onClick = {
                        pestana = 2
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // OPCIÓN: CERRAR SESIÓN
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        onCerrarSesion()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {

        // PANTALLA PRINCIPAL
        Scaffold(
            modifier = Modifier.fillMaxSize(),

            // BARRA SUPERIOR
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

                    // BOTÓN DE LAS TRES LÍNEAS
                    actions = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    }
                )
            },

            // BARRA DE NAVEGACIÓN INFERIOR
            bottomBar = {
                NavigationBar {

                    NavigationBarItem(
                        selected = pestana == 0,
                        onClick = { pestana = 0 },
                        icon = {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = "Asignaturas"
                            )
                        },
                        label = { Text("Asignaturas") }
                    )

                    NavigationBarItem(
                        selected = pestana == 1,
                        onClick = { pestana = 1 },
                        icon = {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Pruebas"
                            )
                        },
                        label = { Text("Pruebas") }
                    )

                    NavigationBarItem(
                        selected = pestana == 2,
                        onClick = { pestana = 2 },
                        icon = {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Avance"
                            )
                        },
                        label = { Text("Avance") }
                    )
                }
            }

        ) { innerPadding ->

            // CONTENIDO DE CADA PESTAÑA
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                when (pestana) {

                    0 -> ContenidoAsignaturas()

                    1 -> Text(
                        text = "Aquí va la prueba final",
                        style = MaterialTheme.typography.titleMedium
                    )

                    else -> Text(
                        text = "Aquí va el avance del estudiante",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

// CONTENIDO DE LAS ASIGNATURAS
@Composable
fun ContenidoAsignaturas() {

    val asignaturas = listOf(
        "Lenguaje y Comunicación",
        "Matemáticas",
        "Ciencias Naturales",
        "Historia, Geografía y Ciencias Sociales"
    )

    asignaturas.forEach { asignatura ->

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = asignatura,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}
