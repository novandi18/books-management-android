package com.novandi.core.utils

import com.novandi.core.room.BookEntity
import com.novandi.core.room.SearchEntity

object DummyData {
    val book = BookEntity(
        id = 1,
        title = "Weeekly Jaehee",
        author = "Mnet",
        image = "https://pm1.aminoapps.com/7658/635b7a9c9f462e543eb37501c287be453e89d7a7r1-1080-1080v2_hq.jpg",
    )

    val search = SearchEntity(id = 1, keyword = "weeekly")

    val updatedBook = BookEntity(
        id = 1,
        title = "Weeekly Jaehee Perform",
        author = "Mnet Group",
        image = "https://i.pinimg.com/originals/81/2a/c4/812ac4332a2ea6c918573293f7db50e1.jpg",
    )
}