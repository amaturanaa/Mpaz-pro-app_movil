package com.example.mpaz_pro_app_movil.screens.login


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.R
import com.example.mpaz_pro_app_movil.auth.AutenticacionDemo
import com.example.mpaz_pro_app_movil.auth.UsuarioDemo


// COLORES DEL LOGO DEL COLEGIO (Colegio Escritora Marcela Paz E-764)
// Celeste del cielo y del agua, y granate del círculo del niño.
private val CelesteLogo = Color(0xFFD6F3F9)      // fondo de la tarjeta
private val GranateLogo = Color(0xFFC23251)      // botón
private val GranateClaro = Color(0xFFE08A9B)     // botón mientras se comprueba
private val GranateOscuro = Color(0xFF7A1F33)    // títulos

// Tamaño del logo del colegio en la parte de arriba.
// Si lo quieres más grande o más chico, cambia este número.
private val TamanoLogo = 120.dp


// PANTALLA DE INICIO DE SESIÓN
// Ya no se elige el perfil: basta con el correo y la contraseña.
// El backend dice si la cuenta es de Alumno, Docente o UTP, y la app
// abre la pantalla que corresponde.
// onIngresar es una función que recibe al usuario
// autenticado y se lo comunica a MainActivity.

@Composable
fun LoginScreen(onIngresar: (UsuarioDemo) -> Unit) {

    // Estos campos se actualizan al escribir en el formulario.
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // Controla si mostramos la contraseña con letras o puntos.
    var mostrarContrasena by remember { mutableStateOf(false) }

    // Variables para explicar los errores al usuario.
    var errorUsuario by remember { mutableStateOf(false) }
    var errorContrasena by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    // Para consultar el servidor sin congelar la pantalla.
    val scope = rememberCoroutineScope()
    var comprobando by remember { mutableStateOf(false) }

    // El botón se pone más claro mientras se revisa la contraseña.
    val colorBoton by animateColorAsState(
        targetValue = if (comprobando) GranateClaro else GranateLogo,
        animationSpec = tween(durationMillis = 250),
        label = "colorBotonLogin"
    )

    // Guardamos el último mensaje de error para que el texto
    // no desaparezca de golpe mientras se anima su salida.
    var ultimoMensaje by remember { mutableStateOf("") }
    LaunchedEffect(mensajeError) {
        mensajeError?.let { ultimoMensaje = it }
    }


    // Esta función se ejecuta al tocar "Iniciar sesión"
    // o al presionar Listo en el teclado del teléfono.
    fun intentarIngresar() {

        // Comprobamos si alguno de los campos quedó vacío.
        errorUsuario = usuario.isBlank()
        errorContrasena = contrasena.isBlank()

        // Si faltan datos, detenemos el proceso y avisamos.
        if (errorUsuario || errorContrasena) {
            mensajeError = "Completa el usuario y la contraseña."
            return
        }

        // Evita enviar dos veces si se toca el botón mientras se comprueba.
        if (comprobando) return
        comprobando = true

        // Se envían el correo y la contraseña al servidor.
        // Se hace en segundo plano para no congelar la pantalla.
        scope.launch {
            val cuenta = AutenticacionDemo.validar(
                usuarioIngresado = usuario,
                contrasenaIngresada = contrasena
            )
            comprobando = false

            if (cuenta == null) {
                // Credenciales erróneas o sin conexión:
                // NO se permite acceder a ninguna pantalla.
                mensajeError = "Usuario o contraseña incorrectos."
            } else {
                // Inicio de sesión correcto: avisamos al contenedor.
                mensajeError = null
                onIngresar(cuenta)
            }
        }
    }


    // DISEÑO VISUAL
    // verticalScroll permite desplazarse si el teclado
    // ocupa buena parte de la pantalla del celular.
    Column(
        modifier = Modifier
            .fillMaxSize()
            // Evita que el login quede detrás de las barras del teléfono.
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Logo del colegio (el archivo está en res/drawable/logo_colegio.png).
        // Se recorta en círculo; Fit muestra la imagen completa para que
        // las letras alrededor del logo no se corten.
        Image(
            painter = painterResource(id = R.drawable.logo_colegio),
            contentDescription = "Logo del Colegio Escritora Marcela Paz",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(TamanoLogo)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFD6DDEA), CircleShape)
        )

        // Título y descripción del acceso a la app.
        Text(
            text = "MPAZ PRO",
            style = MaterialTheme.typography.headlineLarge,
            color = GranateOscuro,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Plataforma educativa · 2.º básico",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Card agrupa el formulario en una tarjeta ordenada.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = CelesteLogo
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "🔐 Iniciar sesión",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = GranateOscuro
                )

                Text("Ingresa con tu correo y tu contraseña")

                // Campo para el nombre de usuario (el correo).
                OutlinedTextField(
                    value = usuario,
                    onValueChange = { nuevoValor ->
                        usuario = nuevoValor
                        errorUsuario = false
                        mensajeError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Usuario") },
                    singleLine = true,
                    isError = errorUsuario,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                // Campo de contraseña: oculta su contenido
                // mientras mostrarContrasena sea falso.
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { nuevoValor ->
                        contrasena = nuevoValor
                        errorContrasena = false
                        mensajeError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    singleLine = true,
                    isError = errorContrasena,
                    visualTransformation = if (mostrarContrasena) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    // El botón Listo también intenta ingresar.
                    keyboardActions = KeyboardActions(
                        onDone = { intentarIngresar() }
                    ),
                    trailingIcon = {
                        TextButton(onClick = {
                            mostrarContrasena = !mostrarContrasena
                        }) {
                            Text(
                                text = if (mostrarContrasena) "Ocultar" else "Ver",
                                color = GranateOscuro
                            )
                        }
                    }
                )

                // MENSAJE DE ERROR ANIMADO =====
                // AnimatedVisibility: el mensaje aparece desplegándose
                // hacia abajo y se va encogiéndose, en vez de saltar.
                AnimatedVisibility(
                    visible = mensajeError != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    label = "mensajeErrorLogin"
                ) {
                    Text(
                        // Mientras sale, mensajeError ya es null:
                        // por eso usamos el último mensaje guardado.
                        text = mensajeError ?: ultimoMensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Ejecuta la validación de todos los campos.
                Button(
                    onClick = { intentarIngresar() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorBoton
                    )
                ) {
                    // TEXTO DEL BOTÓN CON CROSSFADE =====
                    // Mientras se comprueba la clave, el texto se funde
                    // con un circulito de carga y "Comprobando...".
                    Crossfade(
                        targetState = comprobando,
                        label = "textoBotonLogin"
                    ) { estaComprobando ->
                        if (estaComprobando) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                                Text("Comprobando...")
                            }
                        } else {
                            Text("🚀 Iniciar sesión")
                        }
                    }
                }
            }
        }

        // Mostramos las cuentas para facilitar la evaluación.
        // IMPORTANTE: este bloque es solo para una demo.
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    "🧪 Cuentas de prueba",
                    fontWeight = FontWeight.SemiBold
                )
                Text("🧒 Alumno: estudiante@mpaz.cl / 123456")
                Text("🍎 Docente: docente@mpaz.cl / 123456")
                Text("📋 UTP: utp@mpaz.cl / 123456")
                Text(
                    "Acceso demostrativo: no usar datos reales.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}