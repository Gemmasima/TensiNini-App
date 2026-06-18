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

class TomaTensionViewModel (private val dao: TomaTensionDAO) : ViewModel() {

    /* Controla si toca la toma 1, 2 o 3. Usa "by mutableStateOf" para que, cuando el número cambie
    ** en el código, la pantalla del móvil se actualice sola visualmente.
    ** El "private set" impide que se pueda modificar este número por error desde fuera de este archivo.*/
    var tomaActual by mutableStateOf(1)
        private set

    // Guarda los segundos que quedan de espera (empieza en 0) y avisa a la pantalla si cambia
    var segundosRestantes by mutableStateOf(0)
        // Impide que se pueda modificar este número desde fuera de este archivo
        private set

    // Guarda si el usuario está esperando activamente (falso al principio) y avisa a la pantalla
    var esperandoTiempo by mutableStateOf(false)
        // Impide que se pueda modificar este número desde fuera de este archivo
        private set

    // Define la función para activar la cuenta atrás obligatoria de 2 minutos
    fun iniciarTemporizador() {
        // Cambia el estado a verdadero para que la pantalla sepa que toca esperar
        esperandoTiempo = true
        // Asigna los 120 segundos obligatorios (que equivalen a los 2 minutos)
        segundosRestantes = 120
        // Abre un hilo de ejecución en segundo plano para que la app no se quede congelada
        viewModelScope.launch {
            // Ejecuta este bucle de código de forma repetitiva mientras queden segundos en el contador
            while (segundosRestantes > 0) {
                // Pausa la ejecución exactamente durante 1000 milisegundos (es decir, 1 segundo)
                delay(1000)
                // Resta un segundo al contador de segundos restantes
                segundosRestantes--
            }
            // Cambia el estado a falso porque la cuenta atrás de 2 minutos ya ha terminado
            esperandoTiempo = false
            // Suma uno al contador de tomas para pasar automáticamente a la siguiente medición
            tomaActual++
        }
    }

    // Define la función para guardar el registro final con los datos en la base de datos
    fun guardarMedicion(medicion: TomaTension) {
        // Abre un hilo de ejecución seguro en segundo plano para operar con la base de datos
        viewModelScope.launch {
            // Llama a la base de datos y le ordena insertar la fila con todos los datos recogidos
            dao.insertarToma(medicion)
            // Resetea el contador al número 1 para dejar la app lista para la siguiente sesión
            tomaActual = 1
        }
    }
}