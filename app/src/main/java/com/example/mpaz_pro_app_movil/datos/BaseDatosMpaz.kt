package com.example.mpaz_pro_app_movil.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// =============================================
// BASE DE DATOS LOCAL DE LA APP (Room)
// =============================================
// Reúne las cuatro tablas y los DAOs para consultarlas.
// El archivo "mpaz_pro.db" queda en la carpeta privada de la app,
// así que los datos sobreviven aunque se cierre la aplicación.
@Database(
    entities = [
        UnidadEntidad::class,
        PreguntaEntidad::class,
        AsignacionEntidad::class,
        ObservacionEntidad::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BaseDatosMpaz : RoomDatabase() {

    abstract fun unidadDao(): UnidadDao
    abstract fun preguntaDao(): PreguntaDao
    abstract fun asignacionDao(): AsignacionDao
    abstract fun observacionDao(): ObservacionDao

    companion object {
        @Volatile
        private var instancia: BaseDatosMpaz? = null

        // Una sola base de datos para toda la app.
        fun obtener(context: Context): BaseDatosMpaz =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosMpaz::class.java,
                    "mpaz_pro.db"
                ).build().also { instancia = it }
            }
    }
}