package com.example.shoppinglist.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items ORDER BY isBought ASC, id DESC")
    fun getShoppingList(): Flow<List<ShoppingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingEntity)

    @Delete
    suspend fun deleteItem(item: ShoppingEntity)

    @Update
    suspend fun updateItem(item: ShoppingEntity)
}
