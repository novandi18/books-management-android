package com.novandi.core.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BookEntity::class, SearchEntity::class],
    version = 2,
    exportSchema = false
)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun searchDao(): SearchDao
}