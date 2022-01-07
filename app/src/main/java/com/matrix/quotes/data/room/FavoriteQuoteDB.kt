package com.matrix.quotes.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QuoteEntity::class], version = 1)
abstract class FavoriteQuoteDB : RoomDatabase() {

    abstract fun favoriteQuoteDao(): FavoriteQuoteDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: FavoriteQuoteDB? = null

        fun getDatabase(context: Context): FavoriteQuoteDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteQuoteDB::class.java,
                    "quotes_db"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}