package com.example.mpaz_pro_app_movil.auth

// =============================================
// MODELOS DE AUTENTICACIÓN PARA LA DEMOSTRACIÓN
// =============================================

// Enum representa los tres tipos de perfil.
// Cada usuario tendrá exactamente uno de estos roles.
enum class RolUsuario(val nombreVisible: String) {
    ALUMNO("Alumno"),
    DOCENTE("Docente"),
    UTP("UTP")
}

// Esta clase agrupa los datos de una cuenta ficticia.
// Las contraseñas son EXCLUSIVAMENTE para probar la app.
// En un sistema real nunca guardaríamos claves así.
data class UsuarioDemo(
    val usuario: String,
    val contrasena: String,
    val nombre: String,
    val rol: RolUsuario
)

// Este objeto simula una base de datos de usuarios.
// No usa internet, Firebase ni un servidor externo.
object AutenticacionDemo {

    // Tres usuarios ficticios, uno por cada perfil.
    // Se pueden cambiar estos valores para la demostración.
    val cuentas = listOf(
        UsuarioDemo(
            usuario = "alumno",
            contrasena = "Alumno123!",
            nombre = "Estudiante de prueba",
            rol = RolUsuario.ALUMNO
        ),
        UsuarioDemo(
            usuario = "docente",
            contrasena = "Docente123!",
            nombre = "Docente de prueba",
            rol = RolUsuario.DOCENTE
        ),
        UsuarioDemo(
            usuario = "utp",
            contrasena = "Utp12345!",
            nombre = "Jefatura UTP de prueba",
            rol = RolUsuario.UTP
        )
    )

    // Compara el perfil seleccionado, el usuario y la clave.
    // Si todo coincide, entrega el usuario válido.
    // Si algo está mal, devuelve null.
    fun validar(
        rolSeleccionado: RolUsuario,
        usuarioIngresado: String,
        contrasenaIngresada: String
    ): UsuarioDemo? {
        return cuentas.firstOrNull { cuenta ->
            cuenta.rol == rolSeleccionado &&
                    cuenta.usuario.equals(usuarioIngresado.trim(), ignoreCase = true) &&
                    cuenta.contrasena == contrasenaIngresada
        }
    }
}