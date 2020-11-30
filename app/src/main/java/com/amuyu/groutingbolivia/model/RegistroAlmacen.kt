package com.amuyu.groutingbolivia.model

/**
 * Data Class de control de transacciones de Almacenes Poner ID de los valores involucrados
 */
data class RegistroAlmacen(
    val producto: String = "",
    val cantidad: Int = 0,
    val origen: String = "",
    val destino: String = "",
    val usuario: String = ""
)