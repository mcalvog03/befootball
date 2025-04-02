package com.example.befootball

import retrofit2.http.GET

interface PartidosService {
    @GET(Constants.PATH_PARTIDOS)
    suspend fun getReadsPartidos() : List<Partidos>
}