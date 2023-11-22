package com.novandi.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("title")
    val title: String,

    @ColumnInfo("image")
    val image: String,

    @ColumnInfo("author")
    val author: String,

    @ColumnInfo("isFavorite")
    val isFavorite: Boolean = false
)