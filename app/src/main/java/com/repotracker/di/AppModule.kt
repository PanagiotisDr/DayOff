package com.repotracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.work.WorkManager
import com.repotracker.data.local.AppDatabase
import com.repotracker.data.local.UserPreferencesManager
import com.repotracker.data.local.WorkScheduleDao
import com.repotracker.data.local.dataStore
import com.repotracker.data.repository.PreferencesRepositoryImpl
import com.repotracker.data.repository.ScheduleRepositoryImpl
import com.repotracker.domain.repository.PreferencesRepository
import com.repotracker.domain.repository.ScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module για Database και WorkManager dependencies.
 * Παρέχει Room Database, DataStore, και WorkManager.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideWorkScheduleDao(database: AppDatabase): WorkScheduleDao {
        return database.workScheduleDao()
    }
    
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }
    
    @Provides
    @Singleton
    fun provideUserPreferencesManager(
        dataStore: DataStore<Preferences>
    ): UserPreferencesManager {
        return UserPreferencesManager(dataStore)
    }
    
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
}

/**
 * Hilt Module για Repository bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindScheduleRepository(
        impl: ScheduleRepositoryImpl
    ): ScheduleRepository
    
    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository
}
