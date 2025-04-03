package com.example.befootball

import com.example.befootball.model.Ligas
import retrofit2.http.GET
import retrofit2.http.Path

interface LigasService {
    @GET(Constants.PATH_LIGAS)
    suspend fun getLigas(): List<Ligas>

    @GET(Constants.PATH_JORNADAS)
    suspend fun getJornadasByLiga(@Path("ligaId") ligaId: Int): List<Int>
}