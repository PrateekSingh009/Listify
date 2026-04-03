package com.example.listify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.listify.data.local.entity.CategoryEntity
import com.example.listify.data.local.entity.ClusterEntity
import com.example.listify.data.local.entity.TransactionEntity

@Database(entities = [ClusterEntity::class, CategoryEntity::class, TransactionEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): Dao
}