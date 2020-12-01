package com.amuyu.groutingbolivia.model

data class Perfil(
    val nombre: String  = "",
    val admin:Boolean = false,
    val mail: String = "",
    val dni: String = "",
    val al: String = ""
){
    override fun toString() = "CI: $dni\ncorreo: $mail\n${when (al) {
        "al1" -> "Almacen El Alto"
        "al2" -> "Almacen Sopocachi"
        else -> "Almacen Zona Sur"
    }}"
}
