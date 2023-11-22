package com.novandi.core.di

import android.content.Context
import androidx.room.Room
import com.novandi.core.room.BookDao
import com.novandi.core.room.BookDatabase
import com.novandi.core.room.SearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): BookDatabase =
        Room.databaseBuilder(
            context, BookDatabase::class.java, "book_db"
        ).build()

    @Provides
    fun provideBookDao(database: BookDatabase): BookDao = database.bookDao()

    @Provides
    fun provideSearchDao(database: BookDatabase): SearchDao = database.searchDao()
}