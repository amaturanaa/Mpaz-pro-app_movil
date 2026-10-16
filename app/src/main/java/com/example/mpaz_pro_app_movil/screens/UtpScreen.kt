package com.example.mpaz_pro_app_movil.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.mpaz_pro_app_movil.components.EtiquetaEstado
import com.example.mpaz_pro_app_movil.components.MensajeError
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.ui.theme.*
import com.example.mpaz_pro_app_movil.viewmodel.UtpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtpScreen(
    onCerrarSesion: () -> Unit,
    viewModel: UtpViewModel = viewModel()
) {

    // 0 = lista de unidades, 1 = nueva unidad
    var pestana by remember { mutableIntStateOf(0) }

    // Unidad que la UTP abrió para revisar y unidad que está editando.
    // Se guarda solo el número (id) y la unidad se busca cada vez,
    // así la pantalla siempre muestra la versión más reciente.
    var unidadAbiertaId by remember { mutableStateOf<Int?>(null) }
    var unidadEditandoId by remember { mutableStateOf<Int?>(null) }

    val unidadAbierta = unidadAbiertaId?.let { viewModel.unidadPorId(it) }
    val unidadEditando = unidadEditandoId?.let { viewModel.unidadPorId(it) }

    // Para avisar "Unidad creada" abajo en la pantalla.
    val snackbarHostState = remember { SnackbarHostState() }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (unidadEditando != null) {

        // =============================================
        // EDITAR UNA UNIDAD EXISTENTE
        // =============================================
        PantallaEditarUnidad(
            unidad = unidadEditando,
            // Los códigos de las OTRAS unidades (la propia no cuenta como repetida).
            codigosOtros = viewModel.unidades
                .filter { it.id != unidadEditando.id }
                .map { it.codigoOA },
            onGuardar = { unidadModificada ->
                val error = viewModel.editarUnidad(unidadModificada)
                if (error == null) {
                    unidadEditandoId = null
                    scope.launch { snackbarHostState.showSnackbar("Cambios guardados.") }
                }
                error
            },
            onVolver = { unidadEditandoId = null }
        )

    } else if (unidadAbierta != null) {

        // =============================================
        // VISTA DE REVISIÓN: contenido + TODAS las preguntas
        // =============================================
        DetalleUnidadUtp(
            unidad = unidadAbierta,
            preguntas = viewModel.preguntasDe(unidadAbierta.id),
            puedeEditar = viewModel.puedeEditarse(unidadAbierta),
            siguienteIdPregunta = viewModel.siguienteIdPregunta(),
            onVolver = { unidadAbiertaId = null },
            onCambiarEstado = { nuevo -> viewModel.cambiarEstado(unidadAbierta.id, nuevo) },
            onAgregarPregunta = { pregunta -> viewModel.agregarPregunta(pregunta) },
            onEliminarPregunta = { id -> viewModel.eliminarPregunta(id) }
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

                snackbarHost = { SnackbarHost(snackbarHostState) },

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
                            unidades = viewModel.unidades,
                            puedeEditar = { unidad -> viewModel.puedeEditarse(unidad) },
                            onAbrir = { unidad -> unidadAbiertaId = unidad.id },
                            onEditar = { unidad -> unidadEditandoId = unidad.id }
                        )
                        else -> FormularioUnidad(
                            codigosExistentes = viewModel.unidades.map { it.codigoOA },
                            siguienteId = viewModel.siguienteIdUnidad(),
                            onGuardar = { unidadNueva ->
                                // Se guarda en el teléfono, se vuelve a la
                                // pestaña Unidades y se avisa cómo salió.
                                val error = viewModel.crearUnidad(unidadNueva)
                                pestana = 0
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        error ?: "Unidad creada como borrador."
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// Pantalla para editar una unidad: la barra de arriba con "Volver"
// y el mismo formulario de siempre, ya relleno con los datos actuales.
// onGuardar devuelve null si salió bien, o el motivo del error.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PantallaEditarUnidad(
    unidad: Unidad,
    codigosOtros: List<String>,
    onGuardar: (Unidad) -> String?,
    onVolver: () -> Unit
) {
    BackHandler { onVolver() }

    var errorAlGuardar by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar unidad") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormularioUnidad(
                codigosExistentes = codigosOtros,
                siguienteId = unidad.id,
                unidadInicial = unidad,
                textoBoton = "Guardar cambios",
                onGuardar = { unidadModificada ->
                    errorAlGuardar = onGuardar(unidadModificada)
                }
            )
            MensajeError(errorAlGuardar)
        }
    }
}

// Lista de todas las unidades, con su asignatura, código y estado.
// Al tocar una tarjeta se avisa con onAbrir para ver su detalle.
// El botón Editar solo aparece si la unidad se puede editar
// (una unidad publicada no se toca).
@Composable
fun ListaUnidadesUtp(
    unidades: List<Unidad>,
    puedeEditar: (Unidad) -> Boolean,
    onAbrir: (Unidad) -> Unit,
    onEditar: (Unidad) -> Unit
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

                if (puedeEditar(unidad)) {
                    TextButton(onClick = { onEditar(unidad) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar")
                    }
                } else {
                    Text(
                        text = "Publicada: para editarla hay que deshabilitarla primero.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoSecundario
                    )
                }
            }
        }
    }
}