package com.novandi.books.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandi.core.model.ImgBBResponse
import com.novandi.core.repository.BookRepository
import com.novandi.core.room.BookEntity
import com.novandi.core.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    private val _imageUpload: MutableStateFlow<UiState<ImgBBResponse>> = MutableStateFlow(
        UiState.Loading)
    val imageUpload: StateFlow<UiState<ImgBBResponse>> = _imageUpload

    fun getBook(id: Int): LiveData<BookEntity> = repository.getBookById(id)

    fun updateBook(bookEntity: BookEntity) {
        viewModelScope.launch {
            repository.updateBook(bookEntity)
        }
    }

    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            repository.uploadImage(image = image)
                .catch {
                    _imageUpload.value = UiState.Error(it.message.toString())
                }
                .collect {
                    _imageUpload.value = UiState.Success(it)
                }
        }
    }
}