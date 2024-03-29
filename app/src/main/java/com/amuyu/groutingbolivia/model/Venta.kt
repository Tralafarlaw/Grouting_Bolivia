package com.amuyu.groutingbolivia.model

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Exclude

/**
 * Registro de TRansacciones de Ventas
 *  */
data class Venta(
    val numero: String = "",
    val numeroCorrelativo: String = "0",
    val cliente: String? = null,
    val nombre: String? = null,
    val dni: String? = null,
    val fecha: Timestamp = Timestamp.now(),
    val asesor: String = FirebaseAuth.getInstance().currentUser?.displayName?:"userunloged",
    val asesorId: String = FirebaseAuth.getInstance().uid?:"userunloged",
    val items: List<ItemVenta> = listOf(),
    val tipo: Int = 0,
    val descuento: Double = 0.0,
    val observaciones: String = "",
    @get:Exclude
    var id: String = "",
    @get:Exclude
    val mTipo: TipoVenta = when(tipo){
        1 -> TipoVenta.EFECTIVO
        2 -> TipoVenta.CREDITO
        else -> TipoVenta.OTRO
    },
    val acuenta: Double = 0.0,
){
    fun getTotal(): Double {
        var ans  = 0.0
        for (i in items){
            ans += ((i.cantidad * i.precio) - i.descuento)
        }
        ans -= descuento
        return ans
    }
    fun getSaldo(): Double {
        var tot: Double = getTotal()
        var acuent: Double = acuenta
        return tot-acuent
    }
}

data class ItemVenta(
    val producto: String = "",
    val cantidad: Int = 0,
    val precio: Double = 0.0,
    val descuento: Double = 0.0,
    val nombre: String = ""
)

enum class TipoVenta(val i: Int){
    EFECTIVO(1),
    CREDITO(2),
    OTRO(3)
}
