package com.matrix.quotes.data.api

import com.matrix.quotes.data.models.Quote
import retrofit2.Response
import retrofit2.http.GET

interface QuoteApiService {

    @GET("random")
    suspend fun getRandomQuote(): Response<Quote>

}