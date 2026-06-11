package com.example.shoppinglist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ShoppingEntity::class], version = 1, exportSchema = false)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract val shoppingDao: ShoppingDao
}
