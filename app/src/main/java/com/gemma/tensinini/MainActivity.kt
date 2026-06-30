package com.gemma.tensinini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gemma.tensinini.ui.theme.TensininiTheme
import com.gemma.tensinini.util.ControlHorario

/**
 * Actividad principal de la aplicación. Punto de entrada único que aloja
 * el grafo de navegación de Compose y gestiona el acceso a las pantallas
 * según el horario válido del protocolo médico.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TensininiTheme {
                AppNavigation()
            }
        }
    }
}

/**
 * Grafo de navegación principal de la aplicación.
 *
 * Define dos rutas:
 * - [RUTA_INICIO]: pantalla de bienvenida con el botón para iniciar una sesión de medición.
 * - [RUTA_MEDICION]: pantalla de medición de tensión arterial (3 tomas + emoción final).
 *
 * La navegación hacia [RUTA_MEDICION] está bloqueada fuera del horario válido del protocolo
 * médico (MAÑANA 7:00-11:00, NOCHE 20:00-00:00). Si el paciente intenta acceder fuera de
 * esa franja, se muestra un diálogo informativo y no se produce la navegación.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var mostrarDialogoHorario by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = RUTA_INICIO
    ) {
        composable(RUTA_INICIO) {
            PantallaInicio(
                onIniciarMedicion = {
                    if (ControlHorario.esHorarioValido()) {
                        navController.navigate(RUTA_MEDICION)
                    } else {
                        mostrarDialogoHorario = true
                    }
                }
            )
        }

        composable(RUTA_MEDICION) {
            com.gemma.tensinini.ui.PantallaMedicion()
        }
    }

    if (mostrarDialogoHorario) {
        DialogoHorarioInvalido(
            onDismiss = { mostrarDialogoHorario = false }
        )
    }
}

/**
 * Pantalla de inicio con el botón para comenzar una sesión de medición.
 *
 * @param onIniciarMedicion Callback que se ejecuta cuando el paciente pulsa
 *                          el botón de iniciar medición.
 */
@Composable
fun PantallaInicio(onIniciarMedicion: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Button(onClick = onIniciarMedicion) {
            Text(text = "Iniciar mesura")
        }
    }
}

/**
 * Diálogo informativo que se muestra cuando el paciente intenta iniciar una
 * sesión de medición fuera del horario válido del protocolo médico.
 *
 * Informa de las franjas horarias permitidas y bloquea el acceso a la pantalla
 * de medición hasta que el paciente cierre el diálogo.
 *
 * @param onDismiss Callback que se ejecuta cuando el paciente cierra el diálogo.
 */
@Composable
fun DialogoHorarioInvalido(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Fora d'horari") },
        text = {
            Text(
                text = "Les mesures només es poden realitzar en els horaris següents:\n\n" +
                        "• Matí: 7:00 — 11:00\n" +
                        "• Nit: 20:00 — 00:00\n\n" +
                        "Si us plau, torneu-ho a intentar dins d'aquesta franja horària."
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Entesos")
            }
        }
    )
}

/** Ruta de navegación para la pantalla de inicio. */
private const val RUTA_INICIO = "inicio"

/** Ruta de navegación para la pantalla de medición. */
private const val RUTA_MEDICION = "medicion"