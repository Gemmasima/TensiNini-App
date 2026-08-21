package com.gemma.tensinini.network

import com.gemma.tensinini.data.MedicionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/** Define las llamadas HTTP disponibles hacia el backend Spring Boot.
 * Retrofit genera automatic. la implementacion de esta interfaz.
 * solo declaro "qué" endpoints hay.
 */

interface ApiService {

    /**Envía la medición al backend, como una carta con los datos dentro.
     * suspend = puede tardar, asi que se ejecuta en 2º plano, para no
     * bloquear la app mientras espera respuesta.
     */
    @POST("mediciones")
    suspend fun guardarMedicion(@Body medicion: MedicionDto): Response<MedicionDto>
}
