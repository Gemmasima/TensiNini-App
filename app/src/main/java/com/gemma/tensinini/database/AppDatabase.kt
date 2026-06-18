package com.gemma.tensinini.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gemma.tensinini.dao.TomaTensionDAO
import com.gemma.tensinini.data.TomaTension

// Le digo a Room qué tablas tiene la BD y la versión
@Database(entities = [TomaTension::class], version=1 )
abstract class AppDatabase : RoomDatabase () {

    //Conecto la BD con el DAO para poder usar las consultas
    abstract fun tomaTensionDAO(): TomaTensionDAO
}