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
// Reúne las tablas y los DAOs para consultarlas.
// El archivo "mpaz_pro.db" queda en la carpeta privada de la app,
// así que los datos sobreviven aunque se cierre la aplicación.
//
// Versión 1: unidades, preguntas, asignaciones y observaciones.
// Versión 2: las asignaciones guardan si se avisa al curso y si el
//            estudiante puede ver sus respuestas.
// Versión 3: tabla de usuarios para el inicio de sesión (clave cifrada).
@Database(
    entities = [
        UnidadEntidad::class,
        PreguntaEntidad::class,
        AsignacionEntidad::class,
        ObservacionEntidad::class,
        UsuarioEntidad::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BaseDatosMpaz : RoomDatabase() {

    abstract fun unidadDao(): UnidadDao
    abstract fun preguntaDao(): PreguntaDao
    abstract fun asignacionDao(): AsignacionDao
    abstract fun observacionDao(): ObservacionDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {

        // Versión 1 -> 2: agrega dos columnas a las asignaciones; las que ya
        // existían quedan con "sí" (1).
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

        // Versión 2 -> 3: crea la tabla de usuarios. No toca ninguna otra.
        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS usuarios (" +
                            "usuario TEXT NOT NULL, " +
                            "nombre TEXT NOT NULL, " +
                            "rol TEXT NOT NULL, " +
                            "sal TEXT NOT NULL, " +
                            "claveCifrada TEXT NOT NULL, " +
                            "PRIMARY KEY(usuario))"
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
                    .addMigrations(MIGRACION_1_2, MIGRACION_2_3)
                    .build()
                    .also { instancia = it }
            }
    }
}