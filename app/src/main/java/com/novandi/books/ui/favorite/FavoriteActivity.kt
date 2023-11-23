package com.novandi.books.ui.favorite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.novandi.books.databinding.ActivityFavoriteBinding
import com.novandi.books.ui.book.BookActivity
import com.novandi.core.adapter.BookAdapter
import com.novandi.core.room.BookEntity
import com.novandi.core.utils.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModels()
    private val adapter by lazy {
        BookAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.setNavigationOnClickListener { finish() }

        showData()
    }

    private fun showData() {
        binding.rvBook.layoutManager = GridLayoutManager(this@FavoriteActivity, 2)
        binding.rvBook.addItemDecoration(GridSpacingItemDecoration(2, 32))
        binding.rvBook.adapter = adapter

        viewModel.favorite.observe(this@FavoriteActivity) { books ->
            adapter.differ.submitList(books)
            binding.vEmpty.root.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE

            adapter.setOnItemClickListener(object : BookAdapter.OnItemClickListener {
                override fun onItemClicked(data: BookEntity) {
                    val intent = Intent(this@FavoriteActivity, BookActivity::class.java)
                    val bundle = Bundle()
                    bundle.putInt("extra_id", data.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            })
        }
    }
}