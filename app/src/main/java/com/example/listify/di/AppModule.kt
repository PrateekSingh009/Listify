package com.example.listify.di

import android.content.Context
import androidx.room.Room
import com.example.listify.data.local.AppDatabase
import com.example.listify.data.local.Dao
import com.example.listify.data.repository.DataRepositoryImpl
import com.example.listify.domain.repository.DataRepository
import com.example.listify.domain.usecase.AddCategoryUseCase
import com.example.listify.domain.usecase.AddTransactionUseCase
import com.example.listify.domain.usecase.GetCategoryUseCase
import com.example.listify.domain.usecase.GetTransactionListUseCase
import com.example.listify.domain.usecase.GetCategoryByIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "listify.db")
            .fallbackToDestructiveMigration() // We'll remove this once done
            .build()

    @Provides
    fun provideDao(db: AppDatabase): Dao = db.dao()

    @Provides
    fun provideDataRepository(dao: Dao): DataRepository = DataRepositoryImpl(dao)

    @Provides
    fun provideAddGroupUseCase(repository : DataRepository) = AddCategoryUseCase(repository)

    @Provides
    fun provideAddItemUseCase(repository : DataRepository) = AddTransactionUseCase(repository)

    @Provides
    fun provideGetGroupUseCase(repository : DataRepository) = GetCategoryUseCase(repository)

    @Provides
    fun provideGetItemListUseCase(repository : DataRepository) = GetTransactionListUseCase(repository)

    @Provides
    fun provideGetGroupByIdUseCase(repository : DataRepository) = GetCategoryByIdUseCase(repository)
}