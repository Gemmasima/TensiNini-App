package com.gemma.tensinini.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gemma.tensinini.dao.TomaTensionDAO
import com.gemma.tensinini.data.TomaTension
import kotlinx.coroutines.launch

/**
 * Pantalla que muestra el historial de mediciones guardadas y permite
 * editar los valores numéricos (sistólica, diastólica, pulso) de cada
 * toma, por si el paciente se equivocó al introducirlos.
 */
@Composable
fun PantallaHistorial(dao: TomaTensionDAO) {
    val scope = rememberCoroutineScope()
    var mediciones by remember { mutableStateOf(listOf<TomaTension>()) }
    var medicionEnEdicion by remember { mutableStateOf<TomaTension?>(null) }

    LaunchedEffect(Unit) {
        mediciones = dao.obtenerTodasLasTomas()
    }

    if (medicionEnEdicion != null) {
        FormularioEdicion(
            medicion = medicionEnEdicion!!,
            onGuardar = { medicionActualizada ->
                scope.launch {
                    dao.actualizarToma(medicionActualizada)
                    mediciones = dao.obtenerTodasLasTomas()
                    medicionEnEdicion = null
                }
            },
            onCancelar = { medicionEnEdicion = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Historial de mesures", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))

            if (mediciones.isEmpty()) {
                Text(text = "Encara no hi ha mesures guardades.")
            } else {
                LazyColumn {
                    items(mediciones) { medicion ->
                        FilaHistorial(
                            medicion = medicion,
                            onEditar = { medicionEnEdicion = medicion }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

/**
 * Fila individual del historial: muestra un resumen de la sesión
 * y un botón para editar sus valores.
 */
@Composable
fun FilaHistorial(medicion: TomaTension, onEditar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${medicion.fecha} — ${medicion.franja}",
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Toma 1: ${medicion.sis1}/${medicion.dia1} · ${medicion.pulso1} ppm")
            Text(text = "Toma 2: ${medicion.sis2}/${medicion.dia2} · ${medicion.pulso2} ppm")
            Text(text = "Toma 3: ${medicion.sis3}/${medicion.dia3} · ${medicion.pulso3} ppm")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onEditar) {
                Text("Editar valors")
            }
        }
    }
}

/**
 * Formulario para editar los valores numéricos (sistólica, diastólica,
 * pulso) de las 3 tomas de una medición ya guardada.
 */
@Composable
fun FormularioEdicion(
    medicion: TomaTension,
    onGuardar: (TomaTension) -> Unit,
    onCancelar: () -> Unit
) {
    var sis1 by remember { mutableStateOf(medicion.sis1.toString()) }
    var dia1 by remember { mutableStateOf(medicion.dia1.toString()) }
    var pulso1 by remember { mutableStateOf(medicion.pulso1.toString()) }

    var sis2 by remember { mutableStateOf(medicion.sis2.toString()) }
    var dia2 by remember { mutableStateOf(medicion.dia2.toString()) }
    var pulso2 by remember { mutableStateOf(medicion.pulso2.toString()) }

    var sis3 by remember { mutableStateOf(medicion.sis3.toString()) }
    var dia3 by remember { mutableStateOf(medicion.dia3.toString()) }
    var pulso3 by remember { mutableStateOf(medicion.pulso3.toString()) }

    val todosValidos = listOf(sis1, dia1, pulso1, sis2, dia2, pulso2, sis3, dia3, pulso3)
        .all { it.toIntOrNull() != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = "Editar mesura — ${medicion.fecha}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(16.dp))

        FilaCampos("Toma 1", sis1, dia1, pulso1, { sis1 = it }, { dia1 = it }, { pulso1 = it })
        Spacer(modifier = Modifier.height(12.dp))
        FilaCampos("Toma 2", sis2, dia2, pulso2, { sis2 = it }, { dia2 = it }, { pulso2 = it })
        Spacer(modifier = Modifier.height(12.dp))
        FilaCampos("Toma 3", sis3, dia3, pulso3, { sis3 = it }, { dia3 = it }, { pulso3 = it })
        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                enabled = todosValidos,
                onClick = {
                    onGuardar(
                        medicion.copy(
                            sis1 = sis1.toInt(), dia1 = dia1.toInt(), pulso1 = pulso1.toInt(),
                            sis2 = sis2.toInt(), dia2 = dia2.toInt(), pulso2 = pulso2.toInt(),
                            sis3 = sis3.toInt(), dia3 = dia3.toInt(), pulso3 = pulso3.toInt()
                        )
                    )
                }
            ) {
                Text("Guardar canvis")
            }
            Button(onClick = onCancelar) {
                Text("Cancel·lar")
            }
        }
    }
}

/**
 * Fila con los 3 campos numéricos (sistólica, diastólica, pulso) de una toma.
 */
@Composable
fun FilaCampos(
    titulo: String,
    sistolica: String,
    diastolica: String,
    pulso: String,
    onSistolicaChange: (String) -> Unit,
    onDiastolicaChange: (String) -> Unit,
    onPulsoChange: (String) -> Unit
) {
    Text(text = titulo, fontWeight = FontWeight.Medium)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = sistolica,
            onValueChange = onSistolicaChange,
            label = { Text("Sistólica") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = diastolica,
            onValueChange = onDiastolicaChange,
            label = { Text("Diastólica") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
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
}