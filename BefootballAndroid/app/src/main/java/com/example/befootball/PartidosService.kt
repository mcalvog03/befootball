package com.example.befootball

import com.example.befootball.model.Partidos
import retrofit2.http.GET
import retrofit2.http.Path

interface PartidosService {

    @GET(Constants.PATH_PARTIDOS)
    suspend fun getPartidosByLiga(@Path("ligaId") ligaId: Int): List<Partidos>

    @GET(Constants.PATH_JORNADAS)
    suspend fun getPartidosByJornada(@Path("jornada") jornada: Int): List<Partidos>


}
