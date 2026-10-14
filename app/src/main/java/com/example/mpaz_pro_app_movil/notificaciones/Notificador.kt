package com.example.mpaz_pro_app_movil.notificaciones

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.mpaz_pro_app_movil.MainActivity

// =============================================
// NOTIFICACIONES (recurso nativo de Android)
// =============================================
// Avisa en la barra de notificaciones del teléfono cuando el docente
// asigna una unidad. Si la persona no da el permiso, la app sigue
// funcionando igual: simplemente no se muestra el aviso.
object Notificador {

    private const val CANAL_ID = "mpaz_asignaciones"

    // Desde Android 13 hay que pedir permiso para mostrar notificaciones.
    // En versiones anteriores no hace falta.
    fun necesitaPedirPermiso(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
    }

    // Desde Android 8 las notificaciones viven en un "canal". Se crea una vez.
    private fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val canal = NotificationChannel(
            CANAL_ID,
            "Asignaciones",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos cuando se asigna una unidad a un curso"
        }
        val administrador = context.getSystemService(NotificationManager::class.java)
        administrador.createNotificationChannel(canal)
    }

    // Muestra el aviso. Devuelve true si se mostró, false si no había permiso
    // o las notificaciones están desactivadas para la app.
    @SuppressLint("MissingPermission")
    fun avisarAsignacion(
        context: Context,
        tituloUnidad: String,
        curso: String,
        fechaFin: String
    ): Boolean {
        if (necesitaPedirPermiso(context)) return false

        val administrador = NotificationManagerCompat.from(context)
        if (!administrador.areNotificationsEnabled()) return false

        crearCanal(context)

        // Al tocar el aviso se abre la app.
        val intencion = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val intencionPendiente = PendingIntent.getActivity(
            context,
            0,
            intencion,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val texto = "$tituloUnidad · $curso · hasta el $fechaFin"
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nueva unidad asignada")
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intencionPendiente)
            .setAutoCancel(true)
            .build()

        // Un número distinto por aviso, para que no se reemplacen entre sí.
        administrador.notify(System.currentTimeMillis().toInt(), notificacion)
        return true
    }
}