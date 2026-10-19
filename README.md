# MPAZ PRO 2.º

Aplicación móvil Android de apoyo didáctico para estudiantes de Segundo Básico
de la Escuela Escritora Marcela Paz (San Bernardo).

Proyecto de la Evaluación Parcial 2 de la asignatura DSY1105 Desarrollo de
Aplicaciones Móviles, Duoc UC.

## Integrantes

| Integrante |
|---|
| Alejandro Maturana |
| Sofía Mellado |

## Descripción del proyecto

Hoy el colegio trabaja los contenidos de Segundo Básico con guías impresas que
cada docente redacta, imprime, reparte y corrige a mano. Los resultados quedan
en papel y no existe un registro del avance de cada estudiante por objetivo de
aprendizaje.

MPAZ PRO 2.º reemplaza ese proceso por una aplicación que organiza unidades
didácticas de las cuatro asignaturas del nivel, con explicaciones breves y
lectura en voz alta. La app permite publicar contenidos, asignarlos a un curso,
rendir pruebas y revisar los resultados.

Corresponde a un Producto Mínimo Viable (MVP). Es una herramienta de apoyo y no
reemplaza la labor ni la evaluación del docente.

### Asignaturas incluidas

- Lenguaje y Comunicación
- Matemática
- Ciencias Naturales
- Historia, Geografía y Ciencias Sociales

## Perfiles de usuario

### Jefatura UTP

- Crea y edita unidades con su código OA, objetivo de aprendizaje, explicación
  y ejemplo.
- Crea y elimina las preguntas de cada unidad (cuatro alternativas, una correcta).
- Controla el estado de cada unidad: Borrador, En revisión, Autorizado,
  Publicado y Deshabilitado.

### Docente

- Consulta solo las unidades que la UTP ya publicó.
- Asigna unidades a un curso y define el rango de fechas de disponibilidad.
- Configura el número máximo de intentos (de 1 a 3), que se aplica a la prueba
  del estudiante.
- Decide si el estudiante puede revisar sus respuestas al terminar la prueba.
- Decide si se avisa o no al curso. Cuando se avisa, se envía una notificación.
- Revisa el informe de intentos, notas y promedio, visible solo para este perfil.
- Registra observaciones pedagógicas por estudiante.

### Estudiante

- Ingresa a su perfil de prueba.
- Ve solo las unidades que el docente le asignó. Una unidad publicada por la UTP
  que todavía no fue asignada no aparece.
- Lee las explicaciones de cada asignatura y las escucha en voz alta.
- Rinde la prueba final de cada asignatura dentro de los intentos permitidos.
- Recibe su nota en escala de 1,0 a 7,0. La opción de revisar las respuestas
  aparece solo si el docente la habilitó al asignar la unidad.
- Realiza una actividad con la cámara en Ciencias Naturales (animales).
- Ve su avance: mejor nota, promedio e intentos usados en cada asignatura.
- No tiene acceso a informes pedagógicos.

### Cuentas de prueba

Al iniciar sesión se elige el perfil y se ingresa el usuario y la contraseña.
Son datos ficticios, solo para probar la aplicación.

| Perfil | Usuario | Contraseña |
|---|---|---|
| Alumno | `alumno` | `Alumno123!` |
| Docente | `docente` | `Docente123!` |
| UTP | `utp` | `Utp12345!` |

## Funcionalidades implementadas

- **Inicio de sesión** con redirección según el perfil del usuario y cierre de
  sesión. La sesión se mantiene al girar el teléfono.
- **Contraseñas cifradas**: las claves no se guardan en texto plano (ver
  [Seguridad de las contraseñas](#seguridad-de-las-contraseñas)).
- **Navegación** entre pantallas: barra inferior, menú lateral y botón para volver.
- **Formularios con validación por campo**, ícono, indicador de campo correcto y
  mensaje de error visible: unidad, pregunta, asignación y observación.
- **Validaciones centralizadas** en un solo objeto (`Validador`) y en `Fechas`,
  separadas de los componentes visuales.
- **Gestión de unidades y preguntas** con control de estados de publicación.
- **Asignación de unidades** con fechas, máximo de intentos y permiso para
  revisar respuestas.
- **Pruebas finales** con intentos limitados, nota en escala chilena y revisión
  de respuestas.
- **Informe pedagógico** reservado al perfil docente.
- **Persistencia local con Room**: lo que se crea sobrevive al cerrar la app.
- **Animaciones**: transición entre el inicio de sesión y cada panel, entre las
  pestañas de los tres perfiles y entre la lista, el detalle y la edición de
  unidades de la UTP. También hay animaciones en los mensajes de error, en los
  íconos de los formularios y en la barra de progreso de la actividad con cámara.
- **Recursos nativos** del dispositivo (ver más abajo).

### Reglas que la aplicación hace cumplir

| Regla | Dónde se aplica |
|---|---|
| Una unidad sin preguntas no puede pasar a revisión | UTP |
| Los estados siguen el orden Borrador → En revisión → Autorizado → Publicado → Deshabilitado; no se pueden saltar pasos | UTP |
| Una unidad publicada no se puede editar ni cambiar sus preguntas | UTP |
| El código OA debe tener el formato `OA-XXX-00` y no puede repetirse | UTP |
| Solo se pueden asignar unidades publicadas | Docente |
| No se puede asignar la misma unidad dos veces al mismo curso | Docente |
| La fecha de término no puede ser anterior a la de inicio ni a la de hoy | Docente |
| El máximo de intentos va de 1 a 3 | Docente |
| El estudiante solo ve las unidades que fueron asignadas | Estudiante |
| Cuando el estudiante agota sus intentos, no puede volver a rendir la prueba | Estudiante |
| Solo se puede comentar una unidad que ya fue asignada | Docente |
| Los informes solo se ven en el perfil docente y muestran códigos de estudiante, nunca nombres ni RUT | Docente |

### Límites de los campos de texto

| Campo | Largo permitido |
|---|---|
| Título de la unidad | 5 a 60 caracteres |
| Objetivo de aprendizaje | 10 a 150 caracteres |
| Explicación | 20 a 300 caracteres |
| Ejemplo | 5 a 200 caracteres |
| Pregunta | 10 a 150 caracteres |
| Alternativa | 1 a 60 caracteres |

## Recursos nativos

| Recurso | Uso en la aplicación |
|---|---|
| Notificaciones | Avisa cuando el docente asigna una unidad a un curso. |
| Texto a voz | Lee en voz alta las explicaciones y los ejemplos al estudiante, tanto de las asignaturas como de las unidades publicadas por la UTP. |
| Cámara | En la actividad de animales de Ciencias Naturales, el estudiante toma fotografías con la app de cámara del teléfono. |

Las notificaciones solicitan permiso en Android 13 o superior. Si el permiso se
niega, la aplicación sigue funcionando sin ese aviso. La cámara se abre con la
aplicación de cámara instalada en el teléfono y entrega la foto mediante un
`FileProvider`, por eso no requiere el permiso `CAMERA`.

## Persistencia de datos

Los datos de la aplicación se guardan en una base de datos **Room** (SQLite),
`BaseDatosMpaz`, versión 3, con cinco tablas:

| Tabla | Contenido |
|---|---|
| `usuarios` | Cuentas de acceso, con la contraseña cifrada |
| `unidades` | Unidades didácticas y su estado |
| `preguntas` | Preguntas de cada unidad |
| `asignaciones` | Unidad asignada a un curso, con fechas, máximo de intentos y permiso para revisar respuestas |
| `observaciones` | Comentarios del docente sobre un estudiante |

La primera vez que se abre la aplicación se cargan cuentas, unidades y preguntas
de prueba. Después, todo lo que se crea o se modifica se guarda de inmediato.

Los intentos usados y las notas de las pruebas del estudiante se guardan en
`SharedPreferences`, un archivo por asignatura.

## Seguridad de las contraseñas

Las contraseñas **no se guardan en texto plano**. Al crear una cuenta se
calcula un hash con **PBKDF2** (HMAC-SHA1, 10.000 iteraciones, clave de 256 bits)
y una **sal aleatoria de 16 bytes** distinta para cada usuario. En la tabla
`usuarios` solo quedan la sal y el hash, ambos en Base64.

Al iniciar sesión se calcula el hash de la clave ingresada con la sal guardada y
se compara con `MessageDigest.isEqual`, una comparación de tiempo constante.

Un hash no se puede revertir, por lo que **una clave olvidada no se recupera**:
se debe generar una nueva.

## Tecnologías

- Kotlin
- Android Studio
- Jetpack Compose (toda la interfaz se escribe en Kotlin, sin XML de pantallas)
- Material Design 3
- Animaciones de Compose (`AnimatedContent`)
- Arquitectura MVVM
- Room (SQLite) con KSP para la persistencia local
- SharedPreferences para los resultados de las pruebas
- Git y GitHub para el control de versiones

## Arquitectura

```text
app/src/main/java/com/example/mpaz_pro_app_movil/
├── MainActivity.kt     Punto de entrada y acceso según el perfil
├── auth/               Perfiles (RolUsuario), cuentas de prueba y cifrado de claves
├── components/         Componentes reutilizables (campo validado, etiqueta de estado, transiciones)
├── datos/              Base de datos Room y datos de prueba
│   ├── dao/            Consultas a cada tabla
│   ├── entidad/        Tablas de Room
│   ├── model/          Modelos que usa la interfaz
│   └── repositorio/    Repositorio y almacén compartido de listas
├── notificaciones/     Aviso al asignar una unidad
├── pruebas/            Las cuatro pruebas finales del estudiante
├── screens/            Pantallas de los tres perfiles
├── ui/theme/           Colores y tipografía
├── validacion/         Validador central y manejo de fechas
└── viewmodel/          Estado y reglas de los tres perfiles
```

Flujo de los datos:

```text
Pantalla  →  ViewModel  →  AlmacenMpaz  →  RepositorioMpaz  →  DAO  →  Room
 (muestra)   (reglas)      (listas que       (lee y guarda)            (SQLite)
                            se redibujan)
```

- **screens y components:** muestran el estado y envían los eventos del usuario.
- **viewmodel:** contiene las reglas (bloqueos, estados, qué unidades ve el
  estudiante) y devuelve el motivo cuando una acción no se puede realizar.
- **datos:** guarda y entrega la información. `AlmacenMpaz` mantiene las listas
  compartidas, de modo que lo que publica la UTP aparece en el perfil docente y
  lo que asigna el docente aparece en el estudiante sin reiniciar la aplicación.
- **validacion:** reúne todas las validaciones en un solo lugar.

## Pasos para ejecutar

### Requisitos

- Android Studio instalado.
- Emulador o dispositivo con Android 7.0 (API 24) o superior.

### Instalación

1. Clonar el repositorio:

```bash
git clone https://github.com/amaturanaa/Mpaz-pro-app_movil.git
```

2. Abrir Android Studio y seleccionar **File > Open**.
3. Elegir la carpeta `Mpaz-pro-app_movil` y presionar **OK**.
4. Esperar a que termine **Gradle Sync**.
5. Seleccionar un emulador o conectar un dispositivo.
6. Presionar **Run** para ejecutar el módulo `app`.
7. Iniciar sesión con una de las cuentas de prueba.
8. En Android 13 o superior, aceptar el permiso de notificaciones.

### Instalar en un teléfono Android

1. En la terminal de Android Studio, dentro de la carpeta del proyecto, ejecutar
   `.\gradlew assembleDebug`.
2. Copiar el archivo `app-debug.apk` (carpeta `app\build\outputs\apk\debug\`) al
   teléfono e instalarlo. Android puede pedir permiso para instalar apps de
   origen desconocido.

También se puede ejecutar directamente en el teléfono con la depuración USB o
inalámbrica activada. Cada dispositivo guarda su propia base de datos.

### Recorrido sugerido para probar

1. Entrar como **UTP**, crear una unidad con una pregunta y pasarla hasta **Publicado**.
2. Entrar como **Docente**, asignar la unidad a un curso y revisar la notificación.
3. Entrar como **Alumno**: la unidad asignada aparece con su explicación y el
   botón para escucharla. Rendir una prueba.
4. Volver como **Docente** y revisar el informe y las observaciones.

## Limitaciones conocidas

Es un MVP y estas partes quedan para una siguiente versión:

- Las pruebas del estudiante son una por asignatura, con preguntas fijas. Todavía
  no leen las preguntas que crea la UTP.
- Como la prueba es una por asignatura, sus intentos y notas se comparten entre
  todas las unidades de esa asignatura. Al asignar una unidad por primera vez, el
  conteo de esa asignatura parte de cero.
- Los intentos se guardan por instalación de la aplicación, no por estudiante.
- Las contraseñas de las cuentas de prueba se cargan al crear la base de datos.
  Aún no existe una pantalla para restablecerlas.
- Las notificaciones son locales: el aviso aparece en el dispositivo donde el
  docente asigna la unidad. Enviarlo al teléfono de cada estudiante requeriría
  un servidor con notificaciones push.
- No hay conexión con sistemas del colegio ni un servidor: todo funciona sin internet.
- Las cuentas de inicio de sesión son de demostración.

## Datos y privacidad

Todos los usuarios, cursos, respuestas e informes de la aplicación son
ficticios. No se utilizan nombres, RUT ni credenciales reales de estudiantes o
de la institución. La aplicación no se conecta con sistemas del colegio ni
genera diagnósticos automáticos con inteligencia artificial.

## Planificación y control de versiones

- Repositorio: https://github.com/amaturanaa/Mpaz-pro-app_movil

Cada integrante trabaja en su propia rama y los cambios se integran a `main`
mediante pull requests.