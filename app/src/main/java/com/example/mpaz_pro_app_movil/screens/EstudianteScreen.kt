package com.example.mpaz_pro_app_movil.screens

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
import com.example.mpaz_pro_app_movil.viewmodel.PruebaAlumno
import com.example.mpaz_pro_app_movil.viewmodel.UnidadAsignada
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import kotlinx.coroutines.launch
import com.example.mpaz_pro_app_movil.ui.theme.*
import com.example.mpaz_pro_app_movil.pruebas.PruebaLenguaje
import com.example.mpaz_pro_app_movil.pruebas.PruebaMatematicas
import com.example.mpaz_pro_app_movil.pruebas.PruebaCiencias
import com.example.mpaz_pro_app_movil.pruebas.PruebaHistoria
import com.example.mpaz_pro_app_movil.pruebas.utiles.PaletasPrueba


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
    // -1 = mostrar el MENÚ de las cuatro pruebas.
    //  0 = Lenguaje, 1 = Matemática, 2 = Ciencias, 3 = Historia.
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

                // Opción para ir al listado de las cuatro pruebas.
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
                                    1 -> "Prueba final"
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
                                    Transiciones.lateral(
                                        haciaAdelante = targetState != VistaAsignaturas.Lista
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
                                    is VistaAsignaturas.AsignaturaAbierta -> UnidadScreen(
                                        asignatura = vistaActual.nombre,
                                        onVolver = { asignaturaAbierta = null }
                                    )

                                    // Lista principal: asignaturas + unidades nuevas.
                                    VistaAsignaturas.Lista -> Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(16.dp)
                                    ) {
                                        // NUEVO (MVVM): los nombres vienen del ViewModel.
                                        ContenidoAsignaturas(
                                            asignaturas = viewModel.asignaturas,
                                            onAbrir = { asignatura -> asignaturaAbierta = asignatura }
                                        )

                                        // Debajo de las 4 asignaturas mostramos
                                        // las unidades que la UTP publicó (ej: "Chani").
                                        // NUEVO (MVVM): la lista ya filtrada viene del ViewModel.
                                        // NUEVO (tarea 3): ahora solo trae las publicadas
                                        // que el docente asignó.
                                        UnidadesPublicadas(
                                            asignadas = viewModel.unidadesAsignadas,
                                            onAbrirUnidad = { unidad -> unidadAbierta = unidad }
                                        )
                                    }
                                }
                            }
                        }

                        // Pestaña 1: primero se muestra un listado.
                        // Al elegir una asignatura se abre SU prueba.
                        1 -> {
                            // -1 es el menú; 0 a 3 es una prueba.
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
                                    // NUEVO (MVVM): las 4 pruebas vienen del ViewModel.
                                    MenuDePruebas(
                                        pruebas = viewModel.pruebas,
                                        onElegirPrueba = { numero ->
                                            pruebaSeleccionada = numero
                                        }
                                    )
                                } else {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        // Este botón vuelve al listado sin borrar
                                        // el historial de notas ni los intentos.
                                        TextButton(
                                            onClick = { pruebaSeleccionada = -1 },
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text("← Volver a las cuatro pruebas")
                                        }

                                        // Cada prueba ocupa el espacio restante.
                                        // Se conserva su propia interfaz y scroll.
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            when (pruebaActual) {

                                                // Si seleccionamos Lenguaje,
                                                // mostramos sus cinco preguntas.
                                                0 -> PruebaLenguaje()

                                                // Si seleccionamos Matemática,
                                                // mostramos la prueba de Matemática.
                                                1 -> PruebaMatematicas()

                                                // Si seleccionamos Ciencias Naturales,
                                                // mostramos la prueba de Ciencias.
                                                2 -> PruebaCiencias()

                                                // Si seleccionamos Historia,
                                                // mostramos la prueba de Historia.
                                                3 -> PruebaHistoria()

                                                // Si ocurre una selección desconocida,
                                                // mostramos un mensaje.
                                                else -> Text("Prueba no encontrada")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Pestaña 2: avance del estudiante (mejor nota, promedio e intentos).
                        else -> AvanceEstudiante(asignaturas = viewModel.asignaturas)
                    }
                }
            }
        }
    }
}

// MENÚ DE LAS CUATRO PRUEBAS
// Recibe una función que indica cuál de las 4 pruebas se eligió.
// No hace ni corrige preguntas aquí: eso ocurre dentro
// de PruebaLenguaje, PruebaMatematicas, etc.
@Composable
private fun MenuDePruebas(
    pruebas: List<PruebaAlumno>,
    onElegirPrueba: (Int) -> Unit
) {
    // cada tarjeta usa el fondo suave de su asignatura,
    // en el mismo orden que las pruebas (Lenguaje, Matemática, Ciencias, Historia).
    val paletas = listOf(
        PaletasPrueba.lenguaje,
        PaletasPrueba.matematicas,
        PaletasPrueba.ciencias,
        PaletasPrueba.historia
    )

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
            text = "Selecciona una asignatura para ver su prueba.",
            style = MaterialTheme.typography.bodyMedium
        )

        // Recorremos las cuatro pruebas para crear sus tarjetas.
        pruebas.forEachIndexed { indice, datos ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // 0 = Lenguaje; 1 = Matemática;
                        // 2 = Ciencias; 3 = Historia.
                        onElegirPrueba(indice)
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = paletas.getOrElse(indice) { PaletasPrueba.matematicas }.fondo
                )
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
                            text = datos.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextoPrincipal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = datos.descripcion,
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


// PESTAÑA ORIGINAL DE ASIGNATURAS
// Conservamos la función para no modificar
// tu diseño actual de las asignaturas.
@Composable
fun ContenidoAsignaturas(
    // los nombres llegan desde EstudianteViewModel.asignaturas.
    asignaturas: List<String>,
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
            Text(
                text = asignatura,
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}



// NUEVO: LISTA DE UNIDADES PUBLICADAS POR LA UTP
// asignadas     -> la lista YA filtrada por EstudianteViewModel
//                  (publicadas por la UTP Y asignadas por el docente)
// onAbrirUnidad -> se ejecuta cuando el alumno toca una unidad
// NUEVO (MVVM): antes esta función leía AlmacenMpaz y filtraba
// por su cuenta. Ahora solo dibuja lo que le entregan.
@Composable
fun UnidadesPublicadas(
    asignadas: List<UnidadAsignada>,
    onAbrirUnidad: (Unidad) -> Unit
) {

    // Título de la sección.
    Text(
        text = "Unidades nuevas",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
    )

    // NUEVO (tarea 3): si no hay ninguna asignada, avisamos con un
    // mensaje que explica que falta el paso del profesor.
    if (asignadas.isEmpty()) {
        Text(
            text = "Tu profesor todavía no te ha asignado unidades.",
            style = MaterialTheme.typography.bodyMedium
        )
    }

    // Creamos una tarjeta por cada unidad asignada.
    // "(unidad, cursos, fechaHasta)" saca los tres datos de UnidadAsignada.
    asignadas.forEach { (unidad, cursos, fechaHasta) ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                // Al tocar la tarjeta, avisamos qué unidad se eligió.
                .clickable { onAbrirUnidad(unidad) },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AzulClaro)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Asignatura en letra pequeña (ej: "Ciencias Naturales").
                Text(
                    text = unidad.asignatura,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoPrincipal
                )
                // Título de la unidad en grande (ej: "Chani").
                Text(
                    text = unidad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal
                )
                // Objetivo, para que el alumno sepa de qué trata.
                Text(
                    text = unidad.objetivo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoPrincipal
                )
                // NUEVO (tarea 3): datos de la asignación del docente.
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Curso: ${cursos.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoPrincipal
                )
                Text(
                    text = "Disponible hasta: $fechaHasta",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoPrincipal
                )
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

    // La lista principal con las 4 asignaturas y las unidades nuevas.
    data object Lista : VistaAsignaturas

    // Una de las 4 asignaturas abierta (ej: "Matemáticas").
    data class AsignaturaAbierta(val nombre: String) : VistaAsignaturas

    // Una unidad publicada por la UTP abierta.
    data class UnidadAbierta(val unidad: Unidad) : VistaAsignaturas
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