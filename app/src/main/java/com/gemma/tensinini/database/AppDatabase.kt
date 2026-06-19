package com.gemma.tensinini.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gemma.tensinini.dao.TomaTensionDAO
import com.gemma.tensinini.data.TomaTension

/**
 * Punto de entrada a la base de datos local Room.
 * Aquí se declaran todas las entidades (tablas) que maneja la app.
 * y la versión del schema, necesaria para futuras migraciones.
 */
@Database(entities = [TomaTension::class], version=1 )
abstract class AppDatabase : RoomDatabase () {

    //Conecta la BD con el DAO para poder ejecutar las consultas
    abstract fun tomaTensionDAO(): TomaTensionDAO
}