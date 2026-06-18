package com.gemma.tensinini.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gemma.tensinini.data.TomaTension

@Dao
interface TomaTensionDAO {

    // suspend. hace que la función corra en segundo plano para no congelar la pantalla de la app
    @Insert
    suspend fun insertarToma(toma: TomaTension)

    @Query("SELECT * FROM mediciones ORDER BY fecha DESC")
    suspend fun obtenerTodasLasTomas(): List<TomaTension>
}