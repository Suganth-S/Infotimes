package com.suganth.infotimes.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suganth.infotimes.NewsApplication
import com.suganth.infotimes.models.Article
import com.suganth.infotimes.models.NewsResponse
import com.suganth.infotimes.repository.NewsRepository
import com.suganth.infotimes.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class NewsViewModel(
    /**
     * As we know that we cannot use constructor parameter by
     * default for our own viewModels, if we want to do that
     * we really need that here because we need our news repository in our view model then
     * we need to create what is viewModelproviderFactory to define how our own viewmodel
     * should be created
     */
    app: Application,
    val newsRepository: NewsRepository
    //instead of viewmodel , we are using AndroidViewModel, so that we can use app context,
    // which used to check n/w connectivity
    ): AndroidViewModel(app)
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
        safeBreakingNewsCall(countryCode)
    }

  fun searchNews(searchQuery: String) = viewModelScope.launch{
      safeSearchNewsCall(searchQuery)
    }

    /**
     * If response is success- we get success state , or else we get failure state
     */
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if (response.isSuccessful)
        {
            response?.body()?.let { resultResponse ->
                /**
                 * once the response is successfully received means , we can increase our current page number
                 * so that we can able to load next page after that
                 */
                breakingNewsPage++
                if(breakingNewsResponse == null)
                {
                    breakingNewsResponse = resultResponse
                }else{
                    /**
                     * since old article is a List, we cant change or add more, so
                     * we make it mutable list
                     */
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful)
        {
            response?.body()?.let { response ->
                /**
                 * once the response is successfully received means , we can increase our current page number
                 * so that we can able to load next page after that
                 */
                searchNewsPage++
                if (searchNewsResponse == null)
                {
                    searchNewsResponse = response
                }else{
                    val oldSearchArticles = searchNewsResponse?.articles
                    val newSearchArticles = response.articles
                    oldSearchArticles?.addAll(newSearchArticles)
                }
                return Resource.Success(response) }
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

    private suspend fun safeSearchNewsCall(searchQuery: String)
    {
        searchNews.postValue(Resource.Loading())
        try {
            if(checkInternet()){
                val response = newsRepository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response ))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private suspend fun safeBreakingNewsCall(countryCode: String)
    {
        breakingNews.postValue(Resource.Loading())
        try {
            if(checkInternet()){
                val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response ))
            }else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
                when(t){
                    is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                    else -> breakingNews.postValue(Resource.Error("Conversion Error"))
                }
        }
    }

    /**
     * Used To check network connectivity, dont pass activity context to the newsviewModel  use its context
     * so that our activity gets destroyed, so that we cant use its context since its a bad practice
     * so we are using ApplicationContext, becoz we know that it lives long until app exists, so that we Replace viewmodel
     * with AndroidViewModel which ahs an ApplicationContext
     */
    private fun checkInternet() : Boolean{
        val connectivityManager = getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE)
        as ConnectivityManager

        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M)
        {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE ->true
                    TYPE_ETHERNET ->true
                    else -> false
                }
            }
        }
        return false
    }

}