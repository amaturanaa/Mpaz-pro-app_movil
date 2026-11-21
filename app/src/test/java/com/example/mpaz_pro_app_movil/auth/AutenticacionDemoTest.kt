package com.example.mpaz_pro_app_movil.auth

import android.util.Base64
import com.example.mpaz_pro_app_movil.red.LoginRequest
import com.example.mpaz_pro_app_movil.red.LoginRespuesta
import com.example.mpaz_pro_app_movil.red.RedMpaz
import com.example.mpaz_pro_app_movil.red.Sesion
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

class AutenticacionDemoTest {

    private val api = mockk<com.example.mpaz_pro_app_movil.red.ApiMpaz>()

    @Before
    fun preparar() {
        Sesion.cerrar()
        mockkObject(RedMpaz)
        every { RedMpaz.api } returns api
        // El rol viene dentro del token. Aquí se simula lo que lee Sesion.
        mockkStatic(Base64::class)
    }

    @After
    fun limpiar() {
        Sesion.cerrar()
        unmockkAll()
    }

    private fun elTokenTraeElRol(rol: String) {
        every { Base64.decode(any<String>(), any()) } returns
                """{"sub":"cuenta@mpaz.cl","role":"$rol"}""".toByteArray()
    }

    @Test
    fun validar_conCuentaUtp_devuelveUsuarioConRolUtp() = runTest {
        elTokenTraeElRol("UTP")
        coEvery { api.login(LoginRequest("utp@mpaz.cl", "123456")) } returns
                LoginRespuesta("ok", "cabecera.carga.firma")

        // El correo se limpia: sin espacios y en minúsculas
        val usuario = AutenticacionDemo.validar("  UTP@Mpaz.cl ", "123456")

        assertEquals(UsuarioDemo("utp@mpaz.cl", "utp@mpaz.cl", RolUsuario.UTP), usuario)
        assertEquals("cabecera.carga.firma", Sesion.token)
    }

    @Test
    fun validar_conCuentaDocente_devuelveRolDocente() = runTest {
        elTokenTraeElRol("DOCENTE")
        coEvery { api.login(any()) } returns LoginRespuesta("ok", "cabecera.carga.firma")

        assertEquals(RolUsuario.DOCENTE, AutenticacionDemo.validar("docente@mpaz.cl", "123456")?.rol)
    }

    @Test
    fun validar_conCuentaEstudiante_devuelveRolAlumno() = runTest {
        // El backend dice ESTUDIANTE y la app dice ALUMNO
        elTokenTraeElRol("ESTUDIANTE")
        coEvery { api.login(any()) } returns LoginRespuesta("ok", "cabecera.carga.firma")

        assertEquals(RolUsuario.ALUMNO, AutenticacionDemo.validar("alumno@mpaz.cl", "123456")?.rol)
    }

    @Test
    fun validar_conClaveIncorrecta_devuelveNullYNoGuardaToken() = runTest {
        coEvery { api.login(any()) } returns LoginRespuesta("error", null)

        assertNull(AutenticacionDemo.validar("utp@mpaz.cl", "mala"))
        assertNull(Sesion.token)
    }

    @Test
    fun validar_siElTokenViene_enBlanco_devuelveNull() = runTest {
        coEvery { api.login(any()) } returns LoginRespuesta("ok", "   ")

        assertNull(AutenticacionDemo.validar("utp@mpaz.cl", "123456"))
        assertNull(Sesion.token)
    }

    @Test
    fun validar_conRolDesconocido_devuelveNullYCierraLaSesion() = runTest {
        elTokenTraeElRol("INVITADO")
        coEvery { api.login(any()) } returns LoginRespuesta("ok", "cabecera.carga.firma")

        assertNull(AutenticacionDemo.validar("raro@mpaz.cl", "123456"))
        assertNull(Sesion.token)
    }

    @Test
    fun validar_sinConexion_devuelveNullYCierraLaSesion() = runTest {
        coEvery { api.login(any()) } throws IOException("sin conexión")

        assertNull(AutenticacionDemo.validar("utp@mpaz.cl", "123456"))
        assertNull(Sesion.token)
    }
}