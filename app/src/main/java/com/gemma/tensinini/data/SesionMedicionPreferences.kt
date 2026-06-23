package com.gemma.tensinini.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extensión que crea una única instancia de DataStore para toda la app.
 * Se declara a nivel de fichero para garantizar que solo existe un DataStore
 * con el nombre "sesion_medicion" durante toda la vida de la aplicación.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "sesion_medicion"
)

/**
 * Repositorio de preferencias para persistir el estado de una sesión de medición
 * de tensión arterial entre aperturas de la aplicación.
 *
 * Guarda dos datos clave:
 * - [tomaActual]: número de toma en curso (1, 2 o 3). 0 significa que no hay sesión activa.
 * - [timestampFinEspera]: marca de tiempo (en milisegundos) en que finaliza la espera
 *   entre tomas. Permite recalcular el tiempo restante del temporizador al reabrir la app.
 *
 * @param context Contexto de la aplicación necesario para acceder a DataStore.
 */


class SesionMedicionPreferences(private val context: Context) {

    companion object {
        /** Clave para almacenar el número de toma actual (0 = sin sesión activa). */
        private val KEY_TOMA_ACTUAL = intPreferencesKey("toma_actual")

        /** Clave para almacenar el timestamp de fin de la espera entre tomas (en ms). */
        private val KEY_TIMESTAMP_FIN_ESPERA = longPreferencesKey("timestamp_fin_espera")
    }

    /**
     * Flujo reactivo que emite el número de toma actual cada vez que cambia.
     * Emite 0 si no hay ningún valor guardado (sin sesión activa).
     */
    val tomaActual: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_TOMA_ACTUAL] ?: 0
    }

    /**
     * Flujo reactivo que emite el timestamp de fin de espera cada vez que cambia.
     * Emite 0L si no hay ningún valor guardado (sin espera activa).
     */
    val timestampFinEspera: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_TIMESTAMP_FIN_ESPERA] ?: 0L
    }

    /**
     * Guarda el número de toma actual en DataStore.
     *
     * @param toma Número de toma a persistir (1, 2 o 3). Usar 0 para indicar
     *             que no hay sesión activa.
     */
    suspend fun guardarTomaActual(toma: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOMA_ACTUAL] = toma
        }
    }

    /**
     * Guarda el timestamp de fin de espera entre tomas en DataStore.
     *
     * @param timestampMs Marca de tiempo en milisegundos (System.currentTimeMillis()
     *                    + duración de la espera) que indica cuándo termina el temporizador.
     */
    suspend fun guardarTimestampFinEspera(timestampMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TIMESTAMP_FIN_ESPERA] = timestampMs
        }
    }

    /**
     * Elimina todos los datos de la sesión en curso de DataStore.
     * Se debe llamar cuando la sesión termina (tras la toma 3 y guardar la emoción)
     * o cuando el usuario cancela la sesión manualmente.
     */
    suspend fun limpiarSesion() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_TOMA_ACTUAL)
            preferences.remove(KEY_TIMESTAMP_FIN_ESPERA)
        }
    }
}