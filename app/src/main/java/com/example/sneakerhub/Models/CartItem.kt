package com.example.sneakerhub

import java.io.Serializable

data class CartItem(
    val shoe_id: String,
    val name: String,
    val price: Double,
    val category_id: String,
    val description: String,
    val quantity: String,
    val image_url: String
) : Serializable

