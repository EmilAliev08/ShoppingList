package com.example.shoppinglist.domain.usecase

import com.example.shoppinglist.domain.ShoppingItem
import com.example.shoppinglist.domain.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShoppingListUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    operator fun invoke(): Flow<List<ShoppingItem>> = repository.getShoppingList()
}
