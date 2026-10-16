package com.example.mpaz_pro_app_movil.datos.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mpaz_pro_app_movil.datos.entidad.AsignacionEntidad
import com.example.mpaz_pro_app_movil.datos.entidad.ObservacionEntidad
import com.example.mpaz_pro_app_movil.datos.entidad.PreguntaEntidad
import com.example.mpaz_pro_app_movil.datos.entidad.UnidadEntidad
import com.example.mpaz_pro_app_movil.datos.entidad.UsuarioEntidad

// =============================================
// DAOs: las consultas a la base de datos (Room)
// =============================================
// Un DAO dice qué se puede hacer con cada tabla.
// "reemplazar" borra y vuelve a insertar todo dentro de una transacción:
// o se guarda todo o no se guarda nada.

@Dao
abstract class UnidadDao {

    @Query("SELECT * FROM unidades ORDER BY id")
    abstract suspend fun todas(): List<UnidadEntidad>

    @Query("DELETE FROM unidades")
    abstract suspend fun borrarTodas()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(unidades: List<UnidadEntidad>)

    @Transaction
    open suspend fun reemplazar(unidades: List<UnidadEntidad>) {
        borrarTodas()
        insertar(unidades)
    }
}

@Dao
abstract class PreguntaDao {

    @Query("SELECT * FROM preguntas ORDER BY id")
    abstract suspend fun todas(): List<PreguntaEntidad>

    @Query("DELETE FROM preguntas")
    abstract suspend fun borrarTodas()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(preguntas: List<PreguntaEntidad>)

    @Transaction
    open suspend fun reemplazar(preguntas: List<PreguntaEntidad>) {
        borrarTodas()
        insertar(preguntas)
    }
}

@Dao
abstract class AsignacionDao {

    @Query("SELECT * FROM asignaciones ORDER BY id")
    abstract suspend fun todas(): List<AsignacionEntidad>

    @Query("DELETE FROM asignaciones")
    abstract suspend fun borrarTodas()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(asignaciones: List<AsignacionEntidad>)

    @Transaction
    open suspend fun reemplazar(asignaciones: List<AsignacionEntidad>) {
        borrarTodas()
        insertar(asignaciones)
    }
}

@Dao
abstract class ObservacionDao {

    @Query("SELECT * FROM observaciones ORDER BY id")
    abstract suspend fun todas(): List<ObservacionEntidad>

    @Query("DELETE FROM observaciones")
    abstract suspend fun borrarTodas()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(observaciones: List<ObservacionEntidad>)

    @Transaction
    open suspend fun reemplazar(observaciones: List<ObservacionEntidad>) {
        borrarTodas()
        insertar(observaciones)
    }
}


@Dao
abstract class UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario LIMIT 1")
    abstract suspend fun buscar(usuario: String): UsuarioEntidad?

    @Query("SELECT COUNT(*) FROM usuarios")
    abstract suspend fun cantidad(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertar(usuarios: List<UsuarioEntidad>)
}