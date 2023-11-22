package com.novandi.core.di

import com.novandi.core.repository.BookRepository
import com.novandi.core.repository.BookRepositoryImpl
import com.novandi.core.repository.SearchRepository
import com.novandi.core.repository.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [NetworkModule::class, DatabaseModule::class])
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun provideBookRepository(repositoryImpl: BookRepositoryImpl): BookRepository

    @Binds
    fun provideSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository
}