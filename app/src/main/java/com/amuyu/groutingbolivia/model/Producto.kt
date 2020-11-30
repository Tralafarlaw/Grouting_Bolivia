package com.amuyu.groutingbolivia.model

import com.google.firebase.firestore.Exclude

data class Producto(
    val nombre:        String = "",
    val precio:         String = "0",
    val photourl:        String  = "",
    val codigo: String = "",
    val descuento:       String = "0",
    val descripcion:    String = "",
    val codigodebarras:     String = "",
    @Exclude
    val categorias: List<String> = listOf("categoria A", "categoria B" ,"categoria C" ,"categoria D")
){
    @get:Exclude
    var mProductoID: String = ""
    @get:Exclude
    val mdescuento: Double by lazy {  if(descuento == "") 0.0 else descuento.toDouble()}
    @get:Exclude
    val mprecio: Double by lazy {if(precio == "") 0.0 else precio.toDouble()}

    override fun toString(): String {
        return "$nombre :::: $mprecio ::: $precio "
    }
}
