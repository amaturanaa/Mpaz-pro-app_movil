package com.example.mpaz_pro_app_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.mpaz_pro_app_movil.auth.RolUsuario
import com.example.mpaz_pro_app_movil.auth.UsuarioDemo
import com.example.mpaz_pro_app_movil.screens.LoginScreen
import com.example.mpaz_pro_app_movil.screens.ProfesorScreen
import com.example.mpaz_pro_app_movil.ui.theme.MPAZ_PRO_APP_MOVILTheme

// =============================================
// ACTIVIDAD PRINCIPAL: PUNTO DE ENTRADA DE LA APP
// =============================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Jetpack Compose dibuja toda la aplicación aquí.
        setContent {
            MPAZ_PRO_APP_MOVILTheme {
                AplicacionMPAZ()
            }
        }
    }
}

// =============================================
// CONTROLADOR DE ACCESO A LAS TRES VISTAS
// =============================================
@Composable
fun AplicacionMPAZ() {

    // null = todavía no hay ninguna sesión iniciada.
    // Al ingresar correctamente, guardamos la cuenta.
    // El valor se guarda solo EN MEMORIA para la demo;
    // cuando se cierre la aplicación se pedirá ingreso otra vez.
    var usuarioActivo by remember {
        mutableStateOf<UsuarioDemo?>(null)
    }

    // Evitamos utilizar !! gracias a ?.let y a la variable
    // usuarioSesion, que dentro del bloque NO es nula.
    val usuarioSesion = usuarioActivo

    if (usuarioSesion == null) {

        // Sin sesión: solo se muestra el formulario.
        LoginScreen(onIngresar = { usuarioValidado ->
            usuarioActivo = usuarioValidado
        })

    } else {

        // key reinicia el estado visual de cada pantalla
        // si cambia el usuario de una sesión a otra.
        key(usuarioSesion.usuario) {
            when (usuarioSesion.rol) {

                // ALUMNO: conserva la pantalla original
                // con unidades, las cuatro pruebas y avance.
                RolUsuario.ALUMNO -> EstudianteScreen(
                    onCerrarSesion = { usuarioActivo = null }
                )

                // DOCENTE: permite abrir su panel y salir.
                RolUsuario.DOCENTE -> ProfesorScreen(
                    onCerrarSesion = { usuarioActivo = null }
                )

                // UTP: abre el panel UTP que ya existía.
                RolUsuario.UTP -> UtpScreen(
                    onCerrarSesion = { usuarioActivo = null }
                )
            }
        }
    }
}
