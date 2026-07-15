package com.gemma.tensinini.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.gemma.tensinini.data.SesionMedicionPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


/**
 * Pantalla principal de medición de tensión arterial.
 *
 * Muestra en la parte superior un historial de las tomas ya registradas
 * en la sesión actual, y en la parte inferior el formulario para introducir
 * los valores de la toma en curso (sistólica, diastólica y pulso).
 *
 * Entre tomas se muestra un temporizador de 2 minutos (persisitido en
 * SesionMedicionPreferences para sobrevivir al cierre de la app) y tras
 * la tecera toma se muestra la pantalla de seleccion de estado emocional.
 *
 * @param onSesionCompletada Callback que se ejecuta cuando el paciente ha completado
 * las 3 tomas y seleccionado su estado emocional.
 */
@Composable
fun PantallaMedicion (
    onSessioCompletada: () -> Unit ={}
) {

    val context = LocalContext.current
    val prefs = remember { SesionMedicionPreferences(context) }
    val scope = rememberCoroutineScope ()

    val tomasRegistradas = remember {mutableStateOf(listOf<TomaResumen>())}
    var tomaActual by remember {mutableStateOf(1)}
    var sistolica by remember {mutableStateOf("")}
    var diastolica by remember {mutableStateOf("")}
    var pulso by remember {mutableStateOf("")}

    var enEspera by remember { mutableStateOf(false) }
    var segundosRestantes by remember { mutableStateOf(0) }
    var mostrarSeleccionEmocion by remember { mutableStateOf(false) }

    //Restaura el estado de la sesión al abrir/reabrir la pantalla
    LaunchedEffect(Unit) {
        val tomaGuardada = prefs.tomaActual.first()
        val timestampFin = prefs.timestampFinEspera.first()

        when {
            tomaGuardada in 1..3 -> {
                tomaActual=tomaGuardada
                if (timestampFin > System.currentTimeMillis()) {
                    enEspera=true
                }
            }
            tomaGuardada == 4 -> {
                mostrarSeleccionEmocion = true
            }
        }
    }

    // Cuenta atras del temporizador de espera entre tomas
    LaunchedEffect(enEspera) {
        if (enEspera) {
            while (true) {
                val timestampFin = prefs.timestampFinEspera.first()
                    val restanteMs = timestampFin - System.currentTimeMillis()
                    if (restanteMs <= 0) {
                        enEspera = false
                        break
                    }
                    segundosRestantes = (restanteMs / 1000).toInt()
                    delay(1000)
                }
        }
    }

    Column (
        modifier= Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text="Sessió de mesura",
            fontSize=20.sp,
            fontWeight= FontWeight.Medium
        )

        Spacer(modifier=Modifier.height(4.dp))

        Text(
            text= "Introdïu els valors de cada mesura amb calma",
            fontSize=14.sp,
            color= MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier=Modifier.height(16.dp))
        HistorialTomas(tomas=tomasRegistradas.value)
        Spacer(modifier=Modifier.height(16.dp))

        if (tomaActual <=3) {
            FormularioToma(
                numeroToma=tomaActual,
                sistolica=sistolica,
                diastolica=diastolica,
                pulso=pulso,
                onSistolicaChange={sistolica=it},
                onDiastolicaChange={diastolica=it},
                onPulsoChange={pulso=it},
                onGuardar={
                    tomasRegistradas.value=tomasRegistradas.value + TomaResumen(
                        numeroToma = tomaActual,
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
 *
 * @param tomas Listas de TomaResumen con los datos de las tomas completadas.
 */
@Composable
fun HistorialTomas (tomas: List<TomaResumen>) {
    Card(
        modifier=Modifier.fillMaxWidth(),
        colors= CardDefaults.cardColors(
            containerColor=MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier=Modifier.padding(12.dp)) {
            Text(
                text="Mesures registrades",
                fontSize=13.sp,
                color=MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight=FontWeight.Medium
            )
            Spacer(modifier=Modifier.height(8.dp))

            if(tomas.isEmpty()) {
                Text(
                    text="Encara no hi ha mesures registrades.",
                    fontSize=13.sp,
                    color=MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                tomas.forEachIndexed { index, toma ->
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    FilaToma(toma = toma)
                }
            }

            repeat(3 - tomas.size) { index ->
                if (tomas.isNotEmpty() || index > 0) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mesura ${tomas.size + index + 1}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "— pendent",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Fila individual dentro del historial que muestra el número de toma y sus
 * valores de sistólica, diástolica y pulso.
 * @param toma Datos de la toma a mostrar.
 */
@Composable
fun FilaToma(toma:TomaResumen) {
    Row(
        modifier=Modifier.fillMaxWidth(),
        horizontalArrangement=Arrangement.SpaceBetween,
        verticalAlignment=Alignment.CenterVertically
    ){
        Text(
            text="Mesura ${toma.numeroToma}",
            fontSize=13.sp,
            fontWeight=FontWeight.Medium,
            color=MaterialTheme.colorScheme.onSurface
        )
        Text(
            text= "${toma.sistolica} / ${toma.diastolica} . ${toma.pulso} ppm",
            fontSize=13.sp,
            color=MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Formulario para introducir los valores de una toma individual.
 * Contiene tres campos númericos (sistólica, diastólica y pulso) y un boton para
 * guardar la toma. El botón solo se activa cuando los tres campos tienen valores
 * num. validos
 * @param numeroToma Número de la toma actual (1,2 o 3) mostrado como título.
 *  * @param sistolica Valor actual del campo sistólica.
 *  * @param diastolica Valor actual del campo diastólica.
 *  * @param pulso Valor actual del campo pulso.
 *  * @param onSistolicaChange Callback al cambiar el campo sistólica.
 *  * @param onDiastolicaChange Callback al cambiar el campo diastólica.
 *  * @param onPulsoChange Callback al cambiar el campo pulso.
 *  * @param onGuardar Callback al pulsar el botón de guardar.
 */
@Composable
fun FormularioToma(
    numeroToma:Int,
    sistolica:String,
    diastolica: String,
    pulso: String,
    onSistolicaChange:(String)->Unit,
    onDiastolicaChange:(String)->Unit,
    onPulsoChange: (String)->Unit,
    onGuardar: ()->Unit
) {
    val camposValidos=sistolica.toIntOrNull() != null &&
            diastolica.toIntOrNull() != null &&
            pulso.toIntOrNull() != null

    Card(modifier=Modifier.fillMaxWidth()) {
        Column(modifier=Modifier.padding(16.dp)) {
            Text(
                text="Mesura $numeroToma",
                fontSize=16.sp,
                fontWeight=FontWeight.Medium
            )
            Spacer(modifier=Modifier.height(12.dp))

            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement=Arrangement.spacedBy(8.dp)
            ){
                OutlinedTextField(
                    value=sistolica,
                    onValueChange=onSistolicaChange,
                    label={Text("Sistólica")},
                    keyboardOptions= KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine=true,
                    modifier=Modifier.weight(1F)
                )
                OutlinedTextField(
                    value=diastolica,
                    onValueChange=onDiastolicaChange,
                    label={Text("Diastólica")},
                    keyboardOptions=KeyboardOptions(keyboardType= KeyboardType.Number),
                    singleLine=true,
                    modifier=Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = pulso,
                    onValueChange = onPulsoChange,
                    label = { Text("Pols") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier=Modifier.height(12.dp))
            Button(
                onClick=onGuardar,
                enabled=camposValidos,
                modifier=Modifier.fillMaxWidth()
            ) {
                Text(text="Desar mesura $numeroToma")
            }
        }
    }
}
/**
 * Modelo de datos ligero usado únicamente para mostrar el resumen
 * de una toma en el historial de la pantalla de medición.
 *
 * No se persiste en base de datos — solo vive en el estado de la UI
 * durante la sesión activa.
 *
 * @param numero Número de la toma (1, 2 o 3).
 * @param sistolica Valor sistólico introducido por el paciente.
 * @param diastolica Valor diastólico introducido por el paciente.
 * @param pulso Valor de pulso introducido por el paciente.
 */
data class TomaResumen(
    val numeroToma: Int,
    val sistolica: String,
    val diastolica: String,
    val pulso: String
)
