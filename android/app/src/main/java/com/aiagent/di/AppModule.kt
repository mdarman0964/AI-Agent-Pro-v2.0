package com.aiagent.di

import android.content.Context
import com.aiagent.data.db.AppDatabase
import com.aiagent.network.RetrofitClient
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideProjectDao(database: AppDatabase) = database.projectDao()
    
    @Provides
    @Singleton
    fun provideChatDao(database: AppDatabase) = database.chatDao()
    
    @Provides
    @Singleton
    fun provideApiService() = RetrofitClient.apiService
}
