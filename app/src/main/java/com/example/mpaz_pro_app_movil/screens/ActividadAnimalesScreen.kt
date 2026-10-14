package com.example.mpaz_pro_app_movil.screens

// ============================================================
// ACTIVIDAD DE CIENCIAS NATURALES: EXPLORADORES DE ANIMALES
// Segundo básico: clasificar animales en vertebrados e
// invertebrados a partir de fotos tomadas o elegidas del móvil.
// ============================================================

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

// ============================================================
// 1. MODELO: QUÉ GUARDAMOS POR CADA ANIMAL
// ============================================================

// "archivo" es el nombre de la foto dentro del almacenamiento
// privado; NO guardamos aquí una URI externa temporal.
// "tipo" será "Vertebrado", "Invertebrado" o vacío si falta responder.
// "nombre" es opcional: por ejemplo, "Mariposa" o "Perro".
private data class FotoAnimal(
    val archivo: String,
    val nombre: String = "",
    val tipo: String = ""
)

// Cada actividad permite subir entre 3 y 10 imágenes.
private const val MIN_FOTOS_ANIMALES = 3
private const val MAX_FOTOS_ANIMALES = 10

// Nombre del espacio local de datos de ESTA actividad.
// No se mezcla con las preferencias de las pruebas finales.
private const val PREF_ACTIVIDAD_ANIMALES = "actividad_animales_ciencias"

// Carpeta privada para evitar pedir acceso permanente a la galería.
private const val CARPETA_FOTOS = "fotos_animales_ciencias"

// ============================================================
// 2. FUNCIONES PARA GUARDAR Y RECUPERAR EL BORRADOR
// ============================================================

private fun carpetaFotos(context: Context): File =
    File(context.filesDir, CARPETA_FOTOS).apply { mkdirs() }

// Leemos la lista persistida con JSON y descartamos las fotos
// cuyos archivos ya no están en el teléfono.
private fun cargarFotosAnimales(context: Context): List<FotoAnimal> {
    val prefs = context.getSharedPreferences(PREF_ACTIVIDAD_ANIMALES, Context.MODE_PRIVATE)
    val texto = prefs.getString("fotos", "[]") ?: "[]"
    val carpeta = carpetaFotos(context)

    return try {
        val arreglo = JSONArray(texto)
        buildList {
            for (i in 0 until arreglo.length()) {
                val objeto = arreglo.getJSONObject(i)
                val archivo = objeto.optString("archivo")
                // Rechazamos nombres fuera de nuestra carpeta privada.
                if (archivo.isNotBlank() && archivo == File(archivo).name &&
                    File(carpeta, archivo).isFile) {
                    add(
                        FotoAnimal(
                            archivo = archivo,
                            nombre = objeto.optString("nombre"),
                            tipo = objeto.optString("tipo")
                        )
                    )
                }
            }
        }.take(MAX_FOTOS_ANIMALES)
    } catch (_: Exception) {
        // Si los datos se dañan, evitamos cerrar inesperadamente la app.
        emptyList()
    }
}

// Guarda siempre la lista completa y también si ya se terminó.
// Se llama después de agregar, editar, clasificar o eliminar fotos.
private fun guardarFotosAnimales(
    context: Context,
    fotos: List<FotoAnimal>,
    terminada: Boolean
) {
    val arreglo = JSONArray()
    fotos.forEach { foto ->
        arreglo.put(
            JSONObject().apply {
                put("archivo", foto.archivo)
                put("nombre", foto.nombre)
                put("tipo", foto.tipo)
            }
        )
    }
    context.getSharedPreferences(PREF_ACTIVIDAD_ANIMALES, Context.MODE_PRIVATE)
        .edit()
        .putString("fotos", arreglo.toString())
        .putBoolean("terminada", terminada)
        .apply()
}

// Importamos una imagen escogida por el estudiante a una copia
// privada. Esto evita perder la foto al cerrar la app o si el
// proveedor de la galería deja de concedernos acceso temporal.
private fun copiarFotoGaleria(context: Context, uri: Uri): String {
    val archivo = "animal_${UUID.randomUUID()}.jpg"
    val destino = File(carpetaFotos(context), archivo)
    try {
        context.contentResolver.openInputStream(uri)?.use { entrada ->
            destino.outputStream().use { salida ->
                entrada.copyTo(salida)
            }
        } ?: error("No se pudo abrir la foto seleccionada")
        require(destino.length() > 0) { "La foto está vacía" }
        return archivo
    } catch (error: Exception) {
        // Si la copia falla no dejamos una imagen incompleta.
        destino.delete()
        throw error
    }
}

// Crea una miniatura en memoria, en vez de abrir toda la foto
// original de la cámara (que puede ser muy grande).
private fun cargarMiniatura(archivo: File): androidx.compose.ui.graphics.ImageBitmap? {
    return try {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(archivo.absolutePath, limites)
        var muestra = 1
        while (limites.outWidth / muestra > 900 || limites.outHeight / muestra > 900) {
            muestra *= 2
        }
        val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
        BitmapFactory.decodeFile(archivo.absolutePath, opciones)?.asImageBitmap()
    } catch (_: Exception) {
        null
    }
}

// ============================================================
// 3. PANTALLA QUE VE EL ESTUDIANTE
// ============================================================

@Composable
fun ActividadAnimalesScreen(onVolver: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val carpeta = remember(context) { carpetaFotos(context) }

    // Compose observa esta lista: si agregamos o eliminamos una
    // imagen, se vuelve a dibujar automáticamente la pantalla.
    val fotos = remember(context) {
        mutableStateListOf<FotoAnimal>().apply {
            addAll(cargarFotosAnimales(context))
        }
    }

    val prefs = remember(context) {
        context.getSharedPreferences(PREF_ACTIVIDAD_ANIMALES, Context.MODE_PRIVATE)
    }
    var terminada by remember(context) {
        mutableStateOf(prefs.getBoolean("terminada", false))
    }
    var mensaje by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    // Nombre del archivo preparado para la cámara.
    // rememberSaveable lo conserva si se gira la pantalla.
    var archivoCamaraPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    // Confirma antes de borrar, para evitar pérdidas accidentales.
    var fotoParaBorrar by remember { mutableStateOf<String?>(null) }

    // Una modificación vuelve la actividad a borrador y guarda
    // inmediatamente los cambios en el almacenamiento local.
    fun guardarBorrador() {
        terminada = false
        guardarFotosAnimales(context, fotos.toList(), terminada = false)
        mensaje = "Borrador guardado automáticamente."
    }

    // Agrega al listado una foto YA guardada en nuestra carpeta.
    fun agregarArchivo(nombre: String) {
        if (fotos.size >= MAX_FOTOS_ANIMALES) {
            File(carpeta, nombre).delete()
            mensaje = "Se permiten como máximo 10 fotos."
            return
        }
        fotos.add(FotoAnimal(archivo = nombre))
        guardarBorrador()
    }

    // ========================================================
    // 4. ABRIR LA CÁMARA DEL CELULAR
    // ========================================================

    // TakePicture abre la app de cámara instalada y guarda la
    // imagen en la URI que le entregamos. No necesitamos usar
    // directamente CameraX ni declarar permiso CAMERA.
    val abrirCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        val nombre = archivoCamaraPendiente
        archivoCamaraPendiente = null
        if (nombre != null) {
            val archivo = File(carpeta, nombre)
            if (exito && archivo.exists() && archivo.length() > 0) {
                agregarArchivo(nombre)
            } else {
                archivo.delete()
                mensaje = "No se guardó la foto. Puedes intentarlo otra vez."
            }
        }
    }

    // ========================================================
    // 5. ELEGIR UNA FOTO DE LA GALERÍA
    // ========================================================

    // GetContent permite elegir una imagen sin pedir permisos
    // de lectura permanente de todas las fotos del teléfono.
    val abrirGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            if (fotos.size >= MAX_FOTOS_ANIMALES) {
                mensaje = "Ya tienes 10 fotos, elimina una para agregar otra."
            } else {
                // Copiamos en segundo plano para no congelar la interfaz.
                scope.launch {
                    guardando = true
                    try {
                        val nombre = withContext(Dispatchers.IO) {
                            copiarFotoGaleria(context, uri)
                        }
                        agregarArchivo(nombre)
                    } catch (_: Exception) {
                        mensaje = "No fue posible importar esta imagen. Prueba con otra."
                    } finally {
                        guardando = false
                    }
                }
            }
        }
    }

    // El botón atrás físico también devuelve a Ciencias Naturales.
    BackHandler(onBack = onVolver)

    // Contenemos todo en una LazyColumn para que las 10 fotos
    // puedan verse sin quedar tapadas por los bordes del celular.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TextButton(onClick = onVolver) {
                Text("← Volver a Ciencias Naturales")
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF5FF)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Exploradores de animales",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C365F)
                    )
                    Text(
                        "Fotografía animales, incluidos insectos, y descubre si son " +
                                "vertebrados o invertebrados.",
                        color = Color(0xFF375879)
                    )
                    Text(
                        "Los vertebrados tienen columna vertebral. " +
                                "Los invertebrados no tienen columna vertebral. " +
                                "¡Los insectos son invertebrados!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF375879)
                    )
                    Text(
                        "Debes agregar al menos 3 fotos y puedes tener hasta 10.",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1C365F)
                    )
                    Text(
                        "Fotos: ${fotos.size} / $MAX_FOTOS_ANIMALES   ·   " +
                                "Clasificadas: ${fotos.count { it.tipo.isNotBlank() }}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF285CB2)
                    )
                    LinearProgressIndicator(
                        progress = { fotos.size.toFloat() / MAX_FOTOS_ANIMALES },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Los dos botones se deshabilitan cuando se alcanza el límite.
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = fotos.size < MAX_FOTOS_ANIMALES && !guardando,
                    onClick = {
                        // Creamos un archivo ANTES de abrir la cámara.
                        // FileProvider lo convierte a una URI segura.
                        try {
                            val archivo = File.createTempFile(
                                "animal_camara_", ".jpg", carpeta
                            )
                            archivoCamaraPendiente = archivo.name
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.animalfileprovider",
                                archivo
                            )
                            abrirCamara.launch(uri)
                        } catch (_: Exception) {
                            archivoCamaraPendiente?.let { File(carpeta, it).delete() }
                            archivoCamaraPendiente = null
                            mensaje = "No se pudo abrir la cámara de este dispositivo."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3169C6)
                    )
                ) {
                    Text("Sacar foto")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = fotos.size < MAX_FOTOS_ANIMALES && !guardando,
                    onClick = { abrirGaleria.launch("image/*") }
                ) {
                    Text("Galería")
                }
            }
        }

        // Mensajes de validación, éxito y errores.
        if (mensaje.isNotBlank()) {
            item {
                Text(
                    text = mensaje,
                    color = Color(0xFF285CB2),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (guardando) {
            item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        }

        // ====================================================
        // 6. UNA TARJETA POR CADA FOTO
        // ====================================================
        itemsIndexed(fotos, key = { _, foto -> foto.archivo }) { indice, foto ->
            val ruta = remember(foto.archivo) { File(carpeta, foto.archivo) }
            val miniatura = remember(foto.archivo) { cargarMiniatura(ruta) }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F7FF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Animal ${indice + 1}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C365F)
                        )
                        TextButton(onClick = { fotoParaBorrar = foto.archivo }) {
                            Text("Eliminar", color = Color(0xFFB3261E))
                        }
                    }
                    if (miniatura != null) {
                        Image(
                            bitmap = miniatura,
                            contentDescription = "Foto del animal ${indice + 1}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("No se puede mostrar esta imagen.")
                    }
                    // El nombre es opcional; la clasificación es obligatoria.
                    OutlinedTextField(
                        value = foto.nombre,
                        onValueChange = { nuevoNombre ->
                            fotos[indice] = foto.copy(nombre = nuevoNombre)
                            guardarBorrador()
                        },
                        label = { Text("Nombre del animal (opcional)") },
                        placeholder = { Text("Ejemplo: mariposa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("¿Tiene columna vertebral?", color = Color(0xFF1C365F))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Vertebrado", "Invertebrado").forEach { tipo ->
                            FilterChip(
                                selected = foto.tipo == tipo,
                                onClick = {
                                    fotos[indice] = foto.copy(tipo = tipo)
                                    guardarBorrador()
                                },
                                label = { Text(tipo, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        }

        // ====================================================
        // 7. FINALIZAR: MÍNIMO 3 Y TODAS CLASIFICADAS
        // ====================================================
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3169C6)
                ),
                onClick = {
                    when {
                        fotos.size < MIN_FOTOS_ANIMALES -> {
                            mensaje = "Debes agregar al menos 3 fotos para terminar."
                        }
                        fotos.size > MAX_FOTOS_ANIMALES -> {
                            mensaje = "Puedes tener como máximo 10 fotos."
                        }
                        fotos.any { it.tipo.isBlank() } -> {
                            mensaje = "Clasifica todas las fotos antes de terminar."
                        }
                        else -> {
                            // Guardamos la actividad como terminada.
                            terminada = true
                            guardarFotosAnimales(context, fotos.toList(), terminada = true)
                            mensaje = "¡Actividad completada! Clasificaste ${fotos.size} animales."
                        }
                    }
                }
            ) {
                Text(if (terminada) "Actividad guardada ✓" else "Terminar actividad")
            }
        }
        item {
            Text(
                "Tus fotos quedan guardadas en este dispositivo. " +
                        "Si agregas, editas o eliminas una foto después de terminar, " +
                        "deberás pulsar Terminar actividad nuevamente. " +
                        "Pide ayuda a una persona adulta y evita fotografiar rostros.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Ventana de confirmación antes de eliminar una foto.
    fotoParaBorrar?.let { nombre ->
        AlertDialog(
            onDismissRequest = { fotoParaBorrar = null },
            title = { Text("¿Eliminar esta foto?") },
            text = { Text("La foto se borrará de la actividad.") },
            confirmButton = {
                TextButton(onClick = {
                    // Filtramos la lista, persistimos y borramos archivo.
                    fotos.removeAll { it.archivo == nombre }
                    guardarBorrador()
                    File(carpeta, nombre).delete()
                    fotoParaBorrar = null
                    mensaje = "Foto eliminada."
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { fotoParaBorrar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
