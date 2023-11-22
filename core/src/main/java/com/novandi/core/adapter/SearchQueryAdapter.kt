package com.novandi.core.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.novandi.core.R
import com.novandi.core.databinding.ItemSearchQueryBinding
import com.novandi.core.room.SearchEntity
import com.novandi.core.utils.SearchQueryEnum

class SearchQueryAdapter(
    private val context: Context,
    private val searchQueryEnum: SearchQueryEnum
) : RecyclerView.Adapter<SearchQueryAdapter.SearchQueryViewHolder>() {
    private lateinit var binding: ItemSearchQueryBinding
    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var onDeleteClickListener: OnDeleteClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchQueryViewHolder {
        binding = ItemSearchQueryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchQueryViewHolder()
    }

    override fun onBindViewHolder(holder: SearchQueryViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class SearchQueryViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SearchEntity) {
            with(binding) {
                binding.btnDelete.visibility = if (searchQueryEnum == SearchQueryEnum.HISTORY)
                    View.VISIBLE else View.GONE
                ivSearchQuery.setImageDrawable(AppCompatResources.getDrawable(context,
                    if (searchQueryEnum == SearchQueryEnum.HISTORY) R.drawable.ic_history else R.drawable.ic_search
                ))
                tvSearchQuery.text = data.keyword

                cvSearchQuery.setOnClickListener {
                    onItemClickListener.onItemClicked(data)
                }

                btnDelete.setOnClickListener {
                    onDeleteClickListener.onItemClicked(data)
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnDeleteClickListener(onDeleteClickListener: OnDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(data: SearchEntity)
    }

    interface OnDeleteClickListener {
        fun onItemClicked(data: SearchEntity)
    }

    private val differCallback = object : DiffUtil.ItemCallback<SearchEntity>() {
        override fun areItemsTheSame(oldItem: SearchEntity, newItem: SearchEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SearchEntity, newItem: SearchEntity): Boolean = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)
}