package com.novandi.books.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.novandi.books.R
import com.novandi.core.adapter.BookAdapter
import com.novandi.core.adapter.SearchQueryAdapter
import com.novandi.core.adapter.SearchResultAdapter
import com.novandi.books.databinding.ActivityMainBinding
import com.novandi.books.ui.book.BookActivity
import com.novandi.books.ui.favorite.FavoriteActivity
import com.novandi.books.ui.newbook.NewBookActivity
import com.novandi.core.room.BookEntity
import com.novandi.core.room.SearchEntity
import com.novandi.core.utils.GridSpacingItemDecoration
import com.novandi.core.utils.LinearSpacingItemDecoration
import com.novandi.core.utils.SearchQueryEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { BookAdapter() }
    private val viewModel: MainViewModel by viewModels()
    private lateinit var searchQueryAdapter: SearchQueryAdapter
    private lateinit var searchResultAdapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNew.setOnClickListener {
            startActivity(Intent(this@MainActivity, NewBookActivity::class.java))
        }

        showBooks()
        setTopAppBar()
        setSearchView()
        onBack()
        onSwipeRefresh()
    }

    private fun showBooks() {
        binding.rvBook.layoutManager = GridLayoutManager(this@MainActivity, 2)
        binding.rvBook.addItemDecoration(GridSpacingItemDecoration(2, 32))
        binding.rvBook.adapter = adapter

        viewModel.books.observe(this@MainActivity) { books ->
            adapter.differ.submitList(books)
            binding.vEmpty.root.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE

            adapter.setOnItemClickListener(object : BookAdapter.OnItemClickListener {
                override fun onItemClicked(data: BookEntity) {
                    moveToBookPage(data)
                }
            })
        }
    }

    private fun setTopAppBar() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorite -> {
                    startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun setSearchView() {
        binding.rvSearch.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResult.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResult.addItemDecoration(
            LinearSpacingItemDecoration(
                16
            )
        )

        binding.searchView
            .editText
            .addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    searchQueryAdapter = if (s.isNotEmpty()) {
                        SearchQueryAdapter(
                            this@MainActivity,
                            SearchQueryEnum.SEARCH
                        )
                    } else SearchQueryAdapter(
                        this@MainActivity,
                        SearchQueryEnum.HISTORY
                    )
                    binding.rvSearch.adapter = searchQueryAdapter

                    if (s.isNotEmpty()) observeSearchQuery(s.toString(), SearchQueryEnum.SEARCH) else observeSearchHistory()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        binding.searchView.editText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.saveSearchKeyword(
                    SearchEntity(keyword = v.text.toString())
                )
                showSearchResult(v.text.toString())
            }
            false
        }

        binding.searchView.editText.setOnClickListener { v ->
            v.requestFocus()
            binding.searchView.editText.inputType = InputType.TYPE_CLASS_TEXT
        }

        binding.searchView.editText.setOnFocusChangeListener { _, hasFocus ->
            toggleRecyclerViewSearch(hasFocus)
        }
    }

    private fun observeSearchQuery(query: String, searchMode: SearchQueryEnum) {
        viewModel.searchQuery("%$query%").observe(this@MainActivity) { result ->
            if (searchMode == SearchQueryEnum.SEARCH) {
                val data = result.map { book ->
                    SearchEntity(keyword = book.title)
                }
                searchQueryAdapter.differ.submitList(data)
                searchQueryAdapter.setOnItemClickListener(object : SearchQueryAdapter.OnItemClickListener {
                    override fun onItemClicked(data: SearchEntity) {
                        viewModel.saveSearchKeyword(data)
                        showSearchResult(data.keyword)
                        binding.searchView.editText.setText(data.keyword)
                    }
                })
            } else if (searchMode == SearchQueryEnum.RESULT) {
                searchResultAdapter.differ.submitList(result)
                searchResultAdapter.setOnItemClickListener(object : SearchResultAdapter.OnItemClickListener {
                    override fun onItemClicked(data: BookEntity) {
                        moveToBookPage(data)
                    }
                })
            }
        }
    }

    private fun observeSearchHistory() {
        viewModel.searchHistory.observe(this@MainActivity) { result ->
            searchQueryAdapter.differ.submitList(result)
            searchQueryAdapter.setOnItemClickListener(object : SearchQueryAdapter.OnItemClickListener {
                override fun onItemClicked(data: SearchEntity) {
                    viewModel.saveSearchKeyword(data)
                    showSearchResult(data.keyword)
                    binding.searchView.editText.setText(data.keyword)
                }
            })

            searchQueryAdapter.setOnDeleteClickListener(object : SearchQueryAdapter.OnDeleteClickListener {
                override fun onItemClicked(data: SearchEntity) {
                    viewModel.deleteSearchKeyword(data)
                }
            })
        }
    }

    private fun moveToBookPage(data: BookEntity) {
        val intent = Intent(this@MainActivity, BookActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(BookActivity.EXTRA_ID, data.id)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun toggleRecyclerViewSearch(isSearchVisible: Boolean) {
        binding.rvSearch.visibility = if (isSearchVisible) View.VISIBLE else View.GONE
        binding.rvSearchResult.visibility = if (isSearchVisible) View.GONE else View.VISIBLE
    }

    private fun showSearchResult(query: String) {
        binding.searchView.editText.clearFocus()
        val imm: InputMethodManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)

        searchResultAdapter = SearchResultAdapter()
        binding.rvSearchResult.adapter = searchResultAdapter
        observeSearchQuery(query, SearchQueryEnum.RESULT)
        toggleRecyclerViewSearch(false)
    }

    private fun onBack() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val searchView = binding.searchView
                if (searchView.isShowing) {
                    if (searchView.editText.isFocused) {
                        searchView.hide()
                    } else {
                        searchView.editText.setText("")
                        searchView.editText.requestFocus()
                        searchView.editText.inputType = InputType.TYPE_CLASS_TEXT

                    }
                    searchResultAdapter.differ.submitList(listOf())
                    toggleRecyclerViewSearch(true)
                } else finish()
            }
        })
    }

    private fun onSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.rvBook.adapter = null
            binding.rvBook.layoutManager = null
            binding.rvBook.removeItemDecorationAt(0)
            showBooks()
            binding.swipeRefresh.isRefreshing = false
        }
    }
}