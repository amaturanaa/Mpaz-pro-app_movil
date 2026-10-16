package com.example.mpaz_pro_app_movil.auth

import android.content.Context
import android.util.Log
import com.example.mpaz_pro_app_movil.datos.BaseDatosMpaz
import com.example.mpaz_pro_app_movil.datos.UsuarioDao
import com.example.mpaz_pro_app_movil.datos.UsuarioEntidad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

// El usuario que inició sesión. No lleva la contraseña:
// una vez que se entra, la clave ya no se necesita.
data class UsuarioDemo(
    val usuario: String,
    val nombre: String,
    val rol: RolUsuario
)

// =============================================
// INICIO DE SESIÓN CON CLAVE CIFRADA
// =============================================
// Las cuentas viven en la tabla "usuarios" de la base de datos local (Room).
// En esa tabla NO está la contraseña: solo su versión cifrada y la sal
// (ver ClaveSegura). No usa internet, Firebase ni un servidor externo.
object AutenticacionDemo {

    private const val ETIQUETA = "Autenticacion"

    // Las tres cuentas ficticias con las que arranca la demostración.
    // Estas contraseñas solo se usan una vez, para crear las cuentas: se
    // cifran antes de guardarse y después ya no se leen de aquí.
    private class CuentaInicial(
        val usuario: String,
        val contrasena: String,
        val nombre: String,
        val rol: RolUsuario
    )

    private val cuentasIniciales = listOf(
        CuentaInicial("alumno", "Alumno123!", "Estudiante de prueba", RolUsuario.ALUMNO),
        CuentaInicial("docente", "Docente123!", "Docente de prueba", RolUsuario.DOCENTE),
        CuentaInicial("utp", "Utp12345!", "Jefatura UTP de prueba", RolUsuario.UTP)
    )

    // Si todavía no hay usuarios guardados, los crea con la clave cifrada.
    private suspend fun crearCuentasIniciales(dao: UsuarioDao) {
        if (dao.cantidad() > 0) return

        val filas = cuentasIniciales.map { cuenta ->
            // Cada usuario tiene su propia sal.
            val sal = ClaveSegura.generarSal()
            UsuarioEntidad(
                usuario = cuenta.usuario.lowercase(),
                nombre = cuenta.nombre,
                rol = cuenta.rol.name,
                sal = sal,
                claveCifrada = ClaveSegura.cifrar(cuenta.contrasena, sal)
            )
        }
        dao.insertar(filas)
        Log.d(ETIQUETA, "cuentas iniciales creadas con clave cifrada")
    }

    // Comprueba el perfil elegido, el usuario y la clave.
    // Si todo coincide, entrega el usuario válido; si algo está mal, null.
    // Trabaja en segundo plano porque cifrar la clave toma un momento.
    suspend fun validar(
        contexto: Context,
        rolSeleccionado: RolUsuario,
        usuarioIngresado: String,
        contrasenaIngresada: String
    ): UsuarioDemo? = withContext(Dispatchers.IO) {
        try {
            val dao = BaseDatosMpaz.obtener(contexto).usuarioDao()
            crearCuentasIniciales(dao)

            val cuenta = dao.buscar(usuarioIngresado.trim().lowercase())
                ?: return@withContext null

            val rolDeLaCuenta = runCatching { RolUsuario.valueOf(cuenta.rol) }.getOrNull()
                ?: return@withContext null

            // El perfil elegido tiene que ser el de la cuenta.
            if (rolDeLaCuenta != rolSeleccionado) return@withContext null

            // La clave escrita se cifra y se compara con la guardada.
            if (!ClaveSegura.coincide(contrasenaIngresada, cuenta.sal, cuenta.claveCifrada)) {
                return@withContext null
            }

            UsuarioDemo(
                usuario = cuenta.usuario,
                nombre = cuenta.nombre,
                rol = rolDeLaCuenta
            )
        } catch (error: Exception) {
            Log.e(ETIQUETA, "validar: no se pudo comprobar la cuenta", error)
            null
        }
    }
}