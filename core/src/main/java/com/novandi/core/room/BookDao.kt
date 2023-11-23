package com.novandi.core.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(books: BookEntity)

    @Query("SELECT * FROM book WHERE isFavorite = 0 ORDER BY id DESC")
    fun getBooks() : LiveData<List<BookEntity>>

    @Query("SELECT * FROM book WHERE id = :id ORDER BY id DESC")
    fun getBookById(id: Int) : LiveData<BookEntity>

    @Query("SELECT * FROM book WHERE id != :id ORDER BY id DESC LIMIT 5")
    fun getOtherBooks(id: Int) : LiveData<List<BookEntity>>

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("SELECT * FROM book WHERE title LIKE :query LIMIT 10")
    fun getSearchQuery(query: String) : LiveData<List<BookEntity>>

    @Query("SELECT * FROM book WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteBooks() : LiveData<List<BookEntity>>

    @Query("UPDATE book SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateBookFavorite(isFavorite: Boolean, id: Int)

    @Update
    suspend fun updateBook(book: BookEntity)
}