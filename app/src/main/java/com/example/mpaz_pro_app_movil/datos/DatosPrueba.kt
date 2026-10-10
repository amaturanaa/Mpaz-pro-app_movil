package com.example.mpaz_pro_app_movil.datos

// Datos ficticios compartidos por toda la app mientras no exista la base de datos.
// La UTP, el docente y el estudiante leen de aquí, así todos ven lo mismo.
// En la fase 2 se reemplazan por los datos guardados.
object DatosPrueba {

    val asignaturas = listOf(
        "Lenguaje y Comunicación",
        "Matemáticas",
        "Ciencias Naturales",
        "Historia, Geografía y Ciencias Sociales"
    )

    val unidades = listOf(
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
            estado = EstadoUnidad.AUTORIZADO
        ),
        Unidad(
            id = 3,
            asignatura = "Ciencias Naturales",
            titulo = "Las plantas y sus necesidades",
            codigoOA = "OA-CIE-03",
            objetivo = "Identificar lo que necesita una planta para vivir.",
            explicacion = "Las plantas son seres vivos. Necesitan agua, luz y aire para crecer.",
            ejemplo = "Una planta cerca de la ventana recibe luz y hay que regarla.",
            estado = EstadoUnidad.EN_REVISION
        ),
        Unidad(
            id = 4,
            asignatura = "Historia, Geografía y Ciencias Sociales",
            titulo = "Nuestra comunidad",
            codigoOA = "OA-HIS-01",
            objetivo = "Reconocer lugares y personas de la comunidad.",
            explicacion = "Una comunidad está formada por personas que comparten lugares, normas y servicios.",
            ejemplo = "La escuela, la plaza y el consultorio son lugares de la comunidad.",
            estado = EstadoUnidad.BORRADOR
        )
    )

    val preguntas = listOf(
        // UNIDAD 1: LOS GERUNDIOS (las cinco preguntas de la prueba de Sofi)
        Pregunta(1, 1, "¿Cuál es el gerundio de cantar?",
            listOf("Cantar", "Cantando", "Cantó", "Canta"), 1, true),
        Pregunta(2, 1, "La niña está ___ un libro.",
            listOf("Leyendo", "Leer", "Leyó", "Lee"), 0, true),
        Pregunta(3, 1, "¿Cuál es el gerundio de correr?",
            listOf("Corría", "Corrió", "Corriendo", "Corre"), 2, true),
        Pregunta(4, 1, "Los niños están ___ en el patio.",
            listOf("Jugar", "Jugaron", "Juegan", "Jugando"), 3, true),
        Pregunta(5, 1, "¿Cuál es el gerundio de escribir?",
            listOf("Escribiendo", "Escribe", "Escribió", "Escribir"), 0, true),

        // UNIDAD 2: SUMAS HASTA 20
        Pregunta(6, 2, "¿Cuánto es 8 + 7?",
            listOf("14", "16", "15", "13"), 2, true),

        // UNIDAD 3: LAS PLANTAS
        Pregunta(7, 3, "¿Qué necesita una planta para crecer?",
            listOf("Solo oscuridad", "Agua y luz", "Una caja", "Nada"), 1, true),

        // UNIDAD 4: NUESTRA COMUNIDAD
        Pregunta(8, 4, "¿Cuál es un lugar de la comunidad?",
            listOf("La plaza", "Un cajón", "Mi mochila", "Un lápiz"), 0, true)
    )

    val estudiantes = listOf(
        Estudiante(1, "EST-2B-014", "2° Básico B"),
        Estudiante(2, "EST-2B-015", "2° Básico B")
    )

    val asignaciones = listOf(
        Asignacion(1, 1, "2° Básico B", "2026-10-01", "2026-12-15", 3)
    )
}