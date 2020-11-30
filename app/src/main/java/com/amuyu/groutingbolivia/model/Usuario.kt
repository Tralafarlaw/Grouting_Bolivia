package com.amuyu.movil_inv.model

/**
 * Data Class de control de Usuarios
 * TODO añadir Datos extras de la empresa
 * TODO Analizar el estado Deshabilitado si sera por Firebase o DB
 */
data class Usuario (
    val mNombre: String = "",
    val mCI: String = "",
    val Permisos: List<Boolean> = listOf(),
    val Estado: String = ""
)