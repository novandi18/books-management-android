package com.novandi.books.ui.newbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandi.core.model.BookResponse
import com.novandi.core.model.ImgBBResponse
import com.novandi.core.repository.BookRepository
import com.novandi.core.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class NewBookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _imageUpload: MutableStateFlow<UiState<ImgBBResponse>> = MutableStateFlow(
        UiState.Loading)
    val imageUpload: StateFlow<UiState<ImgBBResponse>> = _imageUpload

    private val _result: MutableStateFlow<UiState<BookResponse>> = MutableStateFlow(
        UiState.Loading)
    val result: StateFlow<UiState<BookResponse>> = _result

    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            bookRepository.uploadImage(image = image)
                .catch {
                    _imageUpload.value = UiState.Error(it.message.toString())
                }
                .collect {
                    _imageUpload.value = UiState.Success(it)
                }
        }
    }

    fun insertBook(book: com.novandi.core.room.BookEntity) {
        viewModelScope.launch {
            bookRepository.insertBook(book)
                .catch {
                    _result.value = UiState.Error(it.message.toString())
                }
                .collect {
                    _result.value = UiState.Success(
                        BookResponse(
                            isError = it.isError,
                            message = it.message,
                            books = listOf()
                        )
                    )
                }
        }
    }
}