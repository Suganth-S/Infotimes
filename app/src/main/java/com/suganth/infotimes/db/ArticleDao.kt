package com.suganth.infotimes.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suganth.infotimes.models.Article

@Dao
interface ArticleDao {

    /**
     * onConflict - it determines what happens if that article we
     * want to insert in a database which is already exist in a database, and in that case
     * we simply want to replace that article, so we define OnConflictStrategy.REPLACE
     * so that with the help we use this function as upsert, in which if the article is not there
     * it will insert ,if an article is there it will update that if there is any changes
     * return type is Long which is the type of ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    /**
     * Since the below function is a network call, why dont we use it as a suspend fun ?
     * becoz it will return a LiveData object which doesn't works with suspend fun
     * Purpose of using LiveData is that whenever the change in Article it will update the changes
     * by using observers
     */
    @Query("SELECT * FROM articles")
    fun getAllArticles() : LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}