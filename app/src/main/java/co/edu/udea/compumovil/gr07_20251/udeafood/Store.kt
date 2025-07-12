package co.edu.udea.compumovil.gr07_20251.udeafood

import java.util.UUID

data class Store (
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val description: String = "",
    val hours: String = "",
    val minPrice: Int = 0,
    val open: Boolean = true,
    val imageResId: Int = 0,
    val imageUrl: String = "",
    val products: MutableList<Product> = mutableListOf(),
    val email: String = "",
    val password: String = ""
)