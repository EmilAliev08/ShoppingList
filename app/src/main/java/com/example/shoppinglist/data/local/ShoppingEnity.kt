package com.example.shoppinglist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shoppinglist.domain.ShoppingItem

@Entity(tableName = "shopping_items")
data class ShoppingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Int,
    val isBought: Boolean
)

fun ShoppingEntity.toDomain() = ShoppingItem(id, name, quantity, isBought)
fun ShoppingItem.toEntity() = ShoppingEntity(id, name, quantity, isBought)
