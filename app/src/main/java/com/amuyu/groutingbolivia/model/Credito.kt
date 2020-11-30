package com.amuyu.groutingbolivia.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

/**
 * Data Class para manejo de los estados de credito
 */
data class Credito(
    val venta:String = "",
    val cliente:String = "",
    val total: Double = 0.0,
    val saldo: Double = 0.0,
    val asesor: String = "",
    val fecha: Timestamp = Timestamp.now(),
    val historial: ArrayList<Pagos> = arrayListOf(),
    @get:Exclude
    var id: String = ""
)
data class Pagos (
    val fecha: Timestamp = Timestamp.now(),
    val cantidad: Double = 0.0
)