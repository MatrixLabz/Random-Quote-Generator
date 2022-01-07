package com.matrix.quotes.data.models

import com.matrix.quotes.data.room.QuoteEntity
import com.google.gson.annotations.SerializedName

/*

API: https://api.quotable.io/random
Output:
{
  _id: string
  // The quotation text
  content: string
  // The full name of the author
  author: string
  // The `slug` of the quote author
  authorSlug: string
  // The length of quote (number of characters)
  length: number
  // An array of tag names for this quote
  tags: string[]
}
 */

data class Quote(
    @SerializedName("_id")
    val id: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("authorSlug")
    val authorSlug: String,
    @SerializedName("length")
    val length: Int,
    @SerializedName("tags")
    val tags: List<String>
) {
    fun getQuoteEntity(): QuoteEntity {
        return QuoteEntity(id, content, author)
    }
}