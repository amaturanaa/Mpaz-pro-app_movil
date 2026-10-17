package com.example.mpaz_pro_app_movil.screens

// =============================================
// IMPORTACIONES: HERRAMIENTAS PARA LA INTERFAZ
// =============================================

// Nos permiten organizar elementos, crear espacios
// y hacer que el menú de pruebas tenga desplazamiento.
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape

// Iconos de los botones de navegación.
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star

// Text, Card, Scaffold, NavigationBar y demás elementos Material 3.
import androidx.compose.material3.*

// remember y mutableIntStateOf permiten guardar el estado de la pantalla.
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ===== NUEVO: para leer las unidades que publica la UTP =====
// LocalContext: le da a la pantalla el "contexto" del teléfono,
// que se necesita para abrir la base de datos.
import androidx.compose.ui.platform.LocalContext
// AlmacenMpaz: la lista compartida donde la UTP guarda sus unidades.
import com.example.mpaz_pro_app_movil.datos.repositorio.AlmacenMpaz
// EstadoUnidad: para preguntar si una unidad está "PUBLICADO".
import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
// Unidad: el "molde" de una unidad (título, objetivo, explicación...).
import com.example.mpaz_pro_app_movil.datos.model.Unidad

// Se utiliza para abrir y cerrar el menú lateral.
import kotlinx.coroutines.launch

// Colores existentes en el proyecto MPAZ PRO.
import com.example.mpaz_pro_app_movil.ui.theme.*

// IMPORTANTE: importamos las CUATRO funciones de prueba.
// Cada una ya tiene sus propias preguntas, notas e intentos.
import com.example.mpaz_pro_app_movil.pruebas.PruebaLenguaje
import com.example.mpaz_pro_app_movil.pruebas.PruebaMatematicas
import com.example.mpaz_pro_app_movil.pruebas.PruebaCiencias
import com.example.mpaz_pro_app_movil.pruebas.PruebaHistoria

// Esta anotación permite usar algunos componentes de Material 3
// que todavía pueden estar marcados como experimentales.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteScreen(onCerrarSesion: () -> Unit) {

    // Indica qué pestaña principal está seleccionada:
    // 0 = Asignaturas, 1 = Pruebas, 2 = Avance.
    var pestana by remember { mutableIntStateOf(0) }

    // Indica qué prueba se ha elegido dentro de "Pruebas":
    // -1 = mostrar el MENÚ de las cuatro pruebas.
    //  0 = Lenguaje, 1 = Matemática, 2 = Ciencias, 3 = Historia.
    var pruebaSeleccionada by remember { mutableIntStateOf(-1) }

    // null = no hay ninguna abierta. Si tiene un nombre, se muestra esa unidad.
    var asignaturaAbierta by remember { mutableStateOf<String?>(null) }

    // ===== NUEVO =====
    // Guarda la unidad publicada que el alumno tocó.
    // null = no hay ninguna abierta.
    var unidadAbierta by remember { mutableStateOf<Unidad?>(null) }

    // ===== NUEVO =====
    // Carga las unidades desde la base de datos del teléfono.
    // La UTP y el docente ya lo hacían, pero el alumno NO,
    // por eso su lista siempre estaba vacía.
    // Si ya estaba cargado, esta línea no hace nada,
    // así que no importa que se ejecute varias veces.
    val contexto = LocalContext.current
    AlmacenMpaz.iniciar(contexto)

    // Controla si el menú lateral está abierto o cerrado.
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Permite ejecutar la apertura/cierre del menú lateral.
    val scope = rememberCoroutineScope()

    // =============================================
    // MENÚ LATERAL (EL QUE SE ABRE CON LAS TRES RAYAS)
    // =============================================
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

            // =============================================
            // BARRA SUPERIOR
            // =============================================
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (pestana) {
                                0 -> "Mis asignaturas"
                                1 -> "Prueba final"
                                else -> "Mi avance"
                            }
                        )
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

            // =============================================
            // BARRA INFERIOR: 3 PESTAÑAS
            // =============================================
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = pestana == 0,
                        onClick = {
                            pestana = 0
                            pruebaSeleccionada = -1
                        },
                        icon = {
                            Icon(Icons.Filled.Home, contentDescription = "Asignaturas")
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
                            Icon(Icons.Filled.Edit, contentDescription = "Pruebas")
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
                            Icon(Icons.Filled.Star, contentDescription = "Avance")
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
                when (pestana) {
                    // Pestaña 0: conservamos la pantalla de asignaturas.
                    0 -> {
                        // ===== NUEVO =====
                        // Copiamos la unidad abierta en una variable fija
                        // para que Kotlin sepa que no cambiará a mitad de camino.
                        val unidadSeleccionada = unidadAbierta

                        if (unidadSeleccionada != null) {
                            // Si el alumno tocó una unidad publicada,
                            // mostramos su contenido.
                            UnidadPublicadaScreen(
                                unidad = unidadSeleccionada,
                                onVolver = { unidadAbierta = null }
                            )
                        }
                        else if (asignaturaAbierta != null) {
                            // Hacemos esto para asegurarle a kotlin que la
                            // asignatura abierta tiene un valor
                            asignaturaAbierta?.let { asignaturaSeleccionada ->
                                UnidadScreen(
                                    asignatura = asignaturaSeleccionada,
                                    onVolver = { asignaturaAbierta = null }
                                )
                            }

                        }
                        else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(16.dp)
                            ) {
                                ContenidoAsignaturas(
                                    onAbrir = { asignatura -> asignaturaAbierta = asignatura }
                                )

                                // ===== NUEVO =====
                                // Debajo de las 4 asignaturas mostramos
                                // las unidades que la UTP publicó (ej: "Chani").
                                UnidadesPublicadas(
                                    onAbrirUnidad = { unidad -> unidadAbierta = unidad }
                                )
                            }
                        }
                    }

                    // Pestaña 1: primero se muestra un listado.
                    // Al elegir una asignatura se abre SU prueba.
                    1 -> {
                        if (pruebaSeleccionada == -1) {
                            MenuDePruebas(
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




                                    when (pruebaSeleccionada) {

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

                    // Pestaña 2: dejamos intacto el espacio para avance.
                    2 -> Text(
                        text = "Aquí va el avance del estudiante",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

// =============================================
// MENÚ DE LAS CUATRO PRUEBAS
// =============================================

// Recibe una función que indica cuál de las 4 pruebas se eligió.
// No hace ni corrige preguntas aquí: eso ocurre dentro
// de PruebaLenguaje, PruebaMatematicas, etc.
@Composable
private fun MenuDePruebas(onElegirPrueba: (Int) -> Unit) {

    // Definimos los nombres y descripciones de las 4 pruebas.
    val pruebas = listOf(
        "Lenguaje" to "Gerundios · 5 preguntas",
        "Matemática" to "Números y operaciones · 5 preguntas",
        "Ciencias Naturales" to "Seres vivos y naturaleza · 5 preguntas",
        "Historia" to "Chile y su entorno · 5 preguntas"
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
                    containerColor = AzulClaro
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
                            text = datos.first,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextoPrincipal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = datos.second,
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

// =============================================
// PESTAÑA ORIGINAL DE ASIGNATURAS
// =============================================

// Conservamos la función para no modificar
// tu diseño actual de las asignaturas.
@Composable
fun ContenidoAsignaturas(onAbrir: (String) -> Unit) {

    // Nombres de las cuatro asignaturas de 2.º básico.
    val asignaturas = listOf(
        "Lenguaje y Comunicación",
        "Matemáticas",
        "Ciencias Naturales",
        "Historia, Geografía y Ciencias Sociales"
    )

    // Colores originales definidos en ui.theme.
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
                containerColor = colores[indice]
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


// =============================================
// NUEVO: LISTA DE UNIDADES PUBLICADAS POR LA UTP
// =============================================
// Recibe una función que se ejecuta cuando el alumno toca una unidad.
@Composable
fun UnidadesPublicadas(onAbrirUnidad: (Unidad) -> Unit) {

    // De TODAS las unidades, nos quedamos solo con las publicadas.
    // Como AlmacenMpaz.unidades es una lista "observable", si la UTP
    // publica o deshabilita algo, esta lista se actualiza sola.
    val publicadas = AlmacenMpaz.unidades.filter { unidad ->
        unidad.estado == EstadoUnidad.PUBLICADO
    }

    // Título de la sección.
    Text(
        text = "Unidades nuevas",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
    )

    // Si no hay ninguna publicada, avisamos en vez de dejar un hueco.
    if (publicadas.isEmpty()) {
        Text(
            text = "Todavía no hay unidades publicadas.",
            style = MaterialTheme.typography.bodyMedium
        )
    }

    // Creamos una tarjeta por cada unidad publicada.
    publicadas.forEach { unidad ->
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
            }
        }
    }
}
