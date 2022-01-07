package com.matrix.quotes.ui.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matrix.quotes.R
import com.matrix.quotes.data.room.QuoteEntity

class FavoritesAdapter(
    private val removeAction: (QuoteEntity) -> Unit
) :
    RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    private var list = emptyList<QuoteEntity>()

    inner class FavoritesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvQuoteContent = itemView.findViewById<TextView>(R.id.tvQuoteContent)!!
        private val tvQuoteAuthor = itemView.findViewById<TextView>(R.id.tvQuoteAuthor)!!
        private val ibFav = itemView.findViewById<ImageButton>(R.id.ibFav)!!

        fun bindData(quoteEntity: QuoteEntity) {
            tvQuoteContent.text = quoteEntity.quoteContent
            tvQuoteAuthor.text = quoteEntity.quoteAuthor
            ibFav.setOnClickListener {
                // Lambda from fragment to remove quote of given id from room
                removeAction(quoteEntity)
                // notify adapter of change
                list = list.filter { it.quoteId!=quoteEntity.quoteId }
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition,list.size)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.quote_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    fun submitList(list: List<QuoteEntity>) {
        this.list = list
    }
}