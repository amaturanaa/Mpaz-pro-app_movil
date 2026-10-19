package com.example.mpaz_pro_app_movil.datos

import com.example.mpaz_pro_app_movil.datos.model.Asignacion
import com.example.mpaz_pro_app_movil.datos.model.EstadoUnidad
import com.example.mpaz_pro_app_movil.datos.model.Estudiante
import com.example.mpaz_pro_app_movil.datos.model.Pregunta
import com.example.mpaz_pro_app_movil.datos.model.Unidad

// Datos ficticios con los que se llena la base de datos la primera vez
// que se abre la app (ver RepositorioMpaz). Después, la app trabaja con lo
// que hay guardado en Room y estos datos ya no se vuelven a cargar.
//
// Están pensados para poder mostrar todo el recorrido sin crear nada a mano:
//   - Unidades 1 a 4: publicadas y asignadas, el alumno las ve de inmediato.
//   - Unidad 5: publicada pero sin asignar, para que el docente la asigne.
//   - Unidad 6: autorizada, para que la UTP la publique.
//   - Unidad 7: en revisión.
//   - Unidad 8: borrador.
object DatosPrueba {

    val asignaturas = listOf(
        "Lenguaje y Comunicación",
        "Matemáticas",
        "Ciencias Naturales",
        "Historia, Geografía y Ciencias Sociales"
    )

    val unidades = listOf(
        // PUBLICADAS Y ASIGNADAS
        Unidad(
            id = 1,
            asignatura = "Lenguaje y Comunicación",
            titulo = "Los gerundios",
            codigoOA = "OA-LEN-01",
            objetivo = "Reconocer y usar gerundios en oraciones simples.",
            explicacion = "El gerundio indica una acción que está ocurriendo. Termina en -ando o -iendo.",
            ejemplo = "La niña está leyendo un libro.",
            estado = EstadoUnidad.PUBLICADO
        ),
        Unidad(
            id = 2,
            asignatura = "Matemáticas",
            titulo = "Sumas hasta 20",
            codigoOA = "OA-MAT-02",
            objetivo = "Sumar números hasta 20 contando hacia adelante.",
            explicacion = "Sumar es juntar cantidades. Podemos contar hacia adelante para encontrar el total.",
            ejemplo = "6 + 4 = 10. Desde 6 avanzamos cuatro números: 7, 8, 9 y 10.",
            estado = EstadoUnidad.PUBLICADO
        ),
        Unidad(
            id = 3,
            asignatura = "Ciencias Naturales",
            titulo = "Las plantas y sus necesidades",
            codigoOA = "OA-CIE-03",
            objetivo = "Identificar lo que necesita una planta para vivir.",
            explicacion = "Las plantas son seres vivos. Necesitan agua, luz y aire para crecer.",
            ejemplo = "Una planta cerca de la ventana recibe luz y hay que regarla.",
            estado = EstadoUnidad.PUBLICADO
        ),
        Unidad(
            id = 4,
            asignatura = "Historia, Geografía y Ciencias Sociales",
            titulo = "Nuestra comunidad",
            codigoOA = "OA-HIS-01",
            objetivo = "Reconocer lugares y personas de la comunidad.",
            explicacion = "Una comunidad está formada por personas que comparten lugares, normas y servicios.",
            ejemplo = "La escuela, la plaza y el consultorio son lugares de la comunidad.",
            estado = EstadoUnidad.PUBLICADO
        ),

        // PUBLICADA, TODAVÍA SIN ASIGNAR (el docente la puede asignar)
        Unidad(
            id = 5,
            asignatura = "Lenguaje y Comunicación",
            titulo = "Los sustantivos",
            codigoOA = "OA-LEN-05",
            objetivo = "Reconocer sustantivos que nombran personas, animales y cosas.",
            explicacion = "Los sustantivos son palabras que nombran personas, animales, lugares o cosas.",
            ejemplo = "En «el gato duerme», la palabra gato es un sustantivo.",
            estado = EstadoUnidad.PUBLICADO
        ),

        // EN OTROS ESTADOS (la UTP las puede hacer avanzar)
        Unidad(
            id = 6,
            asignatura = "Matemáticas",
            titulo = "Restas hasta 20",
            codigoOA = "OA-MAT-06",
            objetivo = "Restar números hasta 20 quitando cantidades.",
            explicacion = "Restar es quitar una cantidad de otra. Podemos contar hacia atrás para saber cuánto queda.",
            ejemplo = "10 - 3 = 7. Desde 10 retrocedemos tres números: 9, 8 y 7.",
            estado = EstadoUnidad.AUTORIZADO
        ),
        Unidad(
            id = 7,
            asignatura = "Ciencias Naturales",
            titulo = "El ciclo del agua",
            codigoOA = "OA-CIE-07",
            objetivo = "Describir cómo el agua cambia y se mueve en la naturaleza.",
            explicacion = "El agua de ríos y mares sube al cielo como vapor, forma nubes y vuelve a caer como lluvia.",
            ejemplo = "Después de la lluvia, el sol seca los charcos y el agua vuelve al aire.",
            estado = EstadoUnidad.EN_REVISION
        ),
        Unidad(
            id = 8,
            asignatura = "Historia, Geografía y Ciencias Sociales",
            titulo = "Pueblos originarios de Chile",
            codigoOA = "OA-HIS-08",
            objetivo = "Conocer algunos pueblos originarios que viven en Chile.",
            explicacion = "Los pueblos originarios son los primeros habitantes de nuestro territorio, como los mapuche y los aymara.",
            ejemplo = "Los mapuche viven principalmente en el sur de Chile.",
            estado = EstadoUnidad.BORRADOR
        )
    )

    val preguntas = listOf(
        // UNIDAD 1: LOS GERUNDIOS (las cinco preguntas de la prueba de Lenguaje)
        Pregunta(
            1, 1, "¿Cuál es el gerundio de cantar?",
            listOf("Cantar", "Cantando", "Cantó", "Canta"), 1, true
        ),
        Pregunta(
            2, 1, "La niña está ___ un libro.",
            listOf("Leyendo", "Leer", "Leyó", "Lee"), 0, true
        ),
        Pregunta(
            3, 1, "¿Cuál es el gerundio de correr?",
            listOf("Corría", "Corrió", "Corriendo", "Corre"), 2, true
        ),
        Pregunta(
            4, 1, "Los niños están ___ en el patio.",
            listOf("Jugar", "Jugaron", "Juegan", "Jugando"), 3, true
        ),
        Pregunta(
            5, 1, "¿Cuál es el gerundio de escribir?",
            listOf("Escribiendo", "Escribe", "Escribió", "Escribir"), 0, true
        ),

        // UNIDAD 2: SUMAS HASTA 20
        Pregunta(
            6, 2, "¿Cuánto es 8 + 7?",
            listOf("14", "16", "15", "13"), 2, true
        ),
        Pregunta(
            7, 2, "¿Cuánto es 9 + 6?",
            listOf("14", "15", "16", "17"), 1, true
        ),
        Pregunta(
            8, 2, "¿Cuánto es 10 + 5?",
            listOf("15", "14", "16", "20"), 0, true
        ),

        // UNIDAD 3: LAS PLANTAS
        Pregunta(
            9, 3, "¿Qué necesita una planta para crecer?",
            listOf("Solo oscuridad", "Agua y luz", "Una caja", "Nada"), 1, true
        ),
        Pregunta(
            10, 3, "¿Qué parte de la planta absorbe el agua del suelo?",
            listOf("La raíz", "La flor", "La hoja", "El fruto"), 0, true
        ),
        Pregunta(
            11, 3, "¿Las plantas son seres vivos?",
            listOf("No, son objetos", "Solo de noche", "Sí, nacen y crecen", "Solo las flores"), 2, true
        ),

        // UNIDAD 4: NUESTRA COMUNIDAD
        Pregunta(
            12, 4, "¿Cuál es un lugar de la comunidad?",
            listOf("La plaza", "Un cajón", "Mi mochila", "Un lápiz"), 0, true
        ),
        Pregunta(
            13, 4, "¿Qué lugar atiende a las personas enfermas?",
            listOf("La biblioteca", "El consultorio", "La panadería", "El paradero"), 1, true
        ),
        Pregunta(
            14, 4, "¿Para qué sirven las normas de la comunidad?",
            listOf("Para convivir mejor", "Para pelear", "Para no salir", "Para nada"), 0, true
        ),

        // UNIDAD 5: LOS SUSTANTIVOS
        Pregunta(
            15, 5, "¿Cuál de estas palabras es un sustantivo?",
            listOf("Correr", "Rápido", "Mesa", "Cantando"), 2, true
        ),
        Pregunta(
            16, 5, "Un sustantivo sirve para nombrar ___.",
            listOf("Acciones", "Personas, animales o cosas", "Solo colores", "Solo números"), 1, true
        ),
        Pregunta(
            17, 5, "¿Cuál es el sustantivo en «El perro duerme»?",
            listOf("Perro", "Duerme", "El", "En"), 0, true
        ),

        // UNIDAD 6: RESTAS HASTA 20
        Pregunta(
            18, 6, "¿Cuánto es 10 - 4?",
            listOf("5", "6", "7", "4"), 1, true
        ),
        Pregunta(
            19, 6, "¿Cuánto es 15 - 5?",
            listOf("10", "11", "9", "20"), 0, true
        ),

        // UNIDAD 7: EL CICLO DEL AGUA
        Pregunta(
            20, 7, "¿Qué cae de las nubes cuando llueve?",
            listOf("Arena", "Agua", "Hojas", "Luz"), 1, true
        ),
        Pregunta(
            21, 7, "¿Qué hace el sol con el agua de los charcos?",
            listOf("La evapora", "La congela", "La pinta", "La guarda"), 0, true
        ),

        // UNIDAD 8: PUEBLOS ORIGINARIOS (borrador)
        Pregunta(
            22, 8, "¿Cuál es un pueblo originario de Chile?",
            listOf("Mapuche", "Vikingo", "Romano", "Azteca"), 0, true
        )
    )

    val estudiantes = listOf(
        Estudiante(1, "EST-2B-014", "2° Básico B"),
        Estudiante(2, "EST-2B-015", "2° Básico B")
    )

    // Unidades 1 a 4 asignadas. La 5 queda libre a propósito.
    val asignaciones = listOf(
        Asignacion(1, 1, "2° Básico B", "2026-10-01", "2027-03-31", 3),
        Asignacion(2, 2, "2° Básico B", "2026-10-01", "2027-03-31", 3),
        Asignacion(3, 3, "2° Básico A", "2026-10-01", "2027-03-31", 3),
        Asignacion(4, 4, "2° Básico B", "2026-10-01", "2027-03-31", 3)
    )
}