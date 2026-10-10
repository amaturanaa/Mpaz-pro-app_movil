package com.example.mpaz_pro_app_movil.screens

// =============================================
// IMPORTACIONES PARA EL FORMULARIO DE INGRESO
// =============================================
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mpaz_pro_app_movil.auth.AutenticacionDemo
import com.example.mpaz_pro_app_movil.auth.RolUsuario
import com.example.mpaz_pro_app_movil.auth.UsuarioDemo

// =============================================
// PANTALLA DE INICIO DE SESIÓN
// =============================================
// onIngresar es una función que recibe al usuario
// autenticado y se lo comunica a MainActivity.
@Composable
fun LoginScreen(onIngresar: (UsuarioDemo) -> Unit) {

    // Rol que se elige tocando Alumno, Docente o UTP.
    var rolSeleccionado by remember { mutableStateOf(RolUsuario.ALUMNO) }

    // Estos campos se actualizan al escribir en el formulario.
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // Controla si mostramos la contraseña con letras o puntos.
    var mostrarContrasena by remember { mutableStateOf(false) }

    // Variables para explicar los errores al usuario.
    var errorUsuario by remember { mutableStateOf(false) }
    var errorContrasena by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

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

        // Buscamos la cuenta que coincida con el rol elegido,
        // el usuario y la contraseña exacta.
        val cuenta = AutenticacionDemo.validar(
            rolSeleccionado = rolSeleccionado,
            usuarioIngresado = usuario,
            contrasenaIngresada = contrasena
        )

        if (cuenta == null) {
            // Credenciales erróneas o perfil incorrecto:
            // NO se permite acceder a ninguna pantalla.
            mensajeError = "Perfil, usuario o contraseña incorrectos."
        } else {
            // Inicio de sesión correcto: avisamos al contenedor.
            mensajeError = null
            onIngresar(cuenta)
        }
    }

    // =============================================
    // DISEÑO VISUAL
    // =============================================
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

        // Título y descripción del acceso a la app.
        Text(
            text = "MPAZ PRO",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF285CB2),
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
                containerColor = Color(0xFFEDF5FF)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C365F)
                )

                Text("Selecciona tu tipo de usuario")

                // Se crea una opción por cada rol.
                // Solo una puede quedar seleccionada.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RolUsuario.entries.forEach { rol ->
                        FilterChip(
                            modifier = Modifier.weight(1f),
                            selected = rolSeleccionado == rol,
                            onClick = {
                                // Seleccionamos el perfil y borramos
                                // mensajes de error anteriores.
                                rolSeleccionado = rol
                                mensajeError = null
                            },
                            label = { Text(rol.nombreVisible) }
                        )
                    }
                }

                // Campo para el nombre de usuario.
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
                            Text(if (mostrarContrasena) "Ocultar" else "Ver")
                        }
                    }
                )

                // El mensaje solo aparece cuando hay un error.
                mensajeError?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Ejecuta la validación de todos los campos.
                Button(
                    onClick = { intentarIngresar() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3169C6)
                    )
                ) {
                    Text("Iniciar sesión")
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
                    "Cuentas de prueba",
                    fontWeight = FontWeight.SemiBold
                )
                Text("Alumno: alumno / Alumno123!")
                Text("Docente: docente / Docente123!")
                Text("UTP: utp / Utp12345!")
                Text(
                    "Acceso demostrativo: no usar datos reales.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
