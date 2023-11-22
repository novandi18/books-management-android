package com.novandi.core.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSearch(search: SearchEntity)

    @Query("SELECT * FROM search_history ORDER BY id DESC")
    fun getSearchHistory() : LiveData<List<SearchEntity>>

    @Delete
    suspend fun deleteSearchHistory(entity: SearchEntity)
}