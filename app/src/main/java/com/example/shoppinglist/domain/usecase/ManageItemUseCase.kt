package com.example.shoppinglist.domain.usecase

import com.example.shoppinglist.domain.ShoppingItem
import com.example.shoppinglist.domain.ShoppingRepository
import javax.inject.Inject

class ManageItemUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend fun addProduct(name: String, quantity: Int) {
        if (name.isBlank()) return
        repository.insertItem(ShoppingItem(name = name, quantity = quantity))
    }

    suspend fun deleteProduct(item: ShoppingItem) {
        repository.deleteItem(item)
    }

    suspend fun toggleBoughtStatus(item: ShoppingItem) {
        repository.updateItem(item.copy(isBought = !item.isBought))
    }

    suspend fun updateProduct(item: ShoppingItem, newName: String, newQuantity: Int) {
        if (newName.isBlank()) return
        repository.updateItem(item.copy(name = newName, quantity = newQuantity))
    }

}
