package com.example.crudarticulos260125.models

import java.io.Serializable

data class Articulo(
    // Obligatorio inicializar todos los valores
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Float = 0F,
) : Serializable
