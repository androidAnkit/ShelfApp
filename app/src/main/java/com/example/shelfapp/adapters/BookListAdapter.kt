package com.example.shelfapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shelfapp.Util.Logger
import com.example.shelfapp.Util.Utility
import com.example.shelfapp.databinding.BookListBinding
import com.example.shelfapp.model.BookListData
import com.example.shelfapp.model.BookListDataItem

class BookListAdapter(
    private val booksList: BookListData,
    var onItemClick: (BookListDataItem) -> Unit
) : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BookListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booksData = booksList[position]
        holder.bind(booksData)
        holder.itemView.setOnClickListener { onItemClick(booksData) }
    }

    override fun getItemCount(): Int = booksList.size

    inner class ViewHolder(private val binding: BookListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bookListDataItem: BookListDataItem) {
            Glide.with(binding.root.context)
                .load(bookListDataItem.image)
                .into(binding.bookThumbnailImageView)
            binding.bookTitleTextView.text = bookListDataItem.title
            binding.hitCountTextView.text = "Hits: " + bookListDataItem.hits.toString()
        }
    }
}
