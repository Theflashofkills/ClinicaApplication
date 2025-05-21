package com.thiagojunhonma.devhealthy.exame

data class Exame(
    val nomePaciente: String,
    val cpfPaciente: String,
    val fotoUrl: String? = null
)
