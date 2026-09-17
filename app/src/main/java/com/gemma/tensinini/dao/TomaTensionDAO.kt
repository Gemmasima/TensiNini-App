package com.gemma.tensinini.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gemma.tensinini.data.TomaTension

/**
 * DAO (Data Access Object para la tabla "mediciones")
 * Define las operaciones permitidas sobre la base de datos local.
 */
@Dao
interface TomaTensionDAO {

    /**
     * Inserta una nueva medición en la base de datos.
     * "suspend" hace que la función corra en segundo plano,
     * para no congelar la pantalla de la app.
     * Devuelve el id autogenerado por Room para esta fila, necesario
     * para poder marcarla luego como sincronizada.
     */
    @Insert
    suspend fun insertarToma(toma: TomaTension): Long

    /**
     * Devuelve todas las mediciones guardadas, ordenadas de más reciente
     * a más antigua según fecha.
     */
    @Query("SELECT * FROM mediciones ORDER BY fecha DESC")
    suspend fun obtenerTodasLasTomas(): List<TomaTension>

    /**
    * Actualiza una medición ya existente (identificada por su id)
    * con valores nuevos, si fuera necesario.
    */
    @androidx.room.Update
    suspend fun actualizarToma(toma: TomaTension)

    /** Marcar una medición como sincronizada con el backend, identificándola
     * por su id. Solo actualiza ese campo, sin tocar el resto de valores.
     */
    @Query("UPDATE mediciones SET sincronizado = :sincronizado WHERE id= :id")
    suspend fun marcarSincronizado(id: Int, sincronizado: Boolean)
}