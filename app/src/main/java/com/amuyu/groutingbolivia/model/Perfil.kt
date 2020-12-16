package com.amuyu.groutingbolivia.model

data class Perfil(
    val nombre: String  = "",
    val admin:Boolean = false,
    val correo: String = "",
    val dni: String = "",
    val almacen: String = ""
){
    override fun toString() = "CI: $dni\ncorreo: $correo\n${when (almacen) {
        "al1" -> "Almacen El Alto"
        "al2" -> "Almacen Sopocachi"
        else -> "Almacen Zona Sur"
    }}"
}
