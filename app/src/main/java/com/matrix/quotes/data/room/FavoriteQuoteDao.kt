package com.matrix.quotes.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteQuoteDao {

    @Insert
    suspend fun saveQuote(quoteEntity: QuoteEntity)

    @Delete
    suspend fun removeQuote(quoteEntity: QuoteEntity)

    @Query("select * from quotes")
    fun getAllSavedQuotes(): List<QuoteEntity>

    @Query("delete from quotes")
    suspend fun deleteAllQuotes()

    @Query("select count(*) from quotes where quoteId=:quoteId")
    suspend fun doesQuoteExist(quoteId: String): Int

}