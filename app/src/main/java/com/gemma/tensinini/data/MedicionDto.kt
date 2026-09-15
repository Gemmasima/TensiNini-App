package com.gemma.tensinini.data

/** Representa los datos de una medicion tal como los espera el backend
 * Spring Boot.
 *
 * DTO (data transfer object) pq su unico proposo es viajar por la red como
 * JSON.
 */
data class MedicionDto (
    val pacienteId: String,
    val fecha: String,   // Formato "yyyy-MM-dd"
    val franja: String,  // "MANANA" o "NOCHE"
    val emocion: String, // "TRANQUILA", "NERVIOSA" o "ENFADADA"

    // Toma 1
    val hora1: String,
    val sis1: Int,
    val dia1: Int,
    val pulso1: Int,

    // Toma 2
    val sis2: Int,
    val dia2: Int,
    val pulso2: Int,

    // Toma 3 (la válida)
    val sis3: Int,
    val dia3: Int,
    val pulso3: Int

)