package com.example.befootball.model

data class Equipos (
    var pkEquipo: Int,
    var nombreEquipo: String,
    var liga: Ligas,
    var estadio: Estadios,
    var escudo: String
)