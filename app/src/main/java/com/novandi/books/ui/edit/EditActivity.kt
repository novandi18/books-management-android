package com.novandi.books.ui.edit

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.novandi.books.R
import com.novandi.books.databinding.ActivityEditBinding
import com.novandi.books.ui.book.BookActivity
import com.novandi.core.room.BookEntity
import com.novandi.core.state.UiState
import com.novandi.core.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@AndroidEntryPoint
class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private val viewModel: EditViewModel by viewModels()
    private var currentImageUri: Uri? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.setNavigationOnClickListener { finish() }

        val id = intent.extras?.getInt(BookActivity.EXTRA_ID)
        if (id != null) showBook(id)
    }

    private fun showBook(id: Int) {
        viewModel.getBook(id).observe(this@EditActivity) { book ->
            binding.edTitle.setText(book.title)
            binding.edAuthor.setText(book.author)
            loadImageFromUrl(book.image)

            binding.btnImage.setOnClickListener { startGallery() }
            binding.btnImageReset.setOnClickListener { resetImage(book.image) }
            initializeMenu(book)
        }
    }

    private fun loadImageFromUrl(image: String) {
        Glide.with(binding.root).load(image)
            .apply(RequestOptions().error(com.novandi.core.R.drawable.image_error))
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

    private fun initializeMenu(bookEntity: BookEntity) {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_submit -> {
                    prepareToUpdate(bookEntity)
                    false
                }
                else -> false
            }
        }
    }

    private fun prepareToUpdate(book: BookEntity) {
        val title = binding.edTitle.text.toString()
        val author = binding.edAuthor.text.toString()
        var data = BookEntity(
            id = book.id,
            title = title,
            author = author,
            image = book.image,
            isFavorite = book.isFavorite
        )

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this@EditActivity)
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                viewModel.uploadImage(multipartBody)
                viewModel.imageUpload.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading -> loading(true)
                        is UiState.Success -> {
                            loading(false)
                            if (uiState.data.success && uiState.data.data != null) {
                                data = data.copy(image = uiState.data.data!!.image.url)
                                updateBook(data)
                            } else showToast(uiState.data.message.toString())
                        }
                        is UiState.Error -> {
                            loading(false)
                            showToast(uiState.errorMessage)
                        }
                    }
                }
            }
        } ?: updateBook(data)
    }

    private fun updateBook(bookEntity: BookEntity) {
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.updateBook(bookEntity)
        }, 300)
        Toast.makeText(
            this@EditActivity,
            resources.getString(R.string.edit_book_completed),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivImage.setImageURI(it)
            binding.btnImageReset.isEnabled = true
        }
    }

    private fun resetImage(image: String) {
        binding.btnImageReset.isEnabled = false
        loadImageFromUrl(image)
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBarUpload.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnImage.isEnabled = !isLoading
        binding.edTitle.isEnabled = !isLoading
        binding.edAuthor.isEnabled = !isLoading
        binding.topAppBar.menu.findItem(R.id.edit_submit).isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}