package com.gemma.tensinini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Franja horaria en la que se realiza la medición.
 * Solo se contemplan dos momentos del día: Mañana y Noche.
 * La franja se determina una única vez, con la hora de la primera toma,
 * y se mantiene fija durante el resto de la sesión.
 */

enum class Franja {
    MANANA,
    NOCHE
}

/** Estado emocional del paciente en el momento de la medición.
 * Es un dato relevante ya que el estrés o la ansiedad pueden alterar
 * los valores de la tensión arterial.
 */

enum class Emocion {
    TRANQUILA,
    NERVIOSA,
    ENFADADA
}

/** Entidad de Room que representa una sesión completa de medición de tensión arterial.
 * El protocolo médico exige tres tomas consecutivas separadas por 2 minutos de espera,para
 * evitar efecto condicionante (bata blanca, hecho del ponerse el manguito) siendo la tercera toma
 * la que se considera válida. Cada toma guarda su propia hora exacta.
 *
 * @property id Identificador único autogenerado por Room.
 * @property fecha Fecha de la medición (formato String "2026-06-26)
 * @property franja Momento del día en que se realiza la medición.
 * @property emocion Estado emocional del paciente al final de la sesión, para no condicionar.
 * @property hora1 Hora exacta de la primera toma (formato "HH:mm").
 * @property sis1 Presión sistólica de la primera toma (mmHg).
 * @property dia1 Presión diastólica de la primera toma (mmHg).
 * @property pulso1 Pulso de la primera toma (lpm).
 * @property sis2 Presión sistólica de la segunda toma, a los 2 minutos (mmHg).
 * @property dia2 Presión diastólica de la segunda toma (mmHg).
 * @property pulso2 Pulso de la segunda toma (lpm).
 * @property sis3 Presión sistólica de la tercera toma — la válida (mmHg).
 * @property dia3 Presión diastólica de la tercera toma — la válida (mmHg).
 * @property pulso3 Pulso de la tercera toma — la válida (lpm).
 */
@Entity(tableName = "mediciones")
data class TomaTension(
    @PrimaryKey(autoGenerate = true)

    val id: Int=0,
    val fecha: String,
    val franja: Franja,
    val emocion: Emocion,

    //Toma 1 (primera medición de la sesión)
    val hora1: String,
    val sis1: Int,
    val dia1: Int,
    val pulso1: Int,

    //Toma 2 (a los 2 minutos de la toma 1)
    val sis2: Int,
    val dia2: Int,
    val pulso2: Int,

    //Toma 3 (a los 2 minutos de la toma 2 -La válida)
    val sis3: Int,
    val dia3: Int,
    val pulso3: Int,
)