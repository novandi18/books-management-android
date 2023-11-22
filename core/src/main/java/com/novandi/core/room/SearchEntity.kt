package com.novandi.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("search_history")
data class SearchEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("keyword")
    val keyword: String
)