package com.example.mpaz_pro_app_movil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.mpaz_pro_app_movil.components.EtiquetaEstado
import com.example.mpaz_pro_app_movil.datos.DatosPrueba
import com.example.mpaz_pro_app_movil.datos.Unidad
import com.example.mpaz_pro_app_movil.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtpScreen(onCerrarSesion: () -> Unit) {

    // 0 = lista de unidades, 1 = nueva unidad
    var pestana by remember { mutableIntStateOf(0) }

    // Unidad que la UTP abrió para revisar.
    // null = no hay ninguna abierta, se ve la lista.
    var unidadAbierta by remember { mutableStateOf<Unidad?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val abierta = unidadAbierta

    if (abierta != null) {

        // =============================================
        // VISTA DE REVISIÓN: contenido + TODAS las preguntas
        // =============================================
        DetalleUnidadUtp(
            unidad = abierta,
            preguntas = DatosPrueba.preguntas.filter { it.unidadId == abierta.id },
            onVolver = { unidadAbierta = null }
        )

    } else {

        // =============================================
        // VISTA NORMAL: menú lateral + pestañas
        // =============================================
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        text = "Jefatura UTP",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()

                    NavigationDrawerItem(
                        label = { Text("Unidades") },
                        selected = pestana == 0,
                        onClick = {
                            pestana = 0
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Nueva unidad") },
                        selected = pestana == 1,
                        onClick = {
                            pestana = 1
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión") },
                        selected = false,
                        onClick = { onCerrarSesion() },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),

                topBar = {
                    TopAppBar(
                        title = {
                            Text(if (pestana == 0) "Unidades" else "Nueva unidad")
                        },
                        actions = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Abrir menú"
                                )
                            }
                        }
                    )
                },

                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = pestana == 0,
                            onClick = { pestana = 0 },
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Unidades") },
                            label = { Text("Unidades") }
                        )
                        NavigationBarItem(
                            selected = pestana == 1,
                            onClick = { pestana = 1 },
                            icon = { Icon(Icons.Filled.Add, contentDescription = "Nueva unidad") },
                            label = { Text("Nueva") }
                        )
                    }
                }
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (pestana) {
                        0 -> ListaUnidadesUtp(
                            unidades = DatosPrueba.unidades,
                            onAbrir = { unidad -> unidadAbierta = unidad }
                        )
                        else -> Text(
                            text = "Aquí irá el formulario para crear una unidad",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

// Lista de todas las unidades, con su asignatura, código y estado.
// Al tocar una tarjeta se avisa con onAbrir para ver su detalle.
@Composable
fun ListaUnidadesUtp(
    unidades: List<Unidad>,
    onAbrir: (Unidad) -> Unit
) {
    unidades.forEach { unidad ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAbrir(unidad) },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FondoTarjetas)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = unidad.asignatura,
                    style = MaterialTheme.typography.labelLarge,
                    color = AzulPrincipal
                )
                Text(
                    text = unidad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal
                )
                Text(
                    text = unidad.codigoOA,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
                EtiquetaEstado(estado = unidad.estado)
            }
        }
    }
}