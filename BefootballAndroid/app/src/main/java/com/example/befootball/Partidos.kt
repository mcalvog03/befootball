package com.example.befootball

import java.time.LocalDateTime

data class Partidos(
    var pkPartido: Int,
    var equipoLocal: Equipos,
    var equipoVisitante: Equipos,
    var golesLocal: Int,
    var golesVisitante: Int,
    var estado: String,
    var fecha: String,
    var jornada: Int,
    var liga: Ligas
)
