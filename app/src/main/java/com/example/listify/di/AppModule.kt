package com.example.listify.di

import android.content.Context
import androidx.room.Room
import com.example.listify.data.local.AppDatabase
import com.example.listify.data.local.dao.Dao
import com.example.listify.data.local.dao.DetectedPaymentDao
import com.example.listify.data.repository.DataRepositoryImpl
import com.example.listify.domain.repository.DataRepository
import com.example.listify.domain.usecase.*
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
//            .fallbackToDestructiveMigration()
            .build()


    @Provides
    fun provideDao(db: AppDatabase): Dao = db.dao()

    @Provides
    fun provideDetectedPaymentDao(db: AppDatabase): DetectedPaymentDao =
        db.detectedPaymentDao()

    @Provides
    fun provideDataRepository(dao: Dao,detectedPaymentDao: DetectedPaymentDao): DataRepository = DataRepositoryImpl(dao,detectedPaymentDao)

    // ── Home ──────────────────────────────────────────────────────
    @Provides
    fun provideGetHomeScreenDataUseCase(repository: DataRepository) =
        GetHomeScreenDataUseCase(repository)

    @Provides
    fun provideAddCategoryUseCase(repository: DataRepository) =
        AddCategoryUseCase(repository)

    @Provides
    fun provideAddClusterUseCase(repository: DataRepository) =
        AddClusterUseCase(repository)

    @Provides
    fun provideDeleteCategoryUseCase(repository: DataRepository) =
        DeleteCategoryUseCase(repository)

    // ── List ──────────────────────────────────────────────────────
    @Provides
    fun provideGetCategoryUseCase(repository: DataRepository) =
        GetCategoryUseCase(repository)

    @Provides
    fun provideAddTransactionUseCase(repository: DataRepository) =
        AddTransactionUseCase(repository)

    @Provides
    fun provideGetTransactionListUseCase(repository: DataRepository) =
        GetTransactionListUseCase(repository)

    @Provides
    fun provideGetCategoryByIdUseCase(repository: DataRepository) =
        GetCategoryByIdUseCase(repository)

    @Provides
    fun provideDeleteTransactionUseCase(repository: DataRepository) =
        DeleteTransactionUseCase(repository)

    @Provides
    fun provideUpdateTotalPlannedUseCase(repository: DataRepository) =
        UpdateTotalPlannedUseCase(repository)

    @Provides
    fun provideGetUnprocessedDetectedPaymentsUseCase(repository: DataRepository) =
        GetUnprocessedDetectedPaymentsUseCase(repository)

    @Provides
    fun provideGetProcessedDetectedPaymentsUseCase(repository: DataRepository) =
        GetProcessedDetectedPaymentsUseCase(repository)
}