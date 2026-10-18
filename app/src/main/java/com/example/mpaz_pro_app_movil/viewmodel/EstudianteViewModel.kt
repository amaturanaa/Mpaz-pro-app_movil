package com.example.mpaz_pro_app_movil.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Unidad
import com.example.mpaz_pro_app_movil.datos.repositorio.AlmacenMpaz
// NUEVO (tarea 3): para mostrar la fecha de término como dd/mm/aaaa.
import com.example.mpaz_pro_app_movil.validacion.Fechas

// =============================================
// NUEVO (tarea 3): UNA UNIDAD QUE EL DOCENTE ASIGNÓ
// =============================================
// Junta la unidad con los datos de su asignación, para que la
// pantalla pueda mostrar "Curso: ..." y "Disponible hasta: ...".
// unidad     -> la unidad publicada por la UTP
// cursos     -> los cursos a los que el docente la asignó (ej: "2° Básico B")
// fechaHasta -> la fecha de término más lejana, ya en formato dd/mm/aaaa
data class UnidadAsignada(
    val unidad: Unidad,
    val cursos: List<String>,
    val fechaHasta: String
)

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
    // NUEVO (tarea 3): UNIDADES QUE VE EL ALUMNO
    // =============================================
    // Decisión A del plan: el alumno solo ve una unidad si cumple DOS reglas:
    //   1. La UTP la dejó en PUBLICADO.
    //   2. El docente la asignó al menos a un curso.
    // Como el alumno de prueba no tiene curso, sirve cualquier curso.
    //
    // Así el flujo completo tiene sentido: UTP publica -> Docente asigna
    // (y llega la notificación) -> recién ahí el alumno la ve.
    //
    // Es un "get()": se calcula cada vez que la pantalla lo pide.
    // AlmacenMpaz.unidades y AlmacenMpaz.asignaciones son listas observables
    // de Compose: si la UTP deshabilita o el docente asigna, la lista
    // del alumno se actualiza sola.
    val unidadesAsignadas: List<UnidadAsignada>
        get() = AlmacenMpaz.unidades
            // Regla 1: solo publicadas.
            .filter { unidad -> unidad.estado == EstadoUnidad.PUBLICADO }
            // Regla 2: buscamos sus asignaciones. mapNotNull descarta
            // (devuelve null) las unidades que no tienen ninguna.
            .mapNotNull { unidad ->
                val asignacionesDeLaUnidad = AlmacenMpaz.asignaciones.filter { asignacion ->
                    asignacion.unidadId == unidad.id
                }
                if (asignacionesDeLaUnidad.isEmpty()) {
                    null
                } else {
                    UnidadAsignada(
                        unidad = unidad,
                        // distinct() evita repetir un curso.
                        cursos = asignacionesDeLaUnidad.map { it.curso }.distinct(),
                        // Las fechas se guardan como año-mes-día (ej: 2026-12-15),
                        // así que la "mayor" como texto es también la más lejana.
                        fechaHasta = Fechas.aVisible(
                            asignacionesDeLaUnidad.maxOf { it.fechaFin }
                        )
                    )
                }
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