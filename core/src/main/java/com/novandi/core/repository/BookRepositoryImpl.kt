package com.novandi.core.repository

import androidx.lifecycle.LiveData
import com.novandi.core.api.ApiService
import com.novandi.core.model.BookResponse
import com.novandi.core.model.ImgBBResponse
import com.novandi.core.room.BookDao
import com.novandi.core.room.BookEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.MultipartBody
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val api: ApiService
) : BookRepository {
    override fun getBooks(): LiveData<List<BookEntity>> = bookDao.getBooks()

    override suspend fun uploadImage(image: MultipartBody.Part): Flow<ImgBBResponse> {
        val upload = api.uploadImage(image = image)
        return flowOf(upload)
    }

    override suspend fun insertBook(book: BookEntity): Flow<BookResponse> {
        return try {
            bookDao.insertBook(book)
            flowOf(
                BookResponse(
                    isError = false,
                    message = "Buku berhasil ditambahkan",
                    books = listOf()
                )
            )
        } catch (e: Exception) {
            flowOf(
                BookResponse(
                    isError = true,
                    message = e.message.toString(),
                    books = listOf()
                )
            )
        }
    }

    override fun getBookById(id: Int): LiveData<BookEntity> = bookDao.getBookById(id)

    override fun getOtherBooks(id: Int): LiveData<List<BookEntity>> = bookDao.getOtherBooks(id)

    override suspend fun deleteBook(book: BookEntity) = bookDao.deleteBook(book)

    override fun getSearchQuery(query: String): LiveData<List<BookEntity>> =
        bookDao.getSearchQuery(query)

    override fun getFavoriteBooks(): LiveData<List<BookEntity>> = bookDao.getFavoriteBooks()

    override suspend fun updateBookFavorite(isFavorite: Boolean, id: Int) =
        bookDao.updateBookFavorite(isFavorite, id)
}