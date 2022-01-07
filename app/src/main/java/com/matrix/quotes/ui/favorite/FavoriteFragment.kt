package com.matrix.quotes.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matrix.quotes.R
import com.matrix.quotes.data.room.FavoriteQuoteDB
import com.matrix.quotes.data.room.FavoriteQuoteDao
import com.matrix.quotes.data.room.QuoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var tvEmptyHint: TextView
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var pbLoading: ProgressBar
    private lateinit var rvFavorites: RecyclerView
    private lateinit var favoriteQuoteDao: FavoriteQuoteDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvFavorites = view.findViewById(R.id.rvFavorites)
        pbLoading = view.findViewById(R.id.pbLoading)
        tvEmptyHint = view.findViewById(R.id.tvEmptyHint)

        setupRecyclerView()
        setupRoom()
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter(deleteQuoteAction)
        rvFavorites.apply {
            this.adapter = favoritesAdapter
            this.layoutManager = LinearLayoutManager(context)
            this.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            this.itemAnimator = DefaultItemAnimator()
            this.setHasFixedSize(true)
        }
    }

    private val deleteQuoteAction: (QuoteEntity) -> Unit = { quote ->
        Log.e("favoriteFragment", "delete clicked")
        pbLoading.isVisible = true
        // function that needs to be done when deleting quote of id = quoteId
        lifecycleScope.launch(Dispatchers.IO) {
            favoriteQuoteDao.removeQuote(quote)
        }

        pbLoading.isVisible = false
    }

    private fun setupRoom() {
        favoriteQuoteDao = FavoriteQuoteDB.getDatabase(requireContext()).favoriteQuoteDao()
        getListFromRoomAndDisplay()
    }

    private fun getListFromRoomAndDisplay() {
        pbLoading.isVisible = true

        lifecycleScope.launch {
            val favQuotesList = withContext(Dispatchers.IO) { favoriteQuoteDao.getAllSavedQuotes() }
            favoritesAdapter.submitList(favQuotesList)
            favoritesAdapter.notifyDataSetChanged()
            tvEmptyHint.isVisible = favQuotesList.isEmpty()
        }

        pbLoading.isVisible = false
    }

}