package com.novandi.core.repository

import androidx.lifecycle.LiveData
import com.novandi.core.room.SearchDao
import com.novandi.core.room.SearchEntity
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao
) : SearchRepository {
    override fun getSearchHistory(): LiveData<List<SearchEntity>> =
        searchDao.getSearchHistory()

    override suspend fun saveSearchToHistory(entity: SearchEntity) =
        searchDao.insertSearch(entity)

    override suspend fun deleteSearchToHistory(entity: SearchEntity) =
        searchDao.deleteSearchHistory(entity)
}