package com.novandi.books.ui.newbook

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.novandi.books.databinding.ActivityNewBookBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@AndroidEntryPoint
class NewBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewBookBinding
    private val viewModel: NewBookViewModel by viewModels()
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
        binding = ActivityNewBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.setNavigationOnClickListener { finish() }

        binding.btnImage.setOnClickListener { startGallery() }
        binding.btnSubmit.setOnClickListener { doSubmit() }
    }

    private fun doSubmit() {
        val title = binding.edTitle.text.toString()
        val author = binding.edAuthor.text.toString()

        currentImageUri?.let { uri ->
            val imageFile = com.novandi.core.utils.uriToFile(uri, this@NewBookActivity)
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )

            if (title.isEmpty() && author.isEmpty()) {
                showToast("Masukkan nama dan author dulu")
            } else {
                lifecycleScope.launch {
                    viewModel.uploadImage(multipartBody)
                    viewModel.imageUpload.collect { uiState ->
                        when (uiState) {
                            is com.novandi.core.state.UiState.Loading -> loading(true)
                            is com.novandi.core.state.UiState.Success -> {
                                loading(false)
                                if (uiState.data.success && uiState.data.data != null) {
                                    val entity = com.novandi.core.room.BookEntity(
                                        title = title,
                                        image = uiState.data.data!!.image.url,
                                        author = author
                                    )
                                    viewModel.insertBook(entity)

                                    viewModel.result.collect { uiStateResult ->
                                        when (uiStateResult) {
                                            is com.novandi.core.state.UiState.Loading -> {}
                                            is com.novandi.core.state.UiState.Success -> {
                                                loading(false)
                                                showToast(uiStateResult.data.message)
                                                finish()
                                            }
                                            is com.novandi.core.state.UiState.Error -> {
                                                loading(false)
                                                showToast(uiStateResult.errorMessage)
                                            }
                                        }
                                    }
                                } else showToast(uiState.data.message.toString())
                            }
                            is com.novandi.core.state.UiState.Error -> {
                                loading(false)
                                showToast(uiState.errorMessage)
                            }
                        }
                    }
                }
            }
        } ?: showToast("Pilih gambar dulu")
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivImage.setImageURI(it)
        }
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnImage.isEnabled = !isLoading
        binding.edTitle.isEnabled = !isLoading
        binding.edAuthor.isEnabled = !isLoading
        binding.btnSubmit.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}