package com.example.mpaz_pro_app_movil.components


import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween


// TRANSICIONES COMPARTIDAS DE LA APP
// Las dejamos en un solo lugar para que TODAS las pantallas
// se muevan igual (alumno, login, y también UTP y Docente
// si Ale las quiere reutilizar). Si se quiere cambiar la
// velocidad de toda la app, basta con cambiar DURACION.
object Transiciones {

    // Milisegundos que dura cada transición.
    // 300 ms es rápido, pero el niño alcanza a ver el movimiento.
    const val DURACION = 300

    // AVANZAR: se usa al entrar a algo (abrir una unidad, una prueba,
    // o pasar a una pestaña que está más a la derecha).
    // La pantalla nueva llega desde la DERECHA y la vieja se va a la IZQUIERDA.
    // "ancho / 3" hace que se deslice solo un tercio: es más suave
    // que recorrer toda la pantalla.
    fun avanzar(): ContentTransform =
        (slideInHorizontally(animationSpec = tween(DURACION)) { ancho -> ancho / 3 } +
                fadeIn(animationSpec = tween(DURACION))) togetherWith
                (slideOutHorizontally(animationSpec = tween(DURACION)) { ancho -> -ancho / 3 } +
                        fadeOut(animationSpec = tween(DURACION)))

    // RETROCEDER: lo contrario de avanzar (botón Volver, pestaña a la izquierda).
    // La pantalla nueva llega desde la IZQUIERDA y la vieja se va a la DERECHA.
    fun retroceder(): ContentTransform =
        (slideInHorizontally(animationSpec = tween(DURACION)) { ancho -> -ancho / 3 } +
                fadeIn(animationSpec = tween(DURACION))) togetherWith
                (slideOutHorizontally(animationSpec = tween(DURACION)) { ancho -> ancho / 3 } +
                        fadeOut(animationSpec = tween(DURACION)))

    // Atajo: elige avanzar o retroceder según la dirección.
    fun lateral(haciaAdelante: Boolean): ContentTransform =
        if (haciaAdelante) avanzar() else retroceder()

    // ENTRAR A LA SESIÓN: del login al panel del usuario.
    // El panel aparece creciendo desde 92 % de su tamaño
    // mientras el login se desvanece.
    fun entrarSesion(): ContentTransform =
        (fadeIn(animationSpec = tween(DURACION)) +
                scaleIn(animationSpec = tween(DURACION), initialScale = 0.92f)) togetherWith
                fadeOut(animationSpec = tween(DURACION))

    // CERRAR SESIÓN: del panel vuelve al login (como "retroceder").
    fun salirSesion(): ContentTransform = retroceder()
}