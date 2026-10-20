package com.example.mpaz_pro_app_movil.screens.estudiante

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mpaz_pro_app_movil.components.Transiciones
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mpaz_pro_app_movil.viewmodel.EstudianteViewModel
import com.example.mpaz_pro_app_movil.viewmodel.PruebaUnidad
import com.example.mpaz_pro_app_movil.datos.ConfigPruebas
import androidx.compose.ui.platform.LocalContext
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import kotlinx.coroutines.launch
import com.example.mpaz_pro_app_movil.ui.theme.*
import com.example.mpaz_pro_app_movil.pruebas.PruebaDeUnidad
import com.example.mpaz_pro_app_movil.pruebas.paletaDeAsignatura


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteScreen(
    onCerrarSesion: () -> Unit,
    viewModel: EstudianteViewModel = viewModel()
) {

    // Indica qué pestaña principal está seleccionada:
    // 0 = Asignaturas, 1 = Pruebas, 2 = Avance.
    var pestana by remember { mutableIntStateOf(0) }

    // Indica qué prueba se ha elegido dentro de "Pruebas":
    // -1 = mostrar el MENÚ con las pruebas asignadas.
    // Cualquier otro número es el id de la unidad cuya prueba se está rindiendo.
    var pruebaSeleccionada by remember { mutableIntStateOf(-1) }

    // null = no hay ninguna abierta. Si tiene un nombre, se muestra esa unidad.
    var asignaturaAbierta by remember { mutableStateOf<String?>(null) }


    // Guarda la unidad publicada que el alumno tocó.
    // null = no hay ninguna abierta.
    var unidadAbierta by remember { mutableStateOf<Unidad?>(null) }


    // Controla si el menú lateral está abierto o cerrado.
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Permite ejecutar la apertura/cierre del menú lateral.
    val scope = rememberCoroutineScope()


    // MENÚ LATERAL
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

                // Opción para ir a las asignaturas.
                NavigationDrawerItem(
                    label = { Text("Mis asignaturas") },
                    selected = pestana == 0,
                    onClick = {
                        pestana = 0
                        pruebaSeleccionada = -1
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Opción para ir al listado de pruebas asignadas.
                NavigationDrawerItem(
                    label = { Text("Pruebas") },
                    selected = pestana == 1,
                    onClick = {
                        pestana = 1
                        pruebaSeleccionada = -1
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Opción para ir al avance académico.
                NavigationDrawerItem(
                    label = { Text("Mi avance") },
                    selected = pestana == 2,
                    onClick = {
                        pestana = 2
                        pruebaSeleccionada = -1
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Llama a la función que ya existía para cerrar sesión.
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = onCerrarSesion,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        // Scaffold mantiene la barra superior, el contenido
        // central y la barra inferior en una sola pantalla.
        Scaffold(
            modifier = Modifier.fillMaxSize(),


            // BARRA SUPERIOR
            topBar = {
                TopAppBar(
                    title = {
                        // NUEVO: Crossfade funde el título viejo con el nuevo
                        // cada vez que se cambia de pestaña.
                        Crossfade(
                            targetState = pestana,
                            label = "tituloAlumno"
                        ) { pestanaTitulo ->
                            Text(
                                when (pestanaTitulo) {
                                    0 -> "Mis asignaturas"
                                    1 -> "Mis pruebas"
                                    else -> "Mi avance"
                                }
                            )
                        }
                    },
                    actions = {
                        // Abre el menú lateral cuando se toca el icono.
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    }
                )
            },

            // BARRA INFERIOR: 3 PESTAÑAS
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = pestana == 0,
                        onClick = {
                            pestana = 0
                            pruebaSeleccionada = -1
                        },
                        icon = {
                            // NUEVO: ícono que crece cuando está seleccionado.
                            IconoPestana(Icons.Filled.Home, "Asignaturas", pestana == 0)
                        },
                        label = { Text("Asignaturas") }
                    )

                    NavigationBarItem(
                        selected = pestana == 1,
                        onClick = {
                            pestana = 1
                            // Al pulsar Pruebas volvemos a su listado.
                            pruebaSeleccionada = -1
                        },
                        icon = {
                            IconoPestana(Icons.Filled.Edit, "Pruebas", pestana == 1)
                        },
                        label = { Text("Pruebas") }
                    )

                    NavigationBarItem(
                        selected = pestana == 2,
                        onClick = {
                            pestana = 2
                            pruebaSeleccionada = -1
                        },
                        icon = {
                            IconoPestana(Icons.Filled.Star, "Avance", pestana == 2)
                        },
                        label = { Text("Avance") }
                    )
                }
            }
        ) { innerPadding ->

            // Box ocupa el espacio ENTRE las barras.
            // A diferencia de una columna desplazable exterior,
            // esto evita problemas de scroll con los cuestionarios,
            // que ya tienen su propio verticalScroll.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                //CAMBIO ANIMADO ENTRE PESTAÑAS
                // AnimatedContent compara la pestaña anterior (initialState)
                // con la nueva (targetState):
                //   - si la nueva está a la DERECHA (número mayor) -> avanzar
                //   - si está a la IZQUIERDA (número menor)         -> retroceder
                // Así el movimiento coincide con la barra inferior.
                AnimatedContent(
                    targetState = pestana,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        Transiciones.lateral(haciaAdelante = targetState > initialState)
                    },
                    label = "pestanasAlumno"
                ) { pestanaActual ->
                    when (pestanaActual) {
                        // Pestaña 0: conservamos la pantalla de asignaturas.
                        0 -> {
                            // Copiamos los valores en variables fijas
                            // para que Kotlin sepa que no cambiarán a mitad de camino.
                            val unidadSeleccionada = unidadAbierta
                            val asignaturaSeleccionada = asignaturaAbierta

                            // resumimos en UN solo valor qué se está viendo.
                            val vista = when {
                                unidadSeleccionada != null ->
                                    VistaAsignaturas.UnidadAbierta(unidadSeleccionada)
                                asignaturaSeleccionada != null ->
                                    VistaAsignaturas.AsignaturaAbierta(asignaturaSeleccionada)
                                else -> VistaAsignaturas.Lista
                            }

                            // animamos el paso lista -> detalle -> lista.
                            // Si vamos hacia la Lista es "volver" (retroceder);
                            // si abrimos algo es "entrar" (avanzar).
                            AnimatedContent(
                                targetState = vista,
                                modifier = Modifier.fillMaxSize(),
                                transitionSpec = {
                                    // Más adentro (lista -> asignatura -> unidad) = avanzar;
                                    // menos adentro = volver.
                                    Transiciones.lateral(
                                        haciaAdelante = targetState.nivel() > initialState.nivel()
                                    )
                                },
                                label = "vistaAsignaturas"
                            ) { vistaActual ->
                                when (vistaActual) {

                                    // El alumno tocó una unidad publicada.
                                    is VistaAsignaturas.UnidadAbierta -> UnidadPublicadaScreen(
                                        unidad = vistaActual.unidad,
                                        onVolver = { unidadAbierta = null }
                                    )

                                    // El alumno tocó una de las 4 asignaturas.
                                    // Dentro de la asignatura se ven sus unidades asignadas.
                                    // Al tocar una, se abre encima y, al volver, se regresa
                                    // a esta misma asignatura.
                                    is VistaAsignaturas.AsignaturaAbierta -> UnidadScreen(
                                        asignatura = vistaActual.nombre,
                                        unidades = viewModel.unidadesAsignadas.filter { asignada ->
                                            asignada.unidad.asignatura == vistaActual.nombre
                                        },
                                        onAbrirUnidad = { unidad -> unidadAbierta = unidad },
                                        onVolver = { asignaturaAbierta = null }
                                    )

                                    // Lista principal: solo las 4 asignaturas.
                                    VistaAsignaturas.Lista -> Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(16.dp)
                                    ) {
                                        // NUEVO (MVVM): los nombres vienen del ViewModel.
                                        // Las unidades asignadas ya no van en una lista aparte:
                                        // cada asignatura avisa cuántas tiene y las muestra
                                        // al abrirla.
                                        ContenidoAsignaturas(
                                            asignaturas = viewModel.asignaturas,
                                            cantidadUnidades = { asignatura ->
                                                viewModel.unidadesAsignadas.count { asignada ->
                                                    asignada.unidad.asignatura == asignatura
                                                }
                                            },
                                            onAbrir = { asignatura -> asignaturaAbierta = asignatura }
                                        )
                                    }
                                }
                            }
                        }

                        // Pestaña 1: primero se muestra un listado con las pruebas
                        // de las unidades asignadas. Al elegir una se abre ESA prueba.
                        1 -> {
                            // -1 es el menú; otro número es el id de una unidad.
                            // Abrir una prueba = avanzar; volver al menú = retroceder.
                            AnimatedContent(
                                targetState = pruebaSeleccionada,
                                modifier = Modifier.fillMaxSize(),
                                transitionSpec = {
                                    Transiciones.lateral(haciaAdelante = targetState != -1)
                                },
                                label = "vistaPruebas"
                            ) { pruebaActual ->
                                if (pruebaActual == -1) {
                                    // Las pruebas vienen del ViewModel: una por cada
                                    // unidad que el docente asignó.
                                    MenuDePruebas(
                                        pruebas = viewModel.pruebasAsignadas,
                                        asignaturas = viewModel.asignaturas,
                                        onElegirPrueba = { unidadId ->
                                            pruebaSeleccionada = unidadId
                                        }
                                    )
                                } else {
                                    val prueba = viewModel.pruebasAsignadas.firstOrNull { elegida ->
                                        elegida.unidad.id == pruebaActual
                                    }

                                    Column(modifier = Modifier.fillMaxSize()) {
                                        // Este botón vuelve al listado sin borrar
                                        // el historial de notas ni los intentos.
                                        TextButton(
                                            onClick = { pruebaSeleccionada = -1 },
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text("← Volver a mis pruebas")
                                        }

                                        // La prueba ocupa el espacio restante.
                                        // Conserva su propia interfaz y scroll.
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            if (prueba != null) {
                                                PruebaDeUnidad(
                                                    unidad = prueba.unidad,
                                                    preguntas = prueba.preguntas
                                                )
                                            } else {
                                                Text("Prueba no encontrada")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Pestaña 2: avance del estudiante (mejor nota, promedio e intentos).
                        else -> AvanceEstudiante(pruebas = viewModel.pruebasAsignadas)
                    }
                }
            }
        }
    }
}

// MENÚ DE PRUEBAS
// Muestra, agrupadas por asignatura, las pruebas de las unidades que el
// docente asignó. Recibe una función que indica cuál unidad se eligió.
// No hace ni corrige preguntas aquí: eso ocurre dentro de PruebaDeUnidad.
@Composable
private fun MenuDePruebas(
    pruebas: List<PruebaUnidad>,
    asignaturas: List<String>,
    onElegirPrueba: (Int) -> Unit
) {
    val context = LocalContext.current

    // Columna desplazable SOLO para el menú de selección.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Mis pruebas",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Aquí aparecen las pruebas de las unidades que tu profesor te asignó.",
            style = MaterialTheme.typography.bodyMedium
        )

        // Una sección por asignatura, con todas sus pruebas.
        asignaturas.forEach { asignatura ->
            val deEstaAsignatura = pruebas.filter { prueba ->
                prueba.unidad.asignatura == asignatura
            }
            val colores = paletaDeAsignatura(asignatura)

            Text(
                text = asignatura,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (deEstaAsignatura.isEmpty()) {
                Text(
                    text = "Todavía no hay pruebas asignadas en esta asignatura.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            deEstaAsignatura.forEach { prueba ->
                val usados = ConfigPruebas.intentosUsados(context, prueba.unidad.id)
                val maximo = ConfigPruebas.maxIntentos(context, prueba.unidad.id)
                val cantidad = prueba.preguntas.size

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Al tocar la tarjeta, avisamos qué unidad se eligió.
                        .clickable { onElegirPrueba(prueba.unidad.id) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colores.fondo)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Prueba: ${prueba.unidad.titulo}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = (if (cantidad == 1) "1 pregunta" else "$cantidad preguntas") +
                                        " · Intentos usados: $usados de $maximo",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoPrincipal
                            )
                        }
                        Text(
                            text = "Abrir →",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextoPrincipal
                        )
                    }
                }
            }
        }
    }
}


// PESTAÑA ORIGINAL DE ASIGNATURAS
// Conservamos la función para no modificar
// tu diseño actual de las asignaturas.
@Composable
fun ContenidoAsignaturas(
    // los nombres llegan desde EstudianteViewModel.asignaturas.
    asignaturas: List<String>,
    // Cuántas unidades asignadas tiene cada asignatura.
    cantidadUnidades: (String) -> Int,
    onAbrir: (String) -> Unit
) {



    val colores = listOf(
        RosadoClaro,
        AzulClaro,
        VerdeClaro,
        AmarilloClaro
    )

    // Creamos una tarjeta por asignatura.
    asignaturas.forEachIndexed { indice, asignatura ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { onAbrir(asignatura) },
            colors = CardDefaults.cardColors(
                // getOrElse evita un error si algún día hay más de 4 asignaturas.
                containerColor = colores.getOrElse(indice) { AzulClaro }
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = asignatura,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal
                )

                // Aviso pequeño si hay unidades asignadas en esta asignatura.
                val cantidad = cantidadUnidades(asignatura)
                if (cantidad > 0) {
                    Text(
                        text = if (cantidad == 1) "1 unidad nueva" else "$cantidad unidades nuevas",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextoPrincipal
                    )
                }
            }
        }
    }
}



//QUÉ SE VE EN LA PESTAÑA ASIGNATURAS
// Antes se usaban tres if/else (unidad, asignatura o lista).
// Para animar el cambio, AnimatedContent necesita UN solo valor
// que diga qué pantalla mostrar. Esta clase sellada es ese valor:
// solo puede ser una de estas tres opciones.
private sealed interface VistaAsignaturas {

    // La lista principal con las 4 asignaturas.
    data object Lista : VistaAsignaturas

    // Una de las 4 asignaturas abierta (ej: "Matemáticas").
    data class AsignaturaAbierta(val nombre: String) : VistaAsignaturas

    // Una unidad publicada por la UTP abierta.
    data class UnidadAbierta(val unidad: Unidad) : VistaAsignaturas
}

// Qué tan adentro está cada vista. Sirve para saber si la animación
// debe avanzar (entrar más adentro) o retroceder (volver).
private fun VistaAsignaturas.nivel(): Int = when (this) {
    VistaAsignaturas.Lista -> 0
    is VistaAsignaturas.AsignaturaAbierta -> 1
    is VistaAsignaturas.UnidadAbierta -> 2
}



//ÍCONO DE PESTAÑA ANIMADO (animateFloatAsState)
// Cuando la pestaña está seleccionada, el ícono crece a 125 %
// con un pequeño rebote (spring). Al dejar de estar seleccionado
// vuelve a su tamaño normal. Ayuda a los niños a ver dónde están.
@Composable
private fun IconoPestana(
    icono: ImageVector,
    descripcion: String,
    seleccionado: Boolean
) {
    val escala by animateFloatAsState(
        targetValue = if (seleccionado) 1.25f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "escalaIcono"
    )

    Icon(
        imageVector = icono,
        contentDescription = descripcion,
        modifier = Modifier.scale(escala)
    )
}