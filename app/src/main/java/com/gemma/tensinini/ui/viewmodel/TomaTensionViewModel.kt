package com.gemma.tensinini.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemma.tensinini.data.TomaTension
import com.gemma.tensinini.dao.TomaTensionDAO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel que controla todo el flujo de una sesión de medición:
 * el número de toma en curso, el temporizador obligatorio de 2 minutos entre
 * tomas y el guardado final del registro en la BD.
 * Sigue el protocolo médico de 3 tomas consecuticas espaciadas, donde la tercera
 * toma es la que se considera válida.
 */
class TomaTensionViewModel (private val dao: TomaTensionDAO) : ViewModel() {

    /**
     * Indica si toca realizar la toma 1, 2 o 3.
     * Se usa "by mutableSatateOf" para que, cuando el número cambie, la pantalla
     * se actualice automáticamente.
     * "private set" impide que se modifique desde fuera de esta clase.
     */
    var tomaActual by mutableStateOf(1)
        private set

    /**
     * Segundos restantes de la cuenta atrás entre tomas.
     * Empieza en 0 y avisa a la pantalla cada vez que cambia.
     */
    var segundosRestantes by mutableStateOf(0)
        private set

    /**
     * Indica si la app está actualmente en período de espera obligatoria.
     */
    var esperandoTiempo by mutableStateOf(false)
        private set

    /**
     * Activa la cuenta atrás obligatoria de 2 minutos entre tomas.
     * Al finalizar, avanza automáticamente a la siguiente toma.
     */
    fun iniciarTemporizador() {
        esperandoTiempo = true
        segundosRestantes = 120 //120 segundos

        //Pátron corrutina en segundo plano para no bloquear la interfaz mientas se cuenta.
        viewModelScope.launch {
            while (segundosRestantes > 0) {
                delay(1000) //Pausa de 1 segundo en cada interación
                segundosRestantes--
            }
            // Cambia el estado a falso porque la cuenta atrás de 2 minutos ya ha terminado
            esperandoTiempo = false
            tomaActual++ //Suma 1 y avanza automaticamente a la siguiente toma.
        }
    }

    /**
     * Guarda en la DB el registro completo de las 3 tomas y resetea el contador
     * para dejar la app lista para la siguiente sesión.
     *
     * @param medicion Registro completo de la sesión, con las 3 tomas incluidas.
     */
    fun guardarMedicion(medicion: TomaTension) {
        viewModelScope.launch {
            // Llama a la base de datos y le ordena insertar la fila con todos los datos recogidos
            dao.insertarToma(medicion)
            /**
             * La sesión actual (3 tomas) ya está guardada por completo. Se reinicia a 1 para que
             * si el usuario inicia otra sesión más tarde, la app empiece d enuevo por la toma 1
             * en esa nueva sesión.
             */
            tomaActual = 1
        }
    }
}