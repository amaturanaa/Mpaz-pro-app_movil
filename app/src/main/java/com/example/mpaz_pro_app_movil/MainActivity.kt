package com.example.mpaz_pro_app_movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// NUEVO: AnimatedContent anima el cambio entre login y panel.
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mpaz_pro_app_movil.auth.RolUsuario
import com.example.mpaz_pro_app_movil.auth.UsuarioDemo
// NUEVO: transiciones compartidas de la app.
import com.example.mpaz_pro_app_movil.components.Transiciones
import com.example.mpaz_pro_app_movil.screens.LoginScreen
import com.example.mpaz_pro_app_movil.screens.DocenteScreen
import com.example.mpaz_pro_app_movil.screens.EstudianteScreen
import com.example.mpaz_pro_app_movil.screens.UtpScreen
import com.example.mpaz_pro_app_movil.ui.theme.MPAZ_PRO_APP_MOVILTheme
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable


// ACTIVIDAD PRINCIPAL: PUNTO DE ENTRADA DE LA APP
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


// CONTROLADOR DE ACCESO A LAS TRES VISTAS
@Composable
fun AplicacionMPAZ() {

    // null = todavía no hay ninguna sesión iniciada.
    // Al ingresar correctamente, guardamos la cuenta.
    //
    // rememberSaveable (en vez de remember) hace que la sesión
    // sobreviva cuando Android recrea la pantalla, por ejemplo al
    // girar el celular. Si se cierra la app del todo, se vuelve a
    // pedir ingreso. Nunca se guarda la clave, solo usuario, nombre y rol.
    var usuarioActivo by rememberSaveable(stateSaver = SaverUsuarioDemo) {
        mutableStateOf<UsuarioDemo?>(null)
    }

    // =============================================
    // NUEVO: TRANSICIÓN ANIMADA LOGIN <-> PANEL
    // =============================================
    // AnimatedContent observa usuarioActivo. Cada vez que cambia
    // (alguien inicia o cierra sesión), dibuja la pantalla nueva
    // con una animación mientras la anterior se va.
    AnimatedContent(
        targetState = usuarioActivo,
        modifier = Modifier.fillMaxSize(),

        // contentKey reemplaza al antiguo key(usuarioSesion.usuario):
        // si cambia el nombre de usuario se considera otra pantalla
        // y su estado visual parte desde cero.
        contentKey = { usuario -> usuario?.usuario },

        // targetState = hacia dónde vamos.
        // Si hay usuario, estamos entrando; si es null, saliendo.
        transitionSpec = {
            if (targetState != null) {
                Transiciones.entrarSesion()
            } else {
                Transiciones.salirSesion()
            }
        },
        label = "transicionSesion"
    ) { usuarioSesion ->
        // usuarioSesion es el valor que corresponde a ESTA pantalla.
        // Dentro del bloque else Kotlin sabe que NO es nulo,
        // así que seguimos sin usar !!.

        if (usuarioSesion == null) {

            // Sin sesión: solo se muestra el formulario.
            LoginScreen(onIngresar = { usuarioValidado ->
                usuarioActivo = usuarioValidado
            })

        } else {

            when (usuarioSesion.rol) {

                // ALUMNO: conserva la pantalla original
                // con unidades, las cuatro pruebas y avance.
                RolUsuario.ALUMNO -> EstudianteScreen(
                    onCerrarSesion = { usuarioActivo = null }
                )

                // DOCENTE: panel con unidades publicadas, asignaciones e informes.
                RolUsuario.DOCENTE -> DocenteScreen(
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

// Convierte el usuario en una lista simple de textos para guardarla
// y lo reconstruye al volver. null = sin sesión.
private val SaverUsuarioDemo = listSaver<UsuarioDemo?, String>(
    save = { usuario ->
        if (usuario == null) emptyList()
        else listOf(usuario.usuario, usuario.nombre, usuario.rol.name)
    },
    restore = { datos ->
        if (datos.size == 3) {
            UsuarioDemo(
                usuario = datos[0],
                nombre = datos[1],
                rol = RolUsuario.valueOf(datos[2])
            )
        } else {
            null
        }
    }
)