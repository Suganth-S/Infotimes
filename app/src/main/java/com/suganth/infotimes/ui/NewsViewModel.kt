package com.suganth.infotimes.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suganth.infotimes.models.Article
import com.suganth.infotimes.models.NewsResponse
import com.suganth.infotimes.repository.NewsRepository
import com.suganth.infotimes.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit

class NewsViewModel(
    /**
     * As we know that we cannot use constructor parameter by
     * default for our own viewModels, if we want to do that
     * we really need that here because we need our news repository in our view model then
     * we need to create what is viewModelproviderFactory to define how our own viewmodel
     * should be created
     */
    val newsRepository: NewsRepository
    ): ViewModel()
{
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    /**
     * we are set pagenumber hers, instead of fragments, if we use fragment means while rotate our device
     * pagenumber will be reset, hence our view model wont destroys so we gave here
     */
    var breakingNewsPage = 1

    /**
     * we are adding pagination to uor response , so that we can able to scroll more
     * even after our first 20 articles and for that the change we need to do in our viewmodel is
     * we need to save current response in news view model, we cant manage this in fragments and if we do that
     * lets say if we load 3 pages and when we rotate our screen , those 3 pages reset and initial page only shown
     * so to avoid that we achieve this in viewModel response
     */
    var breakingNewsResponse : NewsResponse? = null
    var searchNewsResponse : NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        /**
         * before we make a n/w call , we want to emit the loading state to our lifedata because
         * we now know, that we are about to make a n/w call ,so we should emit that loading state
         * so our fragment can handle that
         */
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response ))
    }

  fun searchNews(searchQuery: String) = viewModelScope.launch{
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery,searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    /**
     * If response is success- we get success state , or else we get failure state
     */
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if (response.isSuccessful)
        {
            response?.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful)
        {
            response?.body()?.let { response -> return Resource.Success(response) }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedArticles() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }


}