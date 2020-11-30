package com.amuyu.groutingbolivia.model

/**
 * Data Class de control de los Almacenes usar para referenciar ya non el nombre sino el ID y asi jalar en Room
 */
data class Almacen (
    val Nombre: String = "",
    val Direccion: String = ""
)
