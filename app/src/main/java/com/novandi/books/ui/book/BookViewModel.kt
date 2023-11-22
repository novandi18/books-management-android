package com.novandi.books.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandi.core.repository.BookRepository
import com.novandi.core.room.BookEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    fun getBookById(id: Int): LiveData<BookEntity> = bookRepository.getBookById(id)

    fun otherBooks(id: Int): LiveData<List<BookEntity>> = bookRepository.getOtherBooks(id)

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            bookRepository.deleteBook(book)
        }
    }

    fun updateBookFavorite(isFavorite: Boolean, id: Int) {
        viewModelScope.launch {
            bookRepository.updateBookFavorite(isFavorite, id)
        }
    }
}