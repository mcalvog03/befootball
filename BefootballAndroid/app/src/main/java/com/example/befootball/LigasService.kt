package com.example.befootball

import com.example.befootball.model.Ligas
import retrofit2.http.GET

interface LigasService {
    @GET(Constants.PATH_LIGAS)
    suspend fun getLigas(): List<Ligas>
}