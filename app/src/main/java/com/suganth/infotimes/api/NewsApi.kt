package com.suganth.infotimes.api

import com.suganth.infotimes.models.NewsResponse
import com.suganth.infotimes.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    /**
     * for getting a breaking news , from the documentation of newsApi.org
     * we need to pass country for getting country wise breaking news,
     * page for pagination, and an apiKey, similarly for searchForNews to search
     */

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "in",
        @Query("pageSize")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("pageSize")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}