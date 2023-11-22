package com.novandi.books.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.novandi.core.repository.BookRepository
import com.novandi.core.room.BookEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    bookRepository: BookRepository
) : ViewModel() {
    val favorite: LiveData<List<BookEntity>> = bookRepository.getFavoriteBooks()
}