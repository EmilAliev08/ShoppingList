package com.example.shoppinglist.data

import com.example.shoppinglist.data.local.ShoppingDao
import com.example.shoppinglist.data.local.toDomain
import com.example.shoppinglist.data.local.toEntity
import com.example.shoppinglist.domain.ShoppingItem
import com.example.shoppinglist.domain.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingRepositoryImpl @Inject constructor(
    private val dao: ShoppingDao
) : ShoppingRepository {

    override fun getShoppingList(): Flow<List<ShoppingItem>> {
        return dao.getShoppingList().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertItem(item: ShoppingItem) {
        dao.insertItem(item.toEntity())
    }

    override suspend fun deleteItem(item: ShoppingItem) {
        dao.deleteItem(item.toEntity())
    }

    override suspend fun updateItem(item: ShoppingItem) {
        dao.updateItem(item.toEntity())
    }
}
