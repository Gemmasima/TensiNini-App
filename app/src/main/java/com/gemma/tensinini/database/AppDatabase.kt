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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
        * Devuelve la instancia única de la base de datos, creándola la
        * primera vez que se solicita (patrón singleton).
        */
        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tensinini_database"
                ).build()
                INSTANCE = instance
                instance
                }
            }
        }
}
