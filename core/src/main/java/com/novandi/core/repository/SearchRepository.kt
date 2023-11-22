package com.novandi.core.repository

import androidx.lifecycle.LiveData
import com.novandi.core.room.SearchEntity

interface SearchRepository {
    fun getSearchHistory(): LiveData<List<SearchEntity>>
    suspend fun saveSearchToHistory(entity: SearchEntity)
    suspend fun deleteSearchToHistory(entity: SearchEntity)
}