package com.gemma.tensinini.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemma.tensinini.data.TomaTension
import com.gemma.tensinini.dao.TomaTensionDAO
import com.gemma.tensinini.data.Emocion
import com.gemma.tensinini.data.Franja
import com.gemma.tensinini.data.SesionMedicionPreferences
import com.gemma.tensinini.util.ControlHorario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel que controla todo el flujo de una sesión de medición:
 * el número de toma en curso, el temporizador obligatorio de 2 minutos entre
 * tomas y el guardado final del registro en la BD.
 * Sigue el protocolo médico de 3 tomas consecuticas espaciadas, donde la tercera
 * toma es la que se considera válida.
 */
class TomaTensionViewModel (
    private val dao: TomaTensionDAO,
    private val prefs: SesionMedicionPreferences
) : ViewModel() {

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
     * Indica si toca mostrar la pantalla de selección de estado emocional,
     * tras completar las 3 tomas.
     */
    var mostrarSeleccionEmocion by mutableStateOf(false)
        private set

    private var fecha: String =""
    private var franja: Franja? = null
    private var hora1 = ""
    private var sis1 = 0; private var dia1 = 0; private var pulso1 = 0
    private var sis2 = 0; private var dia2 = 0; private var pulso2 =0
    private var sis3 = 0; private var dia3 = 0; private var pulso3 =0

    init {
        viewModelScope.launch {
            val tomaGuardada = prefs.tomaActual.first()
            val timestampFin = prefs.timestampFinEspera.first()
            when {
                tomaGuardada in 1..3 -> {
                    tomaActual = tomaGuardada
                    val restanteMs = timestampFin - System.currentTimeMillis()
                    if (restanteMs > 0) reanudarTemporizador(restanteMs)
                }
                tomaGuardada == 4 -> mostrarSeleccionEmocion = true
            }
        }
    }

    fun registrarToma (sistolica: Int, diastolica: Int, pulso: Int) {

        val horaToma = ControlHorario.obtenerHoraActual ()
        when (tomaActual) {
            1 -> {
                fecha = ControlHorario.obtenerFechaActual()
                franja = ControlHorario.obtenerFranjaActual()
                hora1 = horaToma; sis1 = sistolica; dia1 = diastolica; pulso1= pulso
            }

                2 -> { sis2 = sistolica; dia2 = diastolica; pulso2 = pulso }
                3 -> { sis3 = sistolica; dia3 = diastolica; pulso3 = pulso }

        }
            if (tomaActual < 3) iniciarTemporizador()
                else {
                    mostrarSeleccionEmocion = true
                    viewModelScope.launch { prefs.guardarTomaActual (4)}
                }
        }


    /**
     * Activa la cuenta atrás obligatoria de 2 minutos entre tomas.
     * Al finalizar, avanza automáticamente a la siguiente toma.
     */
    fun iniciarTemporizador() {
        val timestampFin = System.currentTimeMillis() + 120_000L
        viewModelScope.launch {
            prefs.guardarTimestampFinEspera(timestampFin)
            prefs.guardarTomaActual(tomaActual)
        }
        reanudarTemporizador (120_000L)
    }

    private fun reanudarTemporizador(restanteMS: Long) {
        esperandoTiempo = true
        segundosRestantes = (restanteMS / 1000).toInt()
        viewModelScope.launch {
            while (segundosRestantes > 0) {
                delay (1000)
                segundosRestantes--
            }
            esperandoTiempo = false
            tomaActual++
            prefs.guardarTomaActual(tomaActual)
        }
    }

    /**
     * Guarda en la DB el registro completo de las 3 tomas y resetea el contador
     * para dejar la app lista para la siguiente sesión.
     *
     * @param medicion Registro completo de la sesión, con las 3 tomas incluidas.
     */
    fun guardarMedicion(emocion: Emocion) {
        val franjaSession = franja ?: return
        val medicion = TomaTension(
            fecha=fecha,
            franja=franjaSession,
            emocion=emocion,
            hora1 = hora1, sis1 = sis1, dia1 = dia1, pulso1 = pulso1,
            sis2 = sis2, dia2 = dia2, pulso2 = pulso2,
            sis3 = sis3, dia3 = dia3, pulso3 = pulso3
        )
        viewModelScope.launch {
            dao.insertarToma(medicion)
            prefs.limpiarSesion()
            tomaActual=1
            mostrarSeleccionEmocion=false
        }
    }
}