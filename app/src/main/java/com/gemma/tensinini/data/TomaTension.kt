package com.gemma.tensinini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mediciones")
data class TomaTension (
    @PrimaryKey(autoGenerate = true)
    //val= constantes inmutables. las uso por seguridad médica y estabilidad de la app
    val id: Int=0,
    val fecha: String,
    val franja: String, //Mañana y Noche
    val emocion: String, // Tranquila, Nerviosa, Enfadada

    val sis1: Int, val dia1: Int, val pulso1: Int,
    //Toma 2 (a los 2 minutos)
    val sis2: Int, val dia2: Int, val pulso2: Int,
    //Toma 3 (a los 2 minutos - Válida!
    val sis3: Int, val dia3: Int, val pulso3: Int,
    )
