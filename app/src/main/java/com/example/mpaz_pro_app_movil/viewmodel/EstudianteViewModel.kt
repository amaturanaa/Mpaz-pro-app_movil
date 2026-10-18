package com.example.mpaz_pro_app_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.datos.repositorio.AlmacenMpaz

// =============================================
// MOLDE DE UNA PRUEBA DEL MENÚ DEL ALUMNO
// =============================================
// titulo      -> nombre grande de la tarjeta (ej: "Lenguaje")
// descripcion -> texto pequeño de abajo (ej: "Gerundios · 5 preguntas")
data class PruebaAlumno(
    val titulo: String,
    val descripcion: String
)

// =============================================
// VIEWMODEL DEL PERFIL ALUMNO
// =============================================
// Sigue la misma idea que UtpViewModel y DocenteViewModel:
//   - La PANTALLA (EstudianteScreen) solo dibuja y avisa lo que el niño tocó.
//   - El VIEWMODEL (este archivo) decide QUÉ datos se muestran.
//   - El MODELO (AlmacenMpaz + Room) guarda los datos en el teléfono.
// Con esto los tres perfiles quedan con MVVM.
//
// AndroidViewModel recibe la "application" porque AlmacenMpaz
// necesita un contexto para abrir la base de datos.
class EstudianteViewModel(application: Application) : AndroidViewModel(application) {

    // Al crear el ViewModel cargamos la base de datos.
    // Antes esto se hacía dentro de la pantalla; ahora vive aquí.
    // Si ya estaba cargada (por UTP o Docente), no hace nada.
    init {
        AlmacenMpaz.iniciar(application)
    }

    // =============================================
    // UNIDADES PUBLICADAS
    // =============================================
    // El alumno solo ve las unidades que la UTP dejó en PUBLICADO.
    // Es un "get()": se calcula cada vez que la pantalla lo pide.
    // Como AlmacenMpaz.unidades es una lista observable de Compose,
    // si la UTP publica o deshabilita algo, la pantalla se actualiza sola.
    //
    // PENDIENTE (tarea 3): aquí mismo se agregará el filtro para mostrar
    // solo las unidades que el docente asignó.
    val unidadesPublicadas: List<Unidad>
        get() = AlmacenMpaz.unidades.filter { unidad ->
            unidad.estado == EstadoUnidad.PUBLICADO
        }

    // =============================================
    // ASIGNATURAS DE 2.º BÁSICO
    // =============================================
    // Antes esta lista estaba escrita dentro de la pantalla.
    // El orden importa: la pantalla le da un color a cada posición.
    val asignaturas: List<String> = listOf(
        "Lenguaje y Comunicación",
        "Matemáticas",
        "Ciencias Naturales",
        "Historia, Geografía y Ciencias Sociales"
    )

    // =============================================
    // MENÚ DE LAS CUATRO PRUEBAS
    // =============================================
    // El orden también importa: la posición es el número de la prueba.
    // 0 = Lenguaje, 1 = Matemática, 2 = Ciencias, 3 = Historia.
    val pruebas: List<PruebaAlumno> = listOf(
        PruebaAlumno("Lenguaje", "Gerundios · 5 preguntas"),
        PruebaAlumno("Matemática", "Números y operaciones · 5 preguntas"),
        PruebaAlumno("Ciencias Naturales", "Seres vivos y naturaleza · 5 preguntas"),
        PruebaAlumno("Historia", "Chile y su entorno · 5 preguntas")
    )
}