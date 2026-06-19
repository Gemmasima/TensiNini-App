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
     */
    @Insert
    suspend fun insertarToma(toma: TomaTension)

    /**
     * Devuelve todas las mediciones guardadas, ordenadas de más reciente
     * a más antigua según fecha.
     */
    @Query("SELECT * FROM mediciones ORDER BY fecha DESC")
    suspend fun obtenerTodasLasTomas(): List<TomaTension>
}