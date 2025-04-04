package com.example.befootball

import com.example.befootball.model.Partidos
import retrofit2.http.GET
import retrofit2.http.Path

interface PartidosService {
    @GET(Constants.PATH_PARTIDOS)
    suspend fun getAllPartidos(): List<Partidos>
}
