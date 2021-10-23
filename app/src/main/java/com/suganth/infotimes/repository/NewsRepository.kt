package com.suganth.infotimes.repository

import androidx.room.Query
import com.suganth.infotimes.api.RetrofitInstance
import com.suganth.infotimes.db.ArticleDatabase
import com.suganth.infotimes.models.Article

class NewsRepository(
    var db: ArticleDatabase
) {
    /**
     * network function to get breaking news from API
     */

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.articleDao().upsert(article)

    fun getSavedNews() = db.articleDao().getAllArticles()

    suspend fun delete(article: Article) = db.articleDao().deleteArticle(article)


}