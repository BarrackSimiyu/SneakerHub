package com.example.sneakerhub.Models

import com.example.sneakerhub.CartItem

data class Order(
    val orderId: Int,  // Changed to non-nullable
    val userId: Int,
    val items: MutableList<CartItem>,
    val totalAmount: Double  // Changed to non-nullable
)

data class Item(
    val shoeId: String,
    val quantity: String
)
