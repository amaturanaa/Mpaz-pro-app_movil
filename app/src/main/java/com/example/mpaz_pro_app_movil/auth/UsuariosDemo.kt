package com.example.mpaz_pro_app_movil.auth

import android.util.Log
import com.example.mpaz_pro_app_movil.red.LoginRequest
import com.example.mpaz_pro_app_movil.red.RedMpaz
import com.example.mpaz_pro_app_movil.red.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


// MODELOS DE AUTENTICACIÓN
// Enum representa los tres tipos de perfil.
// Cada usuario tendrá exactamente uno de estos roles.
enum class RolUsuario(val nombreVisible: String) {
    ALUMNO("Alumno"),
    DOCENTE("Docente"),
    UTP("UTP")
}

// El usuario que inició sesión. No lleva la contraseña:
// una vez que se entra, la clave ya no se necesita.
data class UsuarioDemo(
    val usuario: String,
    val nombre: String,
    val rol: RolUsuario
)

// INICIO DE SESIÓN CON EL BACKEND (auth-service)
// Se envía el correo y la clave a POST /auth/login del gateway.
// El backend devuelve un token (JWT) con el rol dentro; la app lo
// guarda en Sesion y lo envía en cada petición siguiente.
// El perfil (Alumno, Docente o UTP) NO se elige: lo decide la cuenta.
object AutenticacionDemo {

    private const val ETIQUETA = "Autenticacion"

    // El backend llama ESTUDIANTE al perfil que la app llama ALUMNO.
    private fun rolDesdeBackend(rol: String?): RolUsuario? = when (rol) {
        "UTP" -> RolUsuario.UTP
        "DOCENTE" -> RolUsuario.DOCENTE
        "ESTUDIANTE" -> RolUsuario.ALUMNO
        else -> null
    }

    // Comprueba el correo y la clave en el backend.
    // Si coinciden, entrega el usuario con el rol de su cuenta; si no, null.
    // Trabaja en segundo plano porque usa la red.
    suspend fun validar(
        usuarioIngresado: String,
        contrasenaIngresada: String
    ): UsuarioDemo? = withContext(Dispatchers.IO) {
        try {
            val correo = usuarioIngresado.trim().lowercase()
            val respuesta = RedMpaz.api.login(LoginRequest(correo, contrasenaIngresada))
            val token = respuesta.token

            if (respuesta.status != "ok" || token.isNullOrBlank()) {
                return@withContext null
            }

            // Guardamos el token para que las próximas peticiones lo lleven.
            Sesion.token = token

            // El rol sale de la cuenta (viene dentro del token).
            val rolDeLaCuenta = rolDesdeBackend(Sesion.rol)
            if (rolDeLaCuenta == null) {
                Sesion.cerrar()
                return@withContext null
            }

            UsuarioDemo(
                usuario = correo,
                nombre = correo,
                rol = rolDeLaCuenta
            )
        } catch (error: Exception) {
            Log.e(ETIQUETA, "validar: no se pudo iniciar sesión en el backend", error)
            Sesion.cerrar()
            null
        }
    }
}