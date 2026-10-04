\# MPAZ PRO 2.º



Aplicación móvil Android de apoyo didáctico para estudiantes de Segundo Básico

de la Escuela Escritora Marcela Paz (San Bernardo).



Proyecto de la Evaluación Parcial 2 de la asignatura DSY1105 Desarrollo de

Aplicaciones Móviles, Duoc UC.



\## Integrantes



\- Sofía Mellado

\- Alejandro Maturana



\## Descripción del proyecto



Hoy el colegio trabaja los contenidos de Segundo Básico con guías impresas que

cada docente redacta, imprime, reparte y corrige a mano. Los resultados quedan

en papel y no existe un registro del avance de cada estudiante por objetivo de

aprendizaje.



MPAZ PRO 2.º reemplaza ese proceso por una aplicación que organiza unidades

didácticas de las cuatro asignaturas del nivel, con texto breve, imágenes de

apoyo y audio. La app permite publicar contenidos, asignarlos a un curso, rendir

actividades y pruebas, y revisar el avance de cada estudiante.



Corresponde a un Producto Mínimo Viable (MVP). Es una herramienta de apoyo y no

reemplaza la labor ni la evaluación del docente.



\### Asignaturas incluidas



\- Lenguaje y Comunicación

\- Matemática

\- Ciencias Naturales

\- Historia, Geografía y Ciencias Sociales



\## Perfiles de usuario



\### Jefatura UTP



\- Crea y edita unidades con su objetivo de aprendizaje, explicación y ejemplo guiado.

\- Crea las preguntas de práctica y de prueba final de cada unidad.

\- Controla el estado de cada unidad: Borrador, En revisión, Autorizado,

&#x20; Publicado y Deshabilitado.



\### Docente



\- Consulta las unidades publicadas por la UTP.

\- Asigna unidades al curso y define el rango de fechas de disponibilidad.

\- Configura el número máximo de intentos de la prueba final.

\- Registra observaciones pedagógicas por estudiante.

\- Revisa el informe de rendimiento, visible solo para este perfil.



\### Estudiante



\- Ingresa a su perfil de prueba y ve solo las unidades asignadas.

\- Lee explicaciones breves, ve imágenes de apoyo y escucha el contenido en audio.

\- Resuelve actividades de práctica con respuesta inmediata.

\- Rinde la prueba final dentro de los intentos permitidos.

\- Revisa sus respuestas correctas e incorrectas.

\- No tiene acceso a informes pedagógicos.



\## Funcionalidades implementadas



\- Inicio de sesión con redirección según el perfil del usuario.

\- Navegación entre pantallas con botón para volver y cierre de sesión.

\- Formularios con validación por campo, íconos y mensajes de error visibles.

\- Validaciones centralizadas en la lógica, separadas de los componentes visuales.

\- Gestión de unidades y preguntas con control de estados de publicación.

\- Asignación de unidades con fechas y máximo de intentos.

\- Actividades de práctica y prueba final con resultado detallado.

\- Informe pedagógico reservado al perfil docente.

\- Guardado del progreso en una base de datos local.

\- Animaciones en transiciones de pantalla, mensajes de error y barras de progreso.

\- Uso de recursos nativos del dispositivo.



\## Recursos nativos



| Recurso | Uso en la aplicación |

|---|---|

| Texto a voz | Lee en voz alta las explicaciones y las preguntas al estudiante. |

| Notificaciones | Avisa cuando se asigna una unidad y cuando se guarda una prueba. |



Las notificaciones solicitan permiso en Android 13 o superior. Si el permiso se

niega, la aplicación sigue funcionando sin ese aviso.



\## Tecnologías



\- Kotlin

\- Android Studio

\- Jetpack Compose

\- Material Design 3

\- Arquitectura MVVM

\- SQLite para la persistencia local

\- Git y GitHub para el control de versiones

\- Trello para la planificación



\## Arquitectura



El proyecto sigue el patrón MVVM y separa el código en tres capas:



&#x20;   app/src/main/java/.../

&#x20;   ├── data/

&#x20;   │   ├── model/         Modelos de datos

&#x20;   │   └── repository/    Acceso a la base de datos y a recursos del dispositivo

&#x20;   ├── viewmodel/         Estado de cada pantalla y reglas de validación

&#x20;   └── ui/

&#x20;       ├── screens/       Pantallas de la aplicación

&#x20;       ├── components/    Componentes visuales reutilizables

&#x20;       └── theme/         Colores y tipografía



\- \*\*data:\*\* guarda y entrega la información.

\- \*\*viewmodel:\*\* contiene la lógica y expone el estado a la interfaz.

\- \*\*ui:\*\* muestra el estado y envía los eventos del usuario.



\## Pasos para ejecutar



\### Requisitos



\- Android Studio instalado.

\- Emulador o dispositivo con Android 7.0 (API 24) o superior.



\### Instalación



1\. Clonar el repositorio:



&#x20;      git clone https://github.com/amaturanaa/Mpaz-pro-app\_movil.git



2\. Abrir Android Studio y seleccionar \*\*File > Open\*\*.

3\. Elegir la carpeta `Mpaz-pro-app\\\_movil` y presionar \*\*OK\*\*.

4\. Esperar a que termine \*\*Gradle Sync\*\*.

5\. Seleccionar un emulador o conectar un dispositivo.

6\. Presionar \*\*Run\*\* para ejecutar el módulo `app`.

7\. En Android 13 o superior, aceptar el permiso de notificaciones.



\## Datos y privacidad



Todos los usuarios, cursos, respuestas e informes de la aplicación son

ficticios. No se utilizan nombres, RUT ni credenciales reales de estudiantes o

de la institución. La aplicación no se conecta con sistemas del colegio ni

genera diagnósticos automáticos con inteligencia artificial.



\## Planificación y control de versiones



\- Tablero Trello: ESCRIBIR enlace

\- Repositorio: https://github.com/amaturanaa/Mpaz-pro-app\_movil



Cada integrante trabaja en su propia rama y los cambios se integran a `main`

mediante pull requests revisados por la otra integrante.

