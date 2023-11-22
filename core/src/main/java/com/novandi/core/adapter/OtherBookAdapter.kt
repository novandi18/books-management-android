package com.novandi.core.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.novandi.core.R
import com.novandi.core.databinding.ItemOtherBookBinding
import com.novandi.core.room.BookEntity

class OtherBookAdapter : RecyclerView.Adapter<OtherBookAdapter.OtherBookViewHolder>() {
    private lateinit var binding: ItemOtherBookBinding
    private lateinit var onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OtherBookViewHolder {
        binding = ItemOtherBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OtherBookViewHolder()
    }

    override fun onBindViewHolder(holder: OtherBookViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class OtherBookViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BookEntity) {
            with(binding) {
                tvTitle.text = data.title
                tvAuthor.text = data.author
                ivFavorite.visibility = if (data.isFavorite) View.VISIBLE else View.GONE

                Glide.with(binding.root).load(data.image)
                    .apply(RequestOptions().error(R.drawable.image_error))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(ivBook)

                cvBook.setOnClickListener {
                    onItemClickListener.onItemClicked(data)
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(data: BookEntity)
    }

    private val differCallback = object : DiffUtil.ItemCallback<BookEntity>() {
        override fun areItemsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)
}