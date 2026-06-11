package com.example.shoppinglist.domain

import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {
    fun getShoppingList(): Flow<List<ShoppingItem>>
    suspend fun insertItem(item: ShoppingItem)
    suspend fun deleteItem(item: ShoppingItem)
    suspend fun updateItem(item: ShoppingItem)
}