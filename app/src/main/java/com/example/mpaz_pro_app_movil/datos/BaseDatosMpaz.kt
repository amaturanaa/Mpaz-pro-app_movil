package com.example.mpaz_pro_app_movil.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// =============================================
// BASE DE DATOS LOCAL DE LA APP (Room)
// =============================================
// Reúne las cuatro tablas y los DAOs para consultarlas.
// El archivo "mpaz_pro.db" queda en la carpeta privada de la app,
// así que los datos sobreviven aunque se cierre la aplicación.
//
// Versión 1: las cuatro tablas.
// Versión 2: las asignaciones guardan si se avisa al curso y si el
//            estudiante puede ver sus respuestas.
@Database(
    entities = [
        UnidadEntidad::class,
        PreguntaEntidad::class,
        AsignacionEntidad::class,
        ObservacionEntidad::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BaseDatosMpaz : RoomDatabase() {

    abstract fun unidadDao(): UnidadDao
    abstract fun preguntaDao(): PreguntaDao
    abstract fun asignacionDao(): AsignacionDao
    abstract fun observacionDao(): ObservacionDao

    companion object {

        // Pasa de la versión 1 a la 2 sin borrar nada: agrega las dos
        // columnas nuevas y las asignaciones antiguas quedan con "sí" (1).
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE asignaciones " +
                            "ADD COLUMN notificarCurso INTEGER NOT NULL DEFAULT 1"
                )
                db.execSQL(
                    "ALTER TABLE asignaciones " +
                            "ADD COLUMN permitirVerRespuestas INTEGER NOT NULL DEFAULT 1"
                )
            }
        }

        @Volatile
        private var instancia: BaseDatosMpaz? = null

        // Una sola base de datos para toda la app.
        fun obtener(context: Context): BaseDatosMpaz =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatosMpaz::class.java,
                    "mpaz_pro.db"
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()
                    .also { instancia = it }
            }
    }
}