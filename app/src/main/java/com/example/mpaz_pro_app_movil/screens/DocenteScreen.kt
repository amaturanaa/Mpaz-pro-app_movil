package com.example.mpaz_pro_app_movil.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.mpaz_pro_app_movil.datos.Asignacion
import com.example.mpaz_pro_app_movil.datos.ConfigPruebas
import com.example.mpaz_pro_app_movil.datos.Unidad
import com.example.mpaz_pro_app_movil.notificaciones.Notificador
import com.example.mpaz_pro_app_movil.ui.theme.*
import com.example.mpaz_pro_app_movil.validacion.Fechas
import com.example.mpaz_pro_app_movil.viewmodel.DocenteViewModel

// Datos de una asignación recién creada, guardados mientras se espera
// la respuesta de la persona al permiso de notificaciones.
private data class AvisoAsignacion(
    val titulo: String,
    val curso: String,
    val fin: String
)

// =============================================
// PANTALLA DEL DOCENTE
// =============================================
// Mismo molde que la de la UTP: menú lateral, barra inferior y pestañas.
//   0 = Unidades (las que la UTP publicó)
//   1 = Asignar una unidad a un curso
//   2 = Informes de lo que hicieron los estudiantes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocenteScreen(
    onCerrarSesion: () -> Unit,
    viewModel: DocenteViewModel = viewModel()
) {
    var pestana by remember { mutableIntStateOf(0) }

    val titulos = listOf("Unidades", "Asignar", "Informes")
    val iconos = listOf(
        Icons.AutoMirrored.Filled.List,
        Icons.Filled.Add,
        Icons.Filled.Info
    )

    val context = LocalContext.current

    // Para avisos cortos abajo en la pantalla.
    val snackbarHostState = remember { SnackbarHostState() }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Asignación hecha que espera la respuesta al permiso de notificaciones.
    var avisoPendiente by remember { mutableStateOf<AvisoAsignacion?>(null) }

    // Ventana de Android que pide permiso para mostrar notificaciones.
    // Si la persona dice que no, la asignación igual queda hecha.
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        val aviso = avisoPendiente
        avisoPendiente = null

        var mostrado = false
        if (concedido && aviso != null) {
            mostrado = Notificador.avisarAsignacion(
                context, aviso.titulo, aviso.curso, aviso.fin
            )
        }
        scope.launch {
            snackbarHostState.showSnackbar(
                if (mostrado) "Unidad asignada. Aviso enviado."
                else "Unidad asignada. Los avisos están desactivados."
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Panel del docente",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()

                titulos.forEachIndexed { posicion, titulo ->
                    NavigationDrawerItem(
                        label = { Text(titulo) },
                        selected = pestana == posicion,
                        onClick = {
                            pestana = posicion
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

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
                    title = { Text(titulos[pestana]) },
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
                    titulos.forEachIndexed { posicion, titulo ->
                        NavigationBarItem(
                            selected = pestana == posicion,
                            onClick = { pestana = posicion },
                            icon = { Icon(iconos[posicion], contentDescription = titulo) },
                            label = { Text(titulo) }
                        )
                    }
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
                    0 -> UnidadesDocente(
                        unidades = viewModel.unidadesPublicadas,
                        asignacionesDe = { unidadId -> viewModel.asignacionesDe(unidadId) }
                    )

                    1 -> FormularioAsignacion(
                        unidades = viewModel.unidadesPublicadas,
                        cursos = ConfigPruebas.cursos,
                        onAsignar = { unidad, curso, inicio, fin, intentos ->
                            val error = viewModel.asignar(
                                unidadId = unidad.id,
                                curso = curso,
                                inicioVisible = inicio,
                                finVisible = fin,
                                maxIntentos = intentos
                            )

                            if (error == null) {
                                val aviso = AvisoAsignacion(unidad.titulo, curso, fin)
                                pestana = 0

                                if (Notificador.necesitaPedirPermiso(context)) {
                                    // Android 13 o más: primero se pide el permiso.
                                    avisoPendiente = aviso
                                    lanzadorPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    val mostrado = Notificador.avisarAsignacion(
                                        context, aviso.titulo, aviso.curso, aviso.fin
                                    )
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (mostrado) "Unidad asignada. Aviso enviado."
                                            else "Unidad asignada. Los avisos están desactivados."
                                        )
                                    }
                                }
                            }
                            error
                        }
                    )

                    else -> InformeDocente(
                        informes = viewModel.informes(),
                        observaciones = viewModel.observaciones,
                        unidadPorId = { id -> viewModel.unidadPorId(id) },
                        onAgregarObservacion = { estudianteId, unidadId, texto ->
                            viewModel.agregarObservacion(estudianteId, unidadId, texto)
                        },
                        onAvisar = { mensaje ->
                            scope.launch { snackbarHostState.showSnackbar(mensaje) }
                        }
                    )
                }
            }
        }
    }
}

// Lista de las unidades publicadas, con los cursos a los que están asignadas.
@Composable
private fun UnidadesDocente(
    unidades: List<Unidad>,
    asignacionesDe: (Int) -> List<Asignacion>
) {
    if (unidades.isEmpty()) {
        AvisoPendiente("La UTP todavía no publica ninguna unidad.")
        return
    }

    Text(
        text = "Unidades publicadas por la UTP",
        style = MaterialTheme.typography.titleMedium,
        color = TextoPrincipal
    )

    unidades.forEach { unidad ->
        val asignaciones = asignacionesDe(unidad.id)

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

                if (asignaciones.isEmpty()) {
                    Text(
                        text = "Sin asignar a ningún curso.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                } else {
                    asignaciones.forEach { asignacion ->
                        Text(
                            text = "Asignada a ${asignacion.curso}: " +
                                    "${Fechas.aVisible(asignacion.fechaInicio)} al " +
                                    "${Fechas.aVisible(asignacion.fechaFin)} · " +
                                    "${asignacion.maxIntentos} intento(s)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = VerdePrincipal
                        )
                    }
                }
            }
        }
    }
}

// Tarjeta con un mensaje corto, para pestañas vacías o por completar.
@Composable
private fun AvisoPendiente(texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjetas)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = TextoSecundario,
            modifier = Modifier.padding(16.dp)
        )
    }
}