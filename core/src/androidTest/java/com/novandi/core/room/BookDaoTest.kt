package com.novandi.core.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import com.novandi.core.utils.DummyData
import com.novandi.core.utils.blockingObserve
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@SmallTest
class BookDaoTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: BookDatabase
    private lateinit var bookDao: BookDao

    @Before
    fun setup() {
        hiltRule.inject()
        bookDao = database.bookDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun book_must_exist_after_insert() = runTest {
        val book = DummyData.book
        bookDao.insertBook(book)
        val allBooks = bookDao.getBooks().blockingObserve()
        Truth.assertThat(allBooks).isEqualTo(listOf(book))
    }

    @Test
    fun book_must_exist_when_search_keyword() = runTest {
        val book = DummyData.book
        bookDao.insertBook(book)
        val keyword = "%weeekly%"
        val searchResult = bookDao.getSearchQuery(keyword).blockingObserve()
        Truth.assertThat(searchResult).isEqualTo(listOf(book))
    }

    @Test
    fun book_must_deleted_after_delete() = runTest {
        val book = DummyData.book
        bookDao.insertBook(book)
        bookDao.deleteBook(book)
        val allBooks = bookDao.getBooks().blockingObserve()
        Truth.assertThat(allBooks).isEqualTo(listOf<BookEntity>())
    }
}