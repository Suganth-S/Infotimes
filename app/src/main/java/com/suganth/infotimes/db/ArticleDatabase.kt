package com.suganth.infotimes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.suganth.infotimes.models.Article
import com.suganth.infotimes.ui.db.Converters

/**
 * database class of room always should be abstract
 * version is used to update our database later on so lets say we want to make some changes in some point
 * then we need to update that version so that room knows that we made some updates to our database
 * and that helps in migrate our own old database to new database
 */
@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        /**
         * the term volatile - the other threats can immediately see when a thread changes the
         * instance so that is really useful
         * LOCK - use this to synchronize that instance, so that we really make sure that there
         * is only a single instance of our database at once
         */
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        /**
         * the invoke is called whenever the instance of database is created
         * by given a LOCK, everything happens inside a fun ,can't be accessed by other
         * threads at the same time, so we realy make sure that we dont set, there's not other
         * thread that sets this instance to something, while we already set it
         */
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK)
        {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "article_db.db"
        ).build()
    }

    /**
     * the last thing to get our database running ,
     * we need to add typeConverter to our database as anotated with typeConverters followd by class file
     */
}