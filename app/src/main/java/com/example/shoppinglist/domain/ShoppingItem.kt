package com.example.shoppinglist.domain

data class ShoppingItem(
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val isBought: Boolean = false
)
