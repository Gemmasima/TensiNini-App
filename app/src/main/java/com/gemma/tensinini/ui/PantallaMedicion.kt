package com.gemma.tensinini.ui

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

/**
 * Pantalla principal de medición de tensión arterial.
 *
 * Muestra en la parte superior un historial de las tomas ya registradas
 * en la sesión actual, y en la parte inferior el formulario para introducir
 * los valores de la toma en curso (sistólica, diastólica y pulso).
 *
 * @param onSesionCompletada Callback que se ejecuta cuando el paciente ha completado
 * las 3 tomas y seleccionado su estado emocional.
 */
@Composable
fun Pantallamedicion (
    onSesionCompletada: () -> Unit ={}
) {
    val tomasRegistradas = remember {mutableStateOf(listOf<TomaResumen>())}
    var tomaActual by remember {mutableStateOf(1)}

    var sistolica by remember {mutableStateOf("")}
    var diastolica by remember {mutableStateOf("")}
    var pulso by remember {mutableStateOf("")}

    Column (
        modifier= Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text="Sesión de medición",
            fontSize=20.sp,
            fontWeight=FontWeight.Medium
        )

        Spacer(modifier=Modifier.height(4.dp))

        Text(
            text= "Introduzca los valores de cada toma con calma",
            fontSize=14.sp,
            color= MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier=Modifier.height(16.dp))
        HistorialTomas(tomas=tomasRegistradas.value)
        Spacer(modifier=Modifier.height(16.dp))

        if (tomaActual <=3) {
            FormularioToma(
                numeroToma=tomaActual
                sistolica=sistolica,
                diastolica=diastolica,
                pulso=pulso,
                onSistolicaChange={sistolica=it},
                onDiastolicaChange={diastolica=it},
                onPulsoChange={pulso=it},
                onGuardar={
                    tomasRegistradas.value=tomasRegistradas.value + TomaResumen(
                        numero=tomaActual,
                        sistolica=sistolica,
                        diastolica=diastolica,
                        pulso=pulso
                    )
                    tomaActual++
                    sistolica=""
                    diastolica=""
                    pulso=""
                }
            )
        }
    }
}

        /**
         * Sección superior de la pantalla que muestra las tomas ya registradas
         * en la sesión actual. Si no hay ninguna toma aún, muestra un mensaaje
         * informativo
         */