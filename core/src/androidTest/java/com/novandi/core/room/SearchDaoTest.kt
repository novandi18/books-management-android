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
class SearchDaoTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: BookDatabase
    private lateinit var searchDao: SearchDao

    @Before
    fun setup() {
        hiltRule.inject()
        searchDao = database.searchDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun keyword_must_saved_to_history_after_search() = runTest {
        val search = DummyData.search
        searchDao.insertSearch(search)
        val searchHistory = searchDao.getSearchHistory().blockingObserve()
        Truth.assertThat(searchHistory).isEqualTo(listOf(search))
    }

    @Test
    fun keyword_must_deleted_from_history_after_delete() = runTest {
        val search = DummyData.search
        searchDao.insertSearch(search)
        searchDao.deleteSearchHistory(search)
        val searchHistory = searchDao.getSearchHistory().blockingObserve()
        Truth.assertThat(searchHistory).isEqualTo(listOf<SearchEntity>())
    }
}