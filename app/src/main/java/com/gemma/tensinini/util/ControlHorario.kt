package com.gemma.tensinini.util

import com.gemma.tensinini.data.Franja
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 * Calcula automatic. la fecha, hora y la franja horaria actuales del
 * sistema, evitando que el usuario tenga que introducirlos manualmente.
 *
 * El protocolo médico solo permite iniciar una sesión de medición dentro
 * de dos ventanas horarias concretas:
 * - Mañana: entre las 7:00 y las 11:00
 * - Noche: entre las 20:00 y las 00:00
 *
 * Fuera de esas ventanas no se considera una medición válida.
 */

class ControlHorario {

    private val INICIO_MANANA = LocalTime.of(7,0)
    private val FIN_MANANA = LocalTime.of(11,0)
    private val INICIO_NOCHE = LocalTime.of(20,0)
    private val FIN_NOCHE = LocalTime.MIDNIGHT

    /**
     * Devuelve la fecha actual del sistema "yyyy-mm-dd"
     */
    fun obtenerFechaActual(): String {
        val formato= DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.now().format(formato)
    }

    /**
     * Devuelve la hora actual del sistema "hh:mm" se usa para registrar la
     * hora exacta de cada toma individual (toma 1,2,3) dentro de una sesión.
     */
    fun obtenerHoraActual(): String {
        val formato= DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.now().format(formato)
    }

    /**
     * Calcula la franja horaria actual según la hora del sistema,
     * comprobando si cae dentro de alguna de las dos ventanas válidas.
     *
     * Debe llamarse solo al iniciar la toma1, se fija para toda la sesión
     * y no se recalcula en las tomas 2 y 3.
     * @return Franja.MANANA - 7:00 y las 11:00, Franja.NOCHE - 20:00 y las 00:00,
     * o `null` si la hora actual no está dentro de ninguna ventana válida.
     */
    fun obtenerFranjaActual(): Franja? {
        val horaActual= LocalTime.now()

        val esManana= !horaActual.isBefore(INICIO_MANANA) && horaActual.isBefore(FIN_MANANA)
        val esNoche= !horaActual.isBefore(INICIO_NOCHE) || horaActual.isBefore(FIN_NOCHE)
        // El "||" en esNoche cubre el cruce de medianoche.

        return when {
            esManana -> Franja.MANANA
            esNoche -> Franja.NOCHE
            else -> null //Fuera de ambas ventanas: no se puede medir.
        }
    }

    /**
     * Indica si en este momento es posible iniciar una sesión de medición,
     * según las ventanas horarias del protocolo.
     * Debe comprobarse antes de registrar la toma1 de una nueva sesión.
     */
    fun esHorarioValido(): Boolean {
        return obtenerFranjaActual() !=null
    }
}