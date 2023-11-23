package com.novandi.books.ui.book

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.novandi.books.R
import com.novandi.books.databinding.ActivityBookBinding
import dagger.hilt.android.AndroidEntryPoint
import com.bumptech.glide.request.target.Target
import com.novandi.books.ui.edit.EditActivity
import com.novandi.core.adapter.OtherBookAdapter
import com.novandi.core.room.BookEntity

@AndroidEntryPoint
class BookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookBinding
    private val viewModel: BookViewModel by viewModels()
    private val adapter by lazy {
        OtherBookAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.setNavigationOnClickListener { finish() }

        val data = intent.extras
        if (data != null) {
            showDetail(data.getInt(EXTRA_ID))
            showOtherBooks(data.getInt(EXTRA_ID))
        }
    }

    private fun showDetail(id: Int) {
        binding.ivImage.clipToOutline = true
        viewModel.getBookById(id).observe(this@BookActivity) { book ->
            if (book == null) {
                finish()
            } else {
                initializeMenu(book)
                binding.tvTitle.text = book.title
                binding.tvAuthor.text = book.author
                setFavoriteIcon(book.isFavorite)

                Glide.with(this)
                    .load(book.image)
                    .apply(RequestOptions().error(R.drawable.image_error))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.ivImage)
            }
        }
    }

    private fun initializeMenu(book: BookEntity) {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.book_delete -> {
                    MaterialAlertDialogBuilder(this@BookActivity)
                        .setTitle(resources.getString(R.string.delete_book))
                        .setMessage(String.format(resources.getString(R.string.delete_book_confirm), book.title, book.author))
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            Handler(Looper.getMainLooper()).postDelayed({
                                viewModel.deleteBook(book)
                            }, 300)
                            Toast.makeText(
                                this@BookActivity,
                                resources.getString(R.string.delete_book_completed),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                        .show()
                    false
                }
                R.id.book_edit -> {
                    val intent = Intent(this@BookActivity, EditActivity::class.java)
                    val bundle = Bundle().apply {
                        putInt(EXTRA_ID, book.id)
                    }
                    intent.putExtras(bundle)
                    startActivity(intent)
                    false
                }
                R.id.book_favorite -> {
                    viewModel.updateBookFavorite(!book.isFavorite, book.id)
                    false
                }
                else -> false
            }
        }
    }

    private fun showOtherBooks(id: Int) {
        binding.rvOtherBook.layoutManager = LinearLayoutManager(this@BookActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvOtherBook.adapter = adapter

        viewModel.otherBooks(id).observe(this@BookActivity) { books ->
            adapter.differ.submitList(books)
            binding.vEmpty.root.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE

            adapter.setOnItemClickListener(object : OtherBookAdapter.OnItemClickListener {
                override fun onItemClicked(data: BookEntity) {
                    val intent = Intent(this@BookActivity, BookActivity::class.java)
                    val bundle = Bundle().apply {
                        putInt(EXTRA_ID, data.id)
                    }
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            })
        }
    }

    private fun setFavoriteIcon(isFavorite: Boolean) {
        binding.topAppBar.menu.findItem(R.id.book_favorite).icon =
            AppCompatResources.getDrawable(this@BookActivity, if (isFavorite)
                R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outlined)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}