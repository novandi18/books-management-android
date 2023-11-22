package com.novandi.books.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandi.core.repository.BookRepository
import com.novandi.core.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {
    val books: LiveData<List<com.novandi.core.room.BookEntity>> = bookRepository.getBooks()
    fun searchQuery(query: String): LiveData<List<com.novandi.core.room.BookEntity>> = bookRepository.getSearchQuery(query)

    val searchHistory: LiveData<List<com.novandi.core.room.SearchEntity>> = searchRepository.getSearchHistory()

    fun saveSearchKeyword(entity: com.novandi.core.room.SearchEntity) {
        viewModelScope.launch {
            searchRepository.saveSearchToHistory(entity)
        }
    }

    fun deleteSearchKeyword(entity: com.novandi.core.room.SearchEntity) {
        viewModelScope.launch {
            searchRepository.deleteSearchToHistory(entity)
        }
    }
}