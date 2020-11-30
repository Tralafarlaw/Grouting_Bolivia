package com.amuyu.groutingbolivia.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Cliente(
    val nombre: String = "",
    val direccion: String = "",
    val nit: String = "",
    val telefono: String = "",
    @get:Exclude
    var id: String = "",
    @get:Exclude
    var zona: ZONAS = ZONAS.ORURO
): Serializable {
    override fun toString(): String {
        return nombre
    }
}
enum class ZONAS{
    ORURO, CENTRO, SUR, EL_ALTO,OTROS
}