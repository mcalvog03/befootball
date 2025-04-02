package com.example.befootball

data class Equipos (
    var pkEquipo: Int = 0,
    var nombreEquipo: String? = null,
    var liga: Ligas? = null,
    var estadio: Estadios? = null,
    var escudo: String? = null
)