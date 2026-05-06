package com.example.listify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.listify.data.local.dao.Dao
import com.example.listify.data.local.dao.DetectedPaymentDao
import com.example.listify.data.local.entity.CategoryEntity
import com.example.listify.data.local.entity.ClusterEntity
import com.example.listify.data.local.entity.DetectedPaymentEntity
import com.example.listify.data.local.entity.TransactionEntity

@Database(
    entities =
        [
            ClusterEntity::class,
            CategoryEntity::class,
            TransactionEntity::class,
            DetectedPaymentEntity::class
        ],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): Dao
    abstract fun detectedPaymentDao(): DetectedPaymentDao
}