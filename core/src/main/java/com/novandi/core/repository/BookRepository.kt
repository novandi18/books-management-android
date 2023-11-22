package com.novandi.core.repository

import androidx.lifecycle.LiveData
import com.novandi.core.model.BookResponse
import com.novandi.core.model.ImgBBResponse
import com.novandi.core.room.BookEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface BookRepository {
    fun getBooks(): LiveData<List<BookEntity>>
    suspend fun uploadImage(image: MultipartBody.Part): Flow<ImgBBResponse>
    suspend fun insertBook(book: BookEntity): Flow<BookResponse>
    fun getBookById(id: Int): LiveData<BookEntity>
    fun getOtherBooks(id: Int) : LiveData<List<BookEntity>>
    suspend fun deleteBook(book: BookEntity)
    fun getSearchQuery(query: String): LiveData<List<BookEntity>>
    fun getFavoriteBooks(): LiveData<List<BookEntity>>
    suspend fun updateBookFavorite(isFavorite: Boolean, id: Int)
}