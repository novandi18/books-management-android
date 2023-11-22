package com.novandi.core.model

import com.novandi.core.room.BookEntity


data class BookResponse(
    val isError: Boolean,
    val message: String,
    val books: List<BookEntity>
)