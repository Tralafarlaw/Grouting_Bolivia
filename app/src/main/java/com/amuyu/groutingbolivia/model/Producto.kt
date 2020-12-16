package com.amuyu.groutingbolivia.model

import com.google.firebase.firestore.Exclude

data class Producto(
    val categorias: List<String> = listOf(),
    val codigo: String = "",
    val codigodebarras: String = "",
    val descripcion: String = "",
    val descuentoFerreteria: Double = 0.0 ,
    val descuentoObras: Double = 0.0,
    val descuentoOficina: Double = 0.0,
    val nombre: String = "",
    val photourl: String = "",
    val precioFerreteria: Double = 0.0,
    val precioObras: Double = 0.0,
    val precioOficina: Double  = 0.0
){
    @get:Exclude
    var mProductoID: String = ""
}