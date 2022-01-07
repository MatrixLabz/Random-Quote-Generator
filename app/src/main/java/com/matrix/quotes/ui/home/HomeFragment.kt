package com.matrix.quotes.ui.home

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.matrix.quotes.R
import com.matrix.quotes.data.api.ApiConstants
import com.matrix.quotes.data.api.QuoteApiService
import com.matrix.quotes.data.models.Quote
import com.matrix.quotes.data.room.FavoriteQuoteDB
import com.matrix.quotes.data.room.FavoriteQuoteDao
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var cardQuote: CardView
    private lateinit var btnGetQuote: Button
    private lateinit var tvQuoteSetupHome: TextView
    private lateinit var tvQuotePunchlineHome: TextView
    private lateinit var ibFavHome: ImageButton
    private lateinit var pbLoadingHome: ProgressBar
    private lateinit var retrofitClient: Retrofit
    private lateinit var quoteApiService: QuoteApiService
    private lateinit var favoriteQuoteDao: FavoriteQuoteDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get views from Id's
        btnGetQuote = view.findViewById(R.id.btnGetQuote)
        cardQuote = view.findViewById(R.id.cardQuote)
        tvQuoteSetupHome = view.findViewById(R.id.tvQuoteSetupHome)
        tvQuotePunchlineHome = view.findViewById(R.id.tvQuotePunchlineHome)
        ibFavHome = view.findViewById(R.id.ibFavHome)
        pbLoadingHome = view.findViewById(R.id.pbLoadingHome)

        setupRoom()
        setupRetrofit()
        setupListeners()
    }

    private fun setupRoom() {
        favoriteQuoteDao = FavoriteQuoteDB.getDatabase(requireContext()).favoriteQuoteDao()
    }

    private fun setupRetrofit() {
        retrofitClient = Retrofit.Builder()
            .baseUrl(ApiConstants.apiEndpoint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        quoteApiService = retrofitClient.create(QuoteApiService::class.java)
    }

    private fun setupListeners() {
        btnGetQuote.setOnClickListener { getQuoteFromApi() }
    }

    private fun getQuoteFromApi() {
        lifecycleScope.launch {
            // show loading bar
            pbLoadingHome.isVisible = true

            // get Quote
            try {
                val quoteResponse = quoteApiService.getRandomQuote()
                if (quoteResponse.isSuccessful) {
                    val quote: Quote? = quoteResponse.body()
                    quote?.let {
                        cardQuote.isVisible = true
                        tvQuoteSetupHome.text = it.content
                        tvQuotePunchlineHome.text = it.author
                        setIfFavorite(it)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                cardQuote.isVisible = false
            }

            // hide loading bar
            pbLoadingHome.isVisible = false
        }
    }

    private fun setIfFavorite(quote: Quote) {
        lifecycleScope.launch {
            val count = favoriteQuoteDao.doesQuoteExist(quote.id)
            if (count == 0) {
                // Quote not saved
                setIconAsNotSelected()
                setListenerToSaveQuote(quote)
            } else {
                setIconAsSelected()
                setListenerToRemoveQuote(quote)
            }
        }
    }

    private fun setListenerToRemoveQuote(quote: Quote) {
        ibFavHome.setOnClickListener {
            lifecycleScope.launch {
                favoriteQuoteDao.removeQuote(quote.getQuoteEntity())
                setIconAsNotSelected()
                setListenerToSaveQuote(quote)
            }
        }
    }

    private fun setListenerToSaveQuote(quote: Quote) {
        ibFavHome.setOnClickListener {
            lifecycleScope.launch {
                favoriteQuoteDao.saveQuote(quote.getQuoteEntity())
                setIconAsSelected()
                setListenerToRemoveQuote(quote)
            }
        }
    }

    private fun setIconAsSelected() {
        ibFavHome.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_favorite_selected
            )
        )
    }

    private fun setIconAsNotSelected() {
        ibFavHome.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_favorite
            )
        )
    }
}